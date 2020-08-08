package com.rworksph.incoriginalmedia

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.SeekBar
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_home.*
import org.json.JSONArray
import org.json.JSONObject
import kotlin.random.Random

class Home : AppCompatActivity(), MediaOnPlayListener {
    internal var homePlaylistsList: MutableList<Home_Playlists> = ArrayList()
    var TracksList = ArrayList<HashMap<String, String>>()
    val data = Data()
    val init = Init()
    var mediaControllerManager = MediaControllerManager()
    val context:Context = this

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("WrongConstant")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val homeFragment = HomeFragment()
        val djFragment = DjFragment()
        val favoriteFragment = FavoriteFragment()
        val settingsFragment = SettingsFragment()

        setCurrentFragment(homeFragment)
        val bottomsheetbehavior = BottomSheetBehavior.from(bottomsheet)

        bottomsheet.visibility = View.GONE

        tvMediaTitle.setOnClickListener {
            bottomsheetbehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }

        val bottomSheetCallback = object : BottomSheetBehavior.BottomSheetCallback() {

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        MediaControllerCollapse.visibility = View.VISIBLE
                        MediaControllerExpanded.visibility = View.GONE
                    }
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        MediaControllerCollapse.visibility = View.GONE
                        MediaControllerExpanded.visibility = View.VISIBLE
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                // Do something for slide offset
            }
        }
        bottomsheetbehavior.addBottomSheetCallback(bottomSheetCallback)


        ivSkipBurron.setOnClickListener {nextSong()}
        ivSkipButton2.setOnClickListener {nextSong()}
        ibPreviousButton.setOnClickListener {previousSong()}
        ibRepeatButton.setOnClickListener { repeatSong() }
        ibShuffleButton.setOnClickListener { shufflePlaylist() }

        imageView3.setColorFilter(Color.parseColor("#2a2a2a"))
        ivOverlay.setColorFilter(Color.parseColor("#2a2a2a"))


        /*bHome.setOnClickListener{
            setCurrentFragment(homeFragment)
        }
        bDj.setOnClickListener{
            setCurrentFragment(djFragment)
        }
        bSettings.setOnClickListener{
            setCurrentFragment(settingsFragment)
        }
        bFave.setOnClickListener{
            setCurrentFragment(favoriteFragment)
        }*/

        bottomNav.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.navHome-> {
                    setCurrentFragment(homeFragment)
                    return@setOnNavigationItemSelectedListener true
                }

                R.id.navDJ-> {
                    setCurrentFragment(djFragment)
                    return@setOnNavigationItemSelectedListener true
                }

                R.id.navFavorites-> {
                    setCurrentFragment(favoriteFragment)
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.navSettings-> {
                    setCurrentFragment(settingsFragment)
                    return@setOnNavigationItemSelectedListener true
                }

            }
            false

        }
        seekBar3.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                if (b) {
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

    @SuppressLint("HandlerLeak")
    private val handler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            seekBar2.setProgress(msg.what)
            seekBar3.setProgress(msg.what)
            progressBar.setProgress(msg.what)
        }
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

    override fun onMediaPlay(context: Context, Data: JSONObject) {
        var mcontext = (context as? Home)
        var fade_in = AnimationUtils.loadAnimation(mcontext, R.anim.fade_in)

        data.nowPlaying(context, Data.toString())

        if (Data.getString("from").equals("DjCue")){
            hideMediaControls(context)
        }else{
            showMediaControls(context)
        }


        mcontext?.ivPlayPauseBurron?.setImageResource(R.drawable.ic_baseline_pause_24_d1a538)
        mcontext?.ivPlayButton?.setImageResource(R.drawable.ic_baseline_pause_24_d1a538)

        mcontext?.tvMediaTitle?.setText(Data.getString("title"))
        mcontext?.tvMediaControllerHeaderTitle?.setText(Data.getString("title"))
        mcontext?.seekBar2?.max = (Data.getString("duration").toInt() * 1000)
        mcontext?.seekBar3?.max = (Data.getString("duration").toInt() * 1000)
        mcontext?.progressBar?.max = (Data.getString("duration").toInt() * 1000)

        //Toast.makeText(context, array[1], Toast.LENGTH_LONG).show()
        Picasso.get()
            .load(Data.getString("image"))
            .resize(300, 300)
            .centerCrop()
            .into(mcontext?.ivMediaAlbum)
        Picasso.get()
            .load(Data.getString("image"))
            .resize(300, 300)
            .centerCrop()
            .into(mcontext?.ivMediaControllerHeaderThumb)
        mcontext?.bottomsheet?.visibility = View.VISIBLE
        mcontext?.bottomsheet?.startAnimation(fade_in)
    }


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun nextSong(){
        val nowPlaying = data.getNowPlaying(this)
        val trackData = JSONObject(nowPlaying)
        val Data = JSONObject()

        var playlistTracks = JSONArray()
        when(trackData.getString("from")){
            "allsongs" ->{
                playlistTracks = JSONArray(data.getAllSongs(this))
            }
            "favorites" ->{
                playlistTracks = JSONArray(data.getFavorites(this))
            }
            else -> {
                playlistTracks = JSONArray(data.getPlaylistTracks(this, trackData.getString("from")))
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
        mediaControllerManager.mediaControllerManager(nextTrack.getString("stream_url"))

        Data.put("title", nextTrack.getString("title"))
        Data.put("image", nextTrack.getString("thumb"))
        Data.put("duration", nextTrack.getString("duration"))
        Data.put("id", nextIndex)
        Data.put("from", trackData.getString("from"))
        onMediaPlay(this, Data)
    }


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun previousSong(){
        val nowPlaying = data.getNowPlaying(this)
        val trackData = JSONObject(nowPlaying)
        val Data = JSONObject()
        var playlistTracks = JSONArray()
        when(trackData.getString("from")){
            "allsongs" ->{
                playlistTracks = JSONArray(data.getAllSongs(this))
            }
            "favorites" ->{
                playlistTracks = JSONArray(data.getFavorites(this))
            }
            else -> {
                playlistTracks = JSONArray(data.getPlaylistTracks(this, trackData.getString("from")))
            }
        }
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

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun repeatSong(){
        val nowPlaying = data.getNowPlaying(this)
        val trackData = JSONObject(nowPlaying)
        val Data = JSONObject()
        if (trackData.getString("from") == "favorites"){
            mediaControllerManager.playFavorites(trackData.getString("trackID"), this)
        }else{
            mediaControllerManager.mediaControllerManager(trackData.getString("streamUrl"))
        }

        Data.put("title", trackData.getString("title"))
        Data.put("image", trackData.getString("image"))
        Data.put("duration", trackData.getString("duration"))
        Data.put("id", trackData.getString("id"))
        Data.put("trackID",trackData.getString("trackID"))
        Data.put("streamUrl",trackData.getString("streamUrl"))
        Data.put("from", trackData.getString("from"))
        onMediaPlay(this, Data)
    }

    fun showMediaControls(context: Context){
        val mcontext = context as Home
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

    fun hideMediaControls(context: Context){
        val mcontext = context as Home
        mcontext.ivSkipButton2.visibility = View.GONE
        mcontext.ibPreviousButton.visibility = View.GONE
        mcontext.ivSkipBurron.visibility = View.GONE
        mcontext.ibRepeatButton.visibility = View.GONE
        mcontext.ibShuffleButton.visibility = View.GONE
        mcontext.seekBar2.visibility = View.GONE
        mcontext.seekBar3.visibility = View.GONE
        mcontext.progressBar.visibility = View.GONE
        mcontext.imageView3.visibility = View.GONE
        mcontext.ivOverlay.visibility = View.GONE
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun shufflePlaylist(){
        val nowPlaying = data.getNowPlaying(this)
        val trackData = JSONObject(nowPlaying)
        val Data = JSONObject()
        var playlistTracks = JSONArray()
        when(trackData.getString("from")){
            "allsongs" ->{
                playlistTracks = JSONArray(data.getAllSongs(this))
            }
            "favorites" ->{
                playlistTracks = JSONArray(data.getFavorites(this))
            }
            else -> {
                playlistTracks = JSONArray(data.getPlaylistTracks(this, trackData.getString("from")))
            }
        }
        val playlistCount = playlistTracks.length()
        val trackIndex = trackData.getString("id").toInt()
        var nextIndex : Int = Random.nextInt(0, playlistCount)

        val nextTrack = playlistTracks.getJSONObject(nextIndex)
        mediaControllerManager.mediaControllerManager(nextTrack.getString("stream_url"))

        Data.put("title", nextTrack.getString("title"))
        Data.put("image", nextTrack.getString("thumb"))
        Data.put("duration", nextTrack.getString("duration"))
        Data.put("id", nextIndex)
        Data.put("from", trackData.getString("from"))
        onMediaPlay(this, Data)
    }


    private  fun setCurrentFragment(fragment: Fragment){
        supportFragmentManager.beginTransaction().apply {
            setCustomAnimations(R.anim.fade_in,R.anim.fade_out)
            replace(R.id.flFragments, fragment)
            commit()
        }
    }
}
