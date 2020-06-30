package com.example.original_music.ui.songs

import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.original_music.R
import com.example.original_music.services.ParsePlaylists
import com.example.original_music.ui.songs.adapters.PlaylistsAdapter
import com.example.original_music.ui.songs.adapters.TracksAdapter
import kotlinx.android.synthetic.main.fragment_songs.*
import java.net.URL

class SongsFragment : Fragment() {

    private lateinit var songsViewModel: SongsViewModel

    private val playlistsRecyclerViewAdapter = PlaylistsAdapter(ArrayList())

    private val tracksRecyclerViewAdapter = TracksAdapter(ArrayList())
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        songsViewModel =
            ViewModelProviders.of(this).get(SongsViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_songs, container, false)

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        playlistsRecyclerView.layoutManager = LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false)
        playlistsRecyclerView.adapter = playlistsRecyclerViewAdapter
        tracksRecyclerView.layoutManager = LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false)
        tracksRecyclerView.adapter = tracksRecyclerViewAdapter

        val downloadData = DownloadData(playlistsRecyclerViewAdapter, tracksRecyclerViewAdapter)
        downloadData.execute(
            "https://api-v2.hearthis.at/mikeds?type=playlists&count=20"
        )
    }

    companion object {
        private class DownloadData(var playlistsAdapter: PlaylistsAdapter, var tracksAdapter: TracksAdapter) : AsyncTask<String, Int, ArrayList<String>>() {
            private val TAG = "DownloadData"

            override fun onPostExecute(result: ArrayList<String>) {
                super.onPostExecute(result)
                Log.d(TAG, "onPostExecute: $result")
                val parsePlaylists = ParsePlaylists()
                parsePlaylists.parse(result[0])

                playlistsAdapter.loadNewData(parsePlaylists.playlists)
                tracksAdapter.loadNewData(parsePlaylists.allTracks)
            }

            override fun doInBackground(vararg url: String?): ArrayList<String> {
                var feedArray = ArrayList<String>()
                for (i in url.indices) {
                    Log.d(TAG, "doInBackground: starts with ${url[i]}")
                    feedArray.add(downloadJSON(url[i]))
                    publishProgress(( i.toFloat() / url.size * 100).toInt())
                }
                if (feedArray.size == 0) {
                    Log.e(TAG, "doInBackground: Error downloading")


                    return feedArray
                }

                return feedArray
            }

            override fun onProgressUpdate(vararg values: Int?) {
                super.onProgressUpdate(*values)
                Log.d(TAG, "onProgressUpdate: ${values[0]}")
            }

            private fun downloadJSON(urlPath: String?): String {
                return URL(urlPath).readText()
            }
        }

    }
}