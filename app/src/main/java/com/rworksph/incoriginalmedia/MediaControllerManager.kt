package com.rworksph.incoriginalmedia

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import java.io.File

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
    fun playFavorites(trackID: String, context:Context){
        val path = File(context.filesDir, "elpaboritos")
        if (mediaPlayer != null){
            if (mediaPlayer.isPlaying){
                mediaPlayer.stop()
                mediaPlayer.reset()
                initFav(context,"$path/$trackID.mp3")
            }else{
                mediaPlayer.reset()
                initFav(context,"$path/$trackID.mp3")
            }

        }
    }

    fun initFav(context: Context, path:String){
        mediaPlayer = create(context, Uri.parse(path))
        mediaPlayer.start()


       //Log.e("tama ba?", "Eto ba pinili???")
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