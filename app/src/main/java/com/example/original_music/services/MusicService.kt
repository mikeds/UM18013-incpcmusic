package com.example.original_music.services

import android.app.Service
import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.IBinder
import android.os.PowerManager
import com.example.original_music.model.Track

class MusicService(var tracks: List<Track>): Service() {

    var player = MediaPlayer()

    var currentTrack: Int = 0

    override fun onCreate() {
        super.onCreate()

        player.setWakeMode(applicationContext, PowerManager.PARTIAL_WAKE_LOCK)
    }

    override fun onBind(p0: Intent?): IBinder? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}
