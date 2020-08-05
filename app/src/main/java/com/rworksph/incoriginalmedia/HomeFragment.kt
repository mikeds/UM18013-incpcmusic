package com.rworksph.incoriginalmedia

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
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

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


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


        view.view_pager.adapter = Home_PlaylistAdapter(activity!!, homePlaylistsList)
        view.rvHome.layoutManager = LinearLayoutManager(activity!!, OrientationHelper.HORIZONTAL, false)
        view.rvHome.adapter = Home_TracksAdapter(activity!!, TracksList)



        return view
    }


}