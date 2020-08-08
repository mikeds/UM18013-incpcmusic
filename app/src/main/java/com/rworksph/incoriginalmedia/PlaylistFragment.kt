package com.rworksph.incoriginalmedia

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_playlist.view.*
import org.json.JSONObject


class PlaylistFragment:Fragment(),PlaylistData {
    var trackList = ArrayList<HashMap<String, String>>()
    var init = Init()
    val data = Data()
    var intent = JSONObject()
    protected var mView: View? = null
    //var adapter: Playlist_TracksAdapter? = null

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
        mView = view
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun playlistData(context: Context, data: JSONObject) {
        intent = data
    }





}