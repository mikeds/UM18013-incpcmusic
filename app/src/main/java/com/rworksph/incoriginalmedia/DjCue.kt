package com.rworksph.incoriginalmedia

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class DjCue : AppCompatActivity(), MediaOnPlayListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dj_cue)
    }

    override fun onMediaPlay(context: Context, data: ArrayList<String>) {

    }
}