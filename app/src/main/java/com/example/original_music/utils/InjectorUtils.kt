package com.example.original_music.utils

import android.app.Application
import android.content.ComponentName
import android.content.Context
import com.example.android.original_music.common.MusicServiceConnection
import com.example.android.original_music.media.MusicService
import com.example.original_music.MainActivityViewModel

object InjectorUtils {
    private fun provideMusicServiceConnection(context: Context): MusicServiceConnection {
        return MusicServiceConnection.getInstance(context,
            ComponentName(context, MusicService::class.java)
        )
    }

    fun provideMainActivityViewModel(context: Context): MainActivityViewModel.Factory {
        val applicationContext = context.applicationContext
        val musicServiceConnection = provideMusicServiceConnection(applicationContext)
        return MainActivityViewModel.Factory(musicServiceConnection)
    }

    fun provideNowPlayingFragmentViewModel(context: Context)
            : NowPlayingFragmentViewModel.Factory {
        val applicationContext = context.applicationContext
        val musicServiceConnection = provideMusicServiceConnection(applicationContext)
        return NowPlayingFragmentViewModel.Factory(
            applicationContext as Application, musicServiceConnection)
    }
}