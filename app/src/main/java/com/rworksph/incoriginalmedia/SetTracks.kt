package com.rworksph.incoriginalmedia

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_set_tracks.*

class SetTracks : AppCompatActivity() {

    private lateinit var itemsAdapter: SetTracksAdapter
    var songList = ArrayList<HashMap<String, String>>()
    var init = inits()
    val data = Data()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_tracks)

        init.initSetTracks(this,songList)
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rvSetTracks.layoutManager = layoutManager
        rvSetTracks.adapter = SetTracksAdapter(this, songList, intent)
    }


}