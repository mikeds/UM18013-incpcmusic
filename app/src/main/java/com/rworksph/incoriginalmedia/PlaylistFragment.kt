package com.rworksph.incoriginalmedia

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.OrientationHelper
import kotlinx.android.synthetic.main.activity_playlist.*
import kotlinx.android.synthetic.main.fragment_home.view.*
import kotlinx.android.synthetic.main.fragment_playlist.view.*
import org.json.JSONObject

class PlaylistFragment:Fragment(),PlaylistData {
    var trackList = ArrayList<HashMap<String, String>>()
    var init = Init()
    val data = Data()
    var mediaControllerManager = MediaControllerManager()
    var intent = JSONObject()

    @SuppressLint("WrongConstant")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        val view  : View = inflater.inflate(R.layout.fragment_playlist, container, false)



        val PlaylistID = intent.getString("playlistID")



        init.initTracks(trackList, data.getPlaylistTracks(activity!!, PlaylistID).toString())
        val layoutManager = LinearLayoutManager(activity!!, LinearLayoutManager.VERTICAL, false)
        view.rvSetTracks.layoutManager = layoutManager
        view.rvSetTracks.adapter = Playlist_TracksAdapter(activity!!, trackList, intent)

        return view
    }


    override fun playlistData(context: Context, data: JSONObject) {

        intent = data
        //Toast.makeText(context, id, Toast.LENGTH_LONG).show()
    }


}