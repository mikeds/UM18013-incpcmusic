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
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_dj_cue.*
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_home.bDj
import kotlinx.android.synthetic.main.activity_home.bFave
import kotlinx.android.synthetic.main.activity_home.bHome
import kotlinx.android.synthetic.main.activity_home.bSettings
import kotlinx.android.synthetic.main.activity_playlist.*
import kotlinx.android.synthetic.main.media_controller.*
import org.json.JSONArray
import org.json.JSONObject

class Playlist : AppCompatActivity(),MediaOnPlayListener {

    var trackList = ArrayList<HashMap<String, String>>()
    var init = Init()
    val data = Data()
    var mediaControllerManager = MediaControllerManager()

    //
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_playlist)
        imageView3.setColorFilter(Color.parseColor("#2a2a2a"))
        ivOverlay.setColorFilter(Color.parseColor("#2a2a2a"))

        bottomSheetLayout.setOnProgressListener { progress -> onprog() }
        tvMediaTitle.setOnClickListener { _ -> bottomSheetLayout.toggle()  }

        val PlaylistID = intent.getStringExtra("playlistID")



        init.initTracks(trackList, data.getPlaylistTracks(this, PlaylistID).toString())
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rvSetTracks.layoutManager = layoutManager
        //rvSetTracks.adapter = Playlist_TracksAdapter(this, trackList, intent)

        bottomSheetLayout.visibility = View.GONE

        if (mediaControllerManager.mediaPlayer.isPlaying){
            bottomSheetLayout.visibility = View.VISIBLE
            loadOnPlayData()
        }

        ivSkipBurron.setOnClickListener {nextSong()}
        ivSkipButton2.setOnClickListener {nextSong()}
        ibPreviousButton.setOnClickListener {previousSong()}
        ibRepeatButton.setOnClickListener { repeatSong() }

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
    }

    // will move to fragment later on
    override fun onMediaPlay(context: Context, Data: JSONObject) {
        var mcontext = (context as? Playlist)
        var fade_in = AnimationUtils.loadAnimation(mcontext, R.anim.fade_in)

        data.nowPlaying(context, Data.toString())

        showMediaControls(context)

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
            hideMediaControls()
        }else{
            showMediaControls(this)
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

    fun showMediaControls(context: Context){
        val mcontext = context as Playlist
        mcontext.ivSkipButton2.visibility = View.VISIBLE
        mcontext.ivSkipBurron.visibility = View.VISIBLE
        mcontext.ibRepeatButton.visibility = View.VISIBLE
        mcontext.ibShuffleButton.visibility = View.VISIBLE
        mcontext.seekBar2.visibility = View.VISIBLE
        mcontext.seekBar3.visibility = View.VISIBLE
        mcontext.progressBar.visibility = View.VISIBLE
        mcontext.imageView3.visibility = View.VISIBLE
        mcontext.ivOverlay.visibility = View.VISIBLE
        mcontext.ibPreviousButton.visibility = View.VISIBLE
    }

    fun hideMediaControls(){
        ivSkipButton2.visibility = View.GONE
        ivSkipBurron.visibility = View.GONE
        ibRepeatButton.visibility = View.GONE
        ibShuffleButton.visibility = View.GONE
        seekBar2.visibility = View.GONE
        seekBar3.visibility = View.GONE
        progressBar.visibility = View.GONE
        imageView3.visibility = View.GONE
        ivOverlay.visibility = View.GONE
        ibPreviousButton.visibility = View.GONE
    }

}