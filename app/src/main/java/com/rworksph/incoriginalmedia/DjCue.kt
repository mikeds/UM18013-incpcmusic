package com.rworksph.incoriginalmedia

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.SeekBar
import androidx.annotation.RequiresApi
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_dj_cue.*
import kotlinx.android.synthetic.main.activity_dj_cue.bDj
import kotlinx.android.synthetic.main.activity_dj_cue.bFave
import kotlinx.android.synthetic.main.activity_dj_cue.bHome
import kotlinx.android.synthetic.main.activity_dj_cue.bSettings
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.media_controller.*
import org.json.JSONArray
import org.json.JSONObject

class DjCue : AppCompatActivity(), MediaOnPlayListener {
    var boolean : Boolean = false
    var mediaControllerManager = MediaControllerManager()
    val data = Data()

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dj_cue)

        imageView3.setColorFilter(Color.parseColor("#2a2a2a"))
        ivOverlay.setColorFilter(Color.parseColor("#2a2a2a"))

        var mediaControllerManager = MediaControllerManager()
        bottomSheetLayout.setOnProgressListener { progress -> onprog() }

        bottomSheetLayout.visibility = View.GONE
        if (mediaControllerManager.mediaPlayer.isPlaying){
            bottomSheetLayout.visibility = View.VISIBLE
            loadOnPlayData()
        }
        ivSkipBurron.setOnClickListener {nextSong()}
        ivSkipButton2.setOnClickListener {nextSong()}
        ibPreviousButton.setOnClickListener {previousSong()}
        ibRepeatButton.setOnClickListener { repeatSong() }

        ivDjPlayPause.setOnClickListener {
            if (textView.text.equals("Now Playing")){
                if (mediaControllerManager.mediaPlayer != null && mediaControllerManager.mediaPlayer.isPlaying) {
                    mediaControllerManager.mediaPlayer.pause()
                    ivDjPlayPause.setImageResource(R.drawable.ic_baseline_play_arrow_24_d1a538)
                } else {
                    mediaControllerManager.mediaPlayer.start()
                    ivDjPlayPause.setImageResource(R.drawable.ic_baseline_pause_24_d1a538)
                }
            }else{
                mediaControllerManager.mediaControllerManager("https://edge.mixlr.com/channel/wycvw")
                ivDjPlayPause.setImageResource(R.drawable.ic_baseline_pause_24_d1a538)
                textView.text = "Now Playing"
                bottomSheetLayout.visibility = View.GONE
                val Data = JSONObject()
                Data.put("title", "DJ's Cue Live Streaming")
                Data.put("image", "https://images.hearthis.at/1/5/9/_/uploads/9341074/image_user/incpc----w800_q70_----1594624193823.jpg")
                Data.put("duration", "0")
                Data.put("id", "0")
                Data.put("from", "DjCue")
                data.nowPlaying(this,Data.toString())

            }

        }


        bHome.setOnClickListener{
            val intent = Intent(applicationContext, Home::class.java)
            startActivity(intent)
        }
        bDj.setOnClickListener{
            val intent = Intent(applicationContext, DjCue::class.java)
            startActivity(intent)
        }
        bSettings.setOnClickListener{

        }
        bFave.setOnClickListener{

        }




        seekBar3.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                if (b){
                    mediaControllerManager.mediaPlayer.seekTo(i)
                    seekBar2.setProgress(i)
                    seekBar3.setProgress(i)
                    progressBar.setProgress(i)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // Do something
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                // Do something
            }
        })

        mediaControllerManager.mediaPlayer.setOnCompletionListener {
            seekBar2.setProgress(0)
            seekBar3.setProgress(0)
            nextSong()
        }

        Thread(Runnable {
            while (mediaControllerManager.mediaPlayer != null) {
                try {
                    if (mediaControllerManager.mediaPlayer.isPlaying()) {
                        val message = Message()
                        message.what = mediaControllerManager.mediaPlayer.getCurrentPosition()
                        handler.sendMessage(message)
                        Thread.sleep(1000)
                    }
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        }).start()
    }

    override fun onMediaPlay(context: Context, Data: JSONObject) {
        var mcontext = (context as? DjCue)
        var fade_in = AnimationUtils.loadAnimation(mcontext, R.anim.fade_in)

        data.nowPlaying(context, Data.toString())


        mcontext?.ivPlayPauseBurron?.setImageResource(R.drawable.ic_baseline_pause_24_d1a538)
        mcontext?.ivPlayButton?.setImageResource(R.drawable.ic_baseline_pause_24_d1a538)

        mcontext?.tvMediaTitle?.setText(Data.getString("title"))
        mcontext?.tvMediaControllerHeaderTitle?.setText(Data.getString("title"))
        mcontext?.seekBar2?.max = (Data.getString("duration").toInt()*1000)
        mcontext?.seekBar3?.max = (Data.getString("duration").toInt()*1000)
        mcontext?.progressBar?.max = (Data.getString("duration").toInt()*1000)

        //Toast.makeText(context, array[1], Toast.LENGTH_LONG).show()
        Picasso.get()
            .load(Data.getString("image").toString())
            .resize(300, 300)
            .centerCrop()
            .into(mcontext?.ivMediaAlbum)
        Picasso.get()
            .load(Data.getString("image").toString())
            .resize(300, 300)
            .centerCrop()
            .into(mcontext?.ivMediaControllerHeaderThumb)
        mcontext?.bottomSheetLayout?.visibility = View.VISIBLE
        mcontext?.bottomSheetLayout?.startAnimation(fade_in)
    }

    @SuppressLint("HandlerLeak")
    private val handler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            seekBar2.setProgress(msg.what)
            seekBar3.setProgress(msg.what)
            progressBar.setProgress(msg.what)
        }
    }



    fun onprog(){
        if (bottomSheetLayout.isExpended()){
            MediaControllerExpanded.visibility = View.VISIBLE
            MediaControllerCollapse.visibility = View.GONE
            bottomSheetLayout.setBackgroundColor(Color.parseColor("#2a2a2a"))
        }else{
            MediaControllerExpanded.visibility = View.GONE
            MediaControllerCollapse.visibility = View.VISIBLE
            bottomSheetLayout.setBackgroundColor(Color.parseColor("#cc2a2a2a"))
        }
    }

    fun loadOnPlayData(){
        val Data = data.getNowPlaying(this)

        val trackData = JSONObject(Data)

        if (trackData.getString("title").equals("DJ's Cue Live Streaming")){
            bottomSheetLayout.visibility = View.GONE
        }

        ivPlayPauseBurron?.setImageResource(R.drawable.ic_baseline_pause_24_d1a538)
        ivPlayButton?.setImageResource(R.drawable.ic_baseline_pause_24_d1a538)
        //Toast.makeText(this, newData!![0]+","+newData[1]+","+newData[2], Toast.LENGTH_LONG).show()

        tvMediaTitle.setText(trackData.getString("title"))
        tvMediaControllerHeaderTitle.setText(trackData.getString("title"))
        Picasso.get()
            .load(trackData.getString("image"))
            .resize(300, 300)
            .centerCrop()
            .into(ivMediaAlbum)
        Picasso.get()
            .load(trackData.getString("image"))
            .resize(300, 300)
            .centerCrop()
            .into(ivMediaControllerHeaderThumb)
        seekBar2.max = ((trackData.getString("duration").toInt())*1000)
        seekBar3.max = ((trackData.getString("duration").toInt())*1000)
        progressBar?.max = ((trackData.getString("duration").toInt())*1000)
    }

    fun playpause(v: View) {
        if (mediaControllerManager.mediaPlayer != null && mediaControllerManager.mediaPlayer.isPlaying) {
            mediaControllerManager.mediaPlayer.pause()
            ivPlayPauseBurron.setImageResource(R.drawable.ic_baseline_play_arrow_24_d1a538)
            ivPlayButton.setImageResource(R.drawable.ic_baseline_play_arrow_24_d1a538)
        } else {
            mediaControllerManager.mediaPlayer.start()
            ivPlayPauseBurron.setImageResource(R.drawable.ic_baseline_pause_24_d1a538)
            ivPlayButton.setImageResource(R.drawable.ic_baseline_pause_24_d1a538)
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun nextSong(){
        val nowPlaying = data.getNowPlaying(this)
        val trackData = JSONObject(nowPlaying)
        val Data = JSONObject()

        if (trackData.getString("from").equals("allsongs")){
            val playlistTracks = JSONArray(data.getAllSongs(this))
            val playlistCount = playlistTracks.length()-1
            val trackIndex = trackData.getString("id").toInt()
            var nextIndex : Int = 0
            if (playlistCount <= trackIndex){
                nextIndex = 0
            }else{
                nextIndex = trackIndex+1
            }
            val nextTrack = playlistTracks.getJSONObject(nextIndex)
            mediaControllerManager.mediaControllerManager(nextTrack.getString("stream_url"))

            Data.put("title", nextTrack.getString("title"))
            Data.put("image", nextTrack.getString("thumb"))
            Data.put("duration", nextTrack.getString("duration"))
            Data.put("id", nextIndex)
            Data.put("from", trackData.getString("from"))
            onMediaPlay(this, Data)
        }else{
            val playlistTracks = JSONArray(data.getPlaylistTracks(this, trackData.getString("from")))
            val playlistCount = playlistTracks.length()-1
            val trackIndex = trackData.getString("id").toInt()
            var nextIndex : Int = 0
            if (playlistCount <= trackIndex){
                nextIndex = 0
            }else{
                nextIndex = trackIndex+1
            }
            val nextTrack = playlistTracks.getJSONObject(nextIndex)
            mediaControllerManager.mediaControllerManager(nextTrack.getString("stream_url"))

            Data.put("title", nextTrack.getString("title"))
            Data.put("image", nextTrack.getString("thumb"))
            Data.put("duration", nextTrack.getString("duration"))
            Data.put("id", nextIndex)
            Data.put("from", trackData.getString("from"))
            onMediaPlay(this, Data)
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun previousSong(){
        val nowPlaying = data.getNowPlaying(this)
        val trackData = JSONObject(nowPlaying)
        val Data = JSONObject()

        if (trackData.getString("from").equals("allsongs")){
            val playlistTracks = JSONArray(data.getAllSongs(this))
            val playlistCount = playlistTracks.length()
            val trackIndex = trackData.getString("id").toInt()
            var nextIndex : Int = 0
            if (trackIndex <= 0){
                nextIndex = playlistCount-1
            }else{
                nextIndex = trackIndex-1
            }
            val nextTrack = playlistTracks.getJSONObject(nextIndex)
            mediaControllerManager.mediaControllerManager(nextTrack.getString("stream_url"))

            Data.put("title", nextTrack.getString("title"))
            Data.put("image", nextTrack.getString("thumb"))
            Data.put("duration", nextTrack.getString("duration"))
            Data.put("id", nextIndex)
            Data.put("from", trackData.getString("from"))
            onMediaPlay(this, Data)
        }else{
            val playlistTracks = JSONArray(data.getPlaylistTracks(this, trackData.getString("from")))
            val playlistCount = playlistTracks.length()
            val trackIndex = trackData.getString("id").toInt()
            var nextIndex : Int = 0
            if (trackIndex <= 0){
                nextIndex = playlistCount-1
            }else{
                nextIndex = trackIndex-1
            }
            val nextTrack = playlistTracks.getJSONObject(nextIndex)
            mediaControllerManager.mediaControllerManager(nextTrack.getString("stream_url"))

            Data.put("title", nextTrack.getString("title"))
            Data.put("image", nextTrack.getString("thumb"))
            Data.put("duration", nextTrack.getString("duration"))
            Data.put("id", nextIndex)
            Data.put("from", trackData.getString("from"))
            onMediaPlay(this, Data)
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun repeatSong(){
        val nowPlaying = data.getNowPlaying(this)
        val trackData = JSONObject(nowPlaying)
        val Data = JSONObject()
        mediaControllerManager.mediaControllerManager(trackData.getString("streamUrl"))
        Data.put("title", trackData.getString("title"))
        Data.put("image", trackData.getString("thumb"))
        Data.put("duration", trackData.getString("duration"))
        Data.put("id", trackData.getString("id"))
        Data.put("from", trackData.getString("from"))
        onMediaPlay(this, Data)
    }
}