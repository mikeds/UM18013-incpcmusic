package com.rworksph.incoriginalmedia.Services

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import android.support.v4.media.session.MediaSessionCompat
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.rworksph.incoriginalmedia.Data
import com.rworksph.incoriginalmedia.Home
import com.rworksph.incoriginalmedia.R
import com.rworksph.incoriginalmedia.sig
import kotlinx.android.synthetic.main.activity_home.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

@Suppress("DEPRECATION")
class MusicServices : Service(), MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener {
    var Sig = sig.getInstance()
    val mediaPlayer : MediaPlayer = Sig
    val data = Data()
    var requestID = System.currentTimeMillis().toInt()
    val CHANNEL_ID = "incom"
    var notificationManager : NotificationManager? = null

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val streamUrl = if (intent?.getStringExtra("from") == "favorites"){
            intent?.getStringExtra("trackID")
        }else{
            intent?.getStringExtra("streamUrl")
        }
        val from = intent?.getStringExtra("from")


        initMusic(streamUrl.toString(), from.toString())


        creatNotification(intent?.getStringExtra("title").toString() ,R.drawable.ic_baseline_pause_24_d1a538,true)
        return START_STICKY
    }

    override fun onCreate() {

        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        mediaPlayer.apply {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                setWakeMode(application.applicationContext, PowerManager.PARTIAL_WAKE_LOCK)
            }else{
                setAudioStreamType(AudioManager.STREAM_MUSIC)
            }

        }
        mediaPlayer.setOnCompletionListener(this)
        mediaPlayer.setOnPreparedListener(this)

        registerReceiver(receiver, IntentFilter("ACTION"))
    }

    override fun onDestroy() {
        super.onDestroy()
        notificationManager?.cancelAll()
        unregisterReceiver(receiver)
        mediaPlayer.stop()
        mediaPlayer.release()
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        stopSelf()
    }



    override fun onCompletion(p0: MediaPlayer?) {
        Log.e("ca", "naccoll to?")
        nextSong()

    }

    override fun onPrepared(p0: MediaPlayer?) {
        mediaPlayer.start()
        val intent = Intent("home").putExtra("UI","prepared")
        application.applicationContext.sendBroadcast(intent)
    }
    fun initMusic(streamUrl: String, from: String){
        if (mediaPlayer != null){
            if (mediaPlayer.isPlaying){
                mediaPlayer.stop()
                mediaPlayer.reset()
                if (from == "favorites"){
                    playFavorites(streamUrl)
                }else{
                    play(streamUrl)
                }

            }else{
                mediaPlayer.reset()
                if (from == "favorites"){
                    playFavorites(streamUrl)
                }else{
                    play(streamUrl)
                }
            }

        }
    }

    private fun play(streamUrl: String) {
        mediaPlayer.setDataSource(streamUrl)
        mediaPlayer.prepareAsync()
    }

    fun playFavorites(streamUrl: String){
        val dir = File(application.applicationContext.filesDir, "elpaboritos")
        val path = "$dir/$streamUrl.mp3"
        val uri = Uri.parse(path)
        mediaPlayer.setDataSource(application.applicationContext, uri)
        mediaPlayer.prepare()

    }

    fun nextSong(){
        val nowPlaying = data.getNowPlaying(application.applicationContext)
        val trackData = JSONObject(nowPlaying)
        val Data = JSONObject()

        var playlistTracks = JSONArray()
        when(trackData.getString("from")){
            "allsongs" ->{
                playlistTracks = JSONArray(data.getAllSongs(application.applicationContext))
            }
            "favorites" ->{
                playlistTracks = JSONArray(data.getFavorites(application.applicationContext))
            }
            "DjCue" ->{

            }
            else -> {
                playlistTracks = JSONArray(data.getPlaylistTracks(application.applicationContext, trackData.getString("from")))
            }
        }

        val playlistCount = playlistTracks.length()-1
        val trackIndex = trackData.getString("id").toInt()
        var nextIndex : Int = 0
        if (playlistCount <= trackIndex){
            nextIndex = 0
        }else{
            nextIndex = trackIndex+1
        }
        val nextTrack = playlistTracks.getJSONObject(nextIndex)


        val streamUrl = if (trackData.getString("from") == "favorites"){
            nextTrack.getString("id")
        }else{
            nextTrack.getString("stream_url")
        }

        initMusic(streamUrl,trackData.getString("from"))


        Data.put("title", nextTrack.getString("title"))
        Data.put("image", nextTrack.getString("thumb"))
        Data.put("duration", nextTrack.getString("duration"))
        Data.put("waveform_url", nextTrack.getString("waveform_url"))
        Data.put("trackID", nextTrack.getString("id"))
        Data.put("streamUrl", nextTrack.getString("stream_url"))
        Data.put("id", nextIndex)
        Data.put("from", trackData.getString("from"))



        data.nowPlaying(application.applicationContext, Data.toString())
        val intent = Intent("home").putExtra("UI","nextSong")
        application.applicationContext.sendBroadcast(intent)
        creatNotification(nextTrack.getString("title"), R.drawable.ic_baseline_pause_24_d1a538, true)
    }

    fun prevSong(){
        val nowPlaying = data.getNowPlaying(application.applicationContext)
        val trackData = JSONObject(nowPlaying)
        val Data = JSONObject()

        var playlistTracks = JSONArray()
        when(trackData.getString("from")){
            "allsongs" ->{
                playlistTracks = JSONArray(data.getAllSongs(application.applicationContext))
            }
            "favorites" ->{
                playlistTracks = JSONArray(data.getFavorites(application.applicationContext))
            }
            "DjCue" ->{

            }
            else -> {
                playlistTracks = JSONArray(data.getPlaylistTracks(application.applicationContext, trackData.getString("from")))
            }
        }

        val playlistCount = playlistTracks.length()-1
        val trackIndex = trackData.getString("id").toInt()
        var nextIndex : Int = 0
        if (trackIndex <= 0){
            nextIndex = playlistCount-1
        }else{
            nextIndex = trackIndex-1
        }
        val nextTrack = playlistTracks.getJSONObject(nextIndex)

        //Toast.makeText(context, "Track No. $nextIndex", Toast.LENGTH_SHORT).show()
        val streamUrl = if (trackData.getString("from") == "favorites"){
            nextTrack.getString("id")
        }else{
            nextTrack.getString("stream_url")
        }

        initMusic(streamUrl,trackData.getString("from"))


        Data.put("title", nextTrack.getString("title"))
        Data.put("image", nextTrack.getString("thumb"))
        Data.put("duration", nextTrack.getString("duration"))
        Data.put("waveform_url", nextTrack.getString("waveform_url"))
        Data.put("trackID", nextTrack.getString("id"))
        Data.put("streamUrl", nextTrack.getString("stream_url"))
        Data.put("id", nextIndex)
        Data.put("from", trackData.getString("from"))



        data.nowPlaying(application.applicationContext, Data.toString())
        val intent = Intent("home").putExtra("UI","nextSong")
        application.applicationContext.sendBroadcast(intent)
        creatNotification(nextTrack.getString("title"), R.drawable.ic_baseline_pause_24_d1a538, true)
    }
    fun playpause() {
        val nowPlaying = data.getNowPlaying(application.applicationContext)
        val trackData = JSONObject(nowPlaying)
        if (mediaPlayer != null && mediaPlayer.isPlaying) {
            mediaPlayer.pause()
            creatNotification(trackData.getString("title"), R.drawable.ic_baseline_play_arrow_24_d1a538, false)
        } else {
            mediaPlayer.start()
            creatNotification(trackData.getString("title"), R.drawable.ic_baseline_pause_24_d1a538, true)
        }
        val intent = Intent("home").putExtra("UI","updatePlayPause")
        application.applicationContext.sendBroadcast(intent)
    }

    fun updatePlayPause() {
        val nowPlaying = data.getNowPlaying(application.applicationContext)
        val trackData = JSONObject(nowPlaying)
        if (mediaPlayer.isPlaying) {
            creatNotification(trackData.getString("title"), R.drawable.ic_baseline_pause_24_d1a538, true)
        } else {
            creatNotification(trackData.getString("title"), R.drawable.ic_baseline_play_arrow_24_d1a538, false)
        }
    }
    var receiver: BroadcastReceiver = object : BroadcastReceiver() {
        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        override fun onReceive(context: Context?, _intent: Intent) {
            val action = _intent.extras?.getString("actionName")
            //Toast.makeText(this@Home, "yohoooo!!", Toast.LENGTH_LONG).show()
            when (action){
                "pause" ->{
                    playpause()
                }
                "previous" ->{
                    prevSong()
                }
                "next" ->{
                    nextSong()
                }
                "updatePlayPause" ->{
                    updatePlayPause()
                }
            }
        }
    }

    fun creatNotification(Title:String, playButton:Int, isOngoing:Boolean){

        val pauseIntent = Intent(application.applicationContext, NotificationReceiver::class.java).setAction("pause")
        val pausePendingIntent: PendingIntent = PendingIntent.getBroadcast(
            application.applicationContext,requestID, pauseIntent, PendingIntent.FLAG_UPDATE_CURRENT
        )

        val prevIntent = Intent(application.applicationContext, NotificationReceiver::class.java).setAction("previous")
        val prevPendingIntent: PendingIntent = PendingIntent.getBroadcast(
            application.applicationContext,requestID, prevIntent, PendingIntent.FLAG_UPDATE_CURRENT
        )

        val nextIntent = Intent(application.applicationContext, NotificationReceiver::class.java).setAction("next")
        val nextPendingIntent: PendingIntent = PendingIntent.getBroadcast(
            application.applicationContext,requestID, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT
        )

        val intent = Intent(application.applicationContext, Home::class.java)
        val toact: PendingIntent = PendingIntent.getActivity(
            application.applicationContext,requestID, intent, 0)

        val artwork : Bitmap = BitmapFactory.decodeResource(application.applicationContext.resources, R.drawable.applogo)
        val mediaSession : MediaSessionCompat = MediaSessionCompat(application.applicationContext, "tag")

        var builder = NotificationCompat.Builder(application.applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.applogo)
            .setLargeIcon(artwork)
            .setContentTitle(Title)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSubText("is Playing")
            .setOngoing(isOngoing)
            .setContentIntent(toact)

        if (!Title.contains("DJ")){
            builder
                .addAction(R.drawable.ic_baseline_skip_previous_24_d1a538, "Previous", prevPendingIntent) // #0
                .addAction(playButton, "Pause", pausePendingIntent) // #1
                .addAction(R.drawable.ic_baseline_skip_next_24_d1a538, "Next", nextPendingIntent) // #2
                .setStyle(
                    androidx.media.app.NotificationCompat.MediaStyle()
                        .setShowActionsInCompactView(0,1,2)
                    .setMediaSession(mediaSession.sessionToken))
        }else{
            builder.addAction(playButton, "Pause", pausePendingIntent) // #1
                .setStyle(
                    androidx.media.app.NotificationCompat.MediaStyle().setShowActionsInCompactView(0)
                    .setMediaSession(mediaSession.sessionToken))
        }

        val notification : Notification = builder.build()
        startForeground(1, notification)
        with(NotificationManagerCompat.from(application.applicationContext)) {
            // notificationId is a unique int for each notification that you must define
            notify(1, builder.build())

        }



    }
}