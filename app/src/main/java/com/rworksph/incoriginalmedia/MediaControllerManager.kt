package com.rworksph.incoriginalmedia

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import java.io.File

@Suppress("DEPRECATION")
class MediaControllerManager {
    var Sig = sig.getInstance()
    var mediaPlayer : MediaPlayer = Sig

}