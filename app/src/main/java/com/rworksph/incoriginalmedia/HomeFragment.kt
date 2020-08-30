package com.rworksph.incoriginalmedia

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.OrientationHelper
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.view.*
import org.json.JSONObject


class HomeFragment : Fragment(R.layout.fragment_home) {

    internal var homePlaylistsList: MutableList<Home_Playlists> = ArrayList()
    var TracksList = ArrayList<HashMap<String, String>>()
    val data = Data()
    val init = Init()

    @SuppressLint("WrongConstant")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        val view  : View = inflater.inflate(R.layout.fragment_home, container, false)

        init.initPlaylists(activity!!, homePlaylistsList)
        init.initTracks(TracksList, data.getAllSongs(activity!!).toString())
        if (data.getTheme(activity!!) != ""){
            val color = JSONObject(data.getTheme(activity!!))
            view.llAccent.setBackgroundColor(Color.parseColor(color.getString("colorAccent")))
            view.llTab.setBackgroundColor(Color.parseColor(color.getString("backgroundColor")))
            view.textView2.setTextColor(Color.parseColor(color.getString("colorAccent")))
            view.svContent.setBackgroundColor(Color.parseColor(color.getString("backgroundColor")))
        }

        view.view_pager.adapter = Home_PlaylistAdapter(activity!!, homePlaylistsList)
        view.rvHome.layoutManager = LinearLayoutManager(activity!!, OrientationHelper.HORIZONTAL, false)
        view.rvHome.adapter = Home_TracksAdapter(activity!!, TracksList)



        return view
    }


}