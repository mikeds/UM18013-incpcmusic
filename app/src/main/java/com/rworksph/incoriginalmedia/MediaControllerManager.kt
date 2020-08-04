package com.rworksph.incoriginalmedia

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Build
import androidx.annotation.RequiresApi

class MediaControllerManager() : MediaPlayer() {
    var Sig = sig.getInstance()
    var mediaPlayer : MediaPlayer = Sig


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun mediaControllerManager(steamUrl:String){
        if (mediaPlayer != null){
            if (mediaPlayer.isPlaying){
                mediaPlayer.stop()
                mediaPlayer.reset()
                init(steamUrl)
            }else{
                mediaPlayer.reset()
                init(steamUrl)
            }

        }

    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun init(url:String){
        mediaPlayer.apply{
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )
            setDataSource(url)
            prepareAsync()
        }

        mediaPlayer.setOnPreparedListener { mp -> mp.start() }
    }







}