package com.rworksph.incoriginalmedia

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_favorite.view.*
import kotlinx.android.synthetic.main.fragment_playlist.view.*
import org.json.JSONObject

class FavoriteFragment:Fragment() {
    var trackList = ArrayList<HashMap<String, String>>()
    var init = Init()
    val data = Data()
    var mediaControllerManager = MediaControllerManager()
    var intent = JSONObject()
    //var adapter: Playlist_TracksAdapter? = null

    @SuppressLint("WrongConstant")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        val view  : View = inflater.inflate(R.layout.fragment_favorite, container, false)
       // val PlaylistID = intent.getString("playlistID")
        if (data.getFavorites(activity!!) != ""){
            trackList.clear()
            init.initTracks(trackList, data.getFavorites(activity!!).toString())
            val layoutManager = LinearLayoutManager(activity!!, LinearLayoutManager.VERTICAL, false)
            view.rvFav.layoutManager = layoutManager
            view.rvFav.adapter = FavoritesAdapter(activity!!, trackList)
        }
        view.refreshlayout.setOnRefreshListener {
            if (data.getFavorites(activity!!) != ""){
                trackList.clear()
                init.initTracks(trackList, data.getFavorites(activity!!).toString())
                val layoutManager = LinearLayoutManager(activity!!, LinearLayoutManager.VERTICAL, false)
                view.rvFav.layoutManager = layoutManager
                view.rvFav.adapter = FavoritesAdapter(activity!!, trackList)
            }
            view.refreshlayout.isRefreshing = false
        }

        return view
    }
}