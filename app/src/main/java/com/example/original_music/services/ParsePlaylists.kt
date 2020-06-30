package com.example.original_music.services

import android.os.AsyncTask
import android.util.Log
import com.example.original_music.model.Playlist
import com.example.original_music.model.Track
import org.json.JSONArray
import org.json.JSONObject
import java.net.URL

class ParsePlaylists {
    private val TAG = "ParsePlaylists"
    val playlists = ArrayList<Playlist>()
    val allTracks = ArrayList<Track>()
    fun parse(json: String): Boolean {
        var playlistsJsonArray = JSONArray(json)
        Log.d(TAG, "ParsePlaylists: ${playlistsJsonArray.length()}")
        var currentPlaylist = Playlist()
        for(i in 0 until playlistsJsonArray.length()) {
            var playlistJsonObject = playlistsJsonArray.getJSONObject(i)
            val downloadTracks = DownloadTracks()
            var trackList = downloadTracks.execute("https://api-v2.hearthis.at/set/${playlistJsonObject.get("permalink")}/").get()
            allTracks.addAll(trackList)
            currentPlaylist.image = playlistJsonObject.get("artwork_url").toString()
            currentPlaylist.title = playlistJsonObject.get("title").toString()
            currentPlaylist.id = playlistJsonObject.get("id").toString()
            currentPlaylist.tracks = trackList
            playlists.add(currentPlaylist)
            currentPlaylist = Playlist()
            Log.d(TAG, "${trackList}")
            Log.d(TAG, "${trackList.size}")

        }

        return true
    }

    companion object {
        private class DownloadTracks: AsyncTask<String, Int, ArrayList<Track>>() {
            private val TAG="DownloadTracks"

            override fun onPostExecute(result: ArrayList<Track>) {
                Log.d(TAG, "DownloadTracks: onPostExecute")
                Log.d(TAG, "$result")
            }

            override fun doInBackground(vararg url: String?): ArrayList<Track> {
                Log.d(TAG, "DownloadTracks: doInBackground ${url[0]}")

                val trackFeed = JSONArray(downloadJSON(url[0]))
                val trackArrayList = ArrayList<Track>()
                var currentTrack = Track()
                for(i in 0 until trackFeed.length()) {
                    var trackJsonObject = trackFeed.getJSONObject(i)
                    currentTrack.title = trackJsonObject.get("title").toString()
                    currentTrack.image = trackJsonObject.get("artwork_url").toString()
                    currentTrack.audioStream = trackJsonObject.get("stream_url").toString()
                    currentTrack.waveform = trackJsonObject.get("waveform_data").toString()
                    currentTrack.id = trackJsonObject.get("permalink").toString()
                    trackArrayList.add(currentTrack)
                    currentTrack = Track()
                }
                return trackArrayList
            }

            private fun downloadJSON(urlPath: String?): String {
                return URL(urlPath).readText()
            }
        }
    }
}