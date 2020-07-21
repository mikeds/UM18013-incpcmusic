package com.rworksph.incoriginalmedia

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import java.util.zip.Inflater

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
        mediaPlayer.setOnPreparedListener{
            mediaPlayer.start()
        }
    }

    fun play(){
        if (mediaPlayer != null && mediaPlayer.isPlaying){
            mediaPlayer.pause()
        }else
            mediaPlayer.start()
    }






}