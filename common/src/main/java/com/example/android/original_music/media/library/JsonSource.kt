package com.example.android.original_music.media.library

import android.content.Context
import android.net.Uri
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.android.original_music.media.extensions.*
import com.example.original_music.media.R
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.URL
import java.util.concurrent.TimeUnit
import android.util.Log

class JsonSource(context: Context, private val source: Uri) : AbstractMusicSource() {
    private var catalog: List<MediaMetadataCompat> = emptyList()
    private val glide: RequestManager

    init {
        state = STATE_INITIALIZING
        glide = Glide.with(context)
    }

    override fun iterator(): Iterator<MediaMetadataCompat> = catalog.iterator()

    override suspend fun load() {
        updateCatalog(source)?.let { updatedCatalog ->
            catalog = updatedCatalog
            state = STATE_INITIALIZED

        } ?: run {
            catalog = emptyList()
            state = STATE_ERROR
        }
    }

    /**
     * Function to connect to a remote URI and download/process the JSON file that corresponds to
     * [MediaMetadataCompat] objects.
     */
    private suspend fun updateCatalog(catalogUri: Uri): List<MediaMetadataCompat>? {
        return withContext(Dispatchers.IO) {
            val playlistCat = try {
                downloadPlaylistJson(catalogUri).toList()
            } catch(ioException: IOException) {
                return@withContext null
            }

            val allTracks = ArrayList<JsonMusic>()
            val playlistIterator = playlistCat.iterator()
            playlistIterator.forEach{playlist ->
                val currentPlaylistTracks = downloadMusicJson(Uri.parse(playlist.uri))
                currentPlaylistTracks.map {music ->
                    music.album = playlist.title
                    music.totalTrackCount = playlist.track_count
                    music.trackNumber = (currentPlaylistTracks.indexOf(music) + 1).toLong()
                }
                playlistCat[playlistCat.indexOf(playlist)].tracks = currentPlaylistTracks
                allTracks.addAll(currentPlaylistTracks)
            }


            // Get the base URI to fix up relative references later.
            val baseUri = catalogUri.toString().removeSuffix(catalogUri.lastPathSegment ?: "")
            allTracks.map { song ->

                Log.d(TAG, song.toString())
                // The JSON may have paths that are relative to the source of the JSON
                // itself. We need to fix them up here to turn them into absolute paths.
                catalogUri.scheme?.let { scheme ->
                    if(!song.uri.startsWith(scheme)) {
                        song.uri = baseUri + song.uri
                    }
                    if(!song.thumb.startsWith(scheme)) {
                        song.thumb = baseUri + song.thumb
                    }
                }

                // Block on downloading artwork
                val artFile = glide.applyDefaultRequestOptions(glideOptions)
                    .downloadOnly()
                    .load(song.thumb)
                    .submit(NOTIFICATION_LARGE_ICON_SIZE, NOTIFICATION_LARGE_ICON_SIZE)
                    .get()

                val artUri = artFile.asAlbumArtContentUri()


                android.support.v4.media.MediaMetadataCompat.Builder()
                    .from(song)
                    .apply {
                        displayIconUri = artUri.toString() // Used by ExoPlayer and Notification
                        albumArtUri = artUri.toString()
                    }
                    .build()

            }.toList()
        }
    }

    /**
     * Attempts to download a catalog from a given Uri.
     *
     * @param catalogUri URI to attempt to download the catalog form.
     * @return The catalog downloaded, or an empty catalog if an error occurred.
     */
    @Throws(IOException::class)
    private fun downloadPlaylistJson(catalogUri: Uri): Array<JsonPlaylist> {
        val catalogConn = URL(catalogUri.toString())
        val reader = BufferedReader(InputStreamReader(catalogConn.openStream()))
        return Gson().fromJson(reader, Array<JsonPlaylist>::class.java)
    }

    @Throws(IOException::class)
    private fun downloadMusicJson(playlistUri: Uri): Array<JsonMusic> {
        val playlistConn = URL(playlistUri.toString())
        val reader = BufferedReader(InputStreamReader(playlistConn.openStream()))
        return Gson().fromJson(reader, Array<JsonMusic>::class.java)
    }
}

/**
 * Extension method for [MediaMetadataCompat.Builder] to set the fields from
 * our JSON constructed object (to make the code a bit easier to see).
 */
fun MediaMetadataCompat.Builder.from(jsonMusic: JsonMusic): MediaMetadataCompat.Builder {
    // The duration from the JSON is given in seconds, but the rest of the code works in
    // milliseconds. Here's where we convert to the proper units.
    val durationMs = TimeUnit.SECONDS.toMillis(jsonMusic.duration)

    id = jsonMusic.id
    title = jsonMusic.title
    album = jsonMusic.album
    duration = durationMs
    genre = jsonMusic.genre
    mediaUri = jsonMusic.stream_url
    albumArtUri = jsonMusic.artwork_url
    trackNumber = jsonMusic.trackNumber
    trackCount = jsonMusic.totalTrackCount
    flag = MediaBrowserCompat.MediaItem.FLAG_PLAYABLE

    // To make things easier for *displaying* these, set the display properties as well.
    displayTitle = jsonMusic.title
    displaySubtitle = jsonMusic.permalink
    displayDescription = jsonMusic.album
    displayIconUri = jsonMusic.artwork_url

    // Add downloadStatus to force the creation of an "extras" bundle in the resulting
    // MediaMetadataCompat object. This is needed to send accurate metadata to the
    // media session during updates.
    downloadStatus = MediaDescriptionCompat.STATUS_NOT_DOWNLOADED

    // Allow it to be used in the typical builder style.
    return this
}

/**
 * Wrapper object for our JSON in order to be processed easily by GSON.
 */
class JsonPlaylist {
    var id: String = ""
    var title: String = ""
    var description: String = ""
    var uri: String = ""
    var thumb: String = ""
    var track_count: Long = 0
    var artwork_url: String = ""

    var tracks: Array<JsonMusic> = emptyArray()
}

/**
 * An individual piece of music included in our JSON catalog.
 * The format from the server is as specified:
 * ```
 *     [
 *     {
 *          "id": id of song
 *          "duration": duration of song
 *          "permalink": name? of song
 *          "genre": genre of song
 *          "title": Title of song
 *          "uri": link of song for JSON values
 *          "thumb": thumbnail image
 *          "artwork_url": artwork image
 *          "artwork_url_retina": higher resolution of artwork image
 *          "background_url": background size of artwork image
 *          "waveform_data": waveform information in .js format
 *          "waveform_url": waveform image
 *          "stream_url": full song url
 *          "preview_url": shorter version of song for preview
 *     },
 *     ]
 * ```
 *
 * `source` and `image` can be provided in either relative or
 * absolute paths. For example:
 * ``
 *     "source" : "https://www.example.com/music/ode_to_joy.mp3",
 *     "image" : "ode_to_joy.jpg"
 * ``
 *
 * The `source` specifies the full URI to download the piece of music from, but
 * `image` will be fetched relative to the path of the JSON file itself. This means
 * that if the JSON was at "https://www.example.com/json/music.json" then the image would be found
 * at "https://www.example.com/json/ode_to_joy.jpg".
 */
@Suppress("unused")
class JsonMusic {
    var id: String = ""
    var duration: Long = -1
    var permalink: String = ""
    var genre: String = ""
    var title: String = ""
    var uri: String = ""
    var thumb: String = ""
    var artwork_url: String = ""
    var artwork_url_retina: String = ""
    var background_url: String = ""
    var waveform_data: String = ""
    var waveform_url: String = ""
    var stream_url: String = ""
    var preview_url: String = ""

    // updated through playlist
    var album: String = ""
    var trackNumber: Long = 0
    var totalTrackCount: Long = 0

    override fun toString(): String {
        return this.id + " " + this.duration + " " + this.permalink + " \n" +
                this.genre + " " + this.title + " " + this.uri + " \n" +
                this.uri + " \n" + this.thumb + " \n" + this.album + " \n" +
                this.artwork_url + " \n" +
                this.artwork_url_retina + " \n" +
                this.background_url + " \n" +
                this.waveform_data + " \n" +
                this.waveform_url + " \n" +
                this.stream_url + " \n" +
                this.preview_url + " \n" +
                this.trackNumber + " " + this.totalTrackCount
    }
}

private const val NOTIFICATION_LARGE_ICON_SIZE = 144 // px

private val glideOptions = RequestOptions()
    .fallback(R.drawable.default_art)
    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)

private val TAG = "JsonSource"