package com.rworksph.incoriginalmedia

import android.media.MediaPlayer

class sig {
    companion object{
        private var instance = MediaPlayer()
        fun getInstance(): MediaPlayer {
            if (instance == null){
                instance = MediaPlayer()
            }
            return instance
        }
    }
}