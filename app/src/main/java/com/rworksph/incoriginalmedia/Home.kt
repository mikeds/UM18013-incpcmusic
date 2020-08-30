package com.rworksph.incoriginalmedia

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.LayerDrawable
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkInfo
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.SeekBar
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.rworksph.incoriginalmedia.Services.MusicServices
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.first_user_dialog.*
import org.json.JSONArray
import org.json.JSONObject
import kotlin.random.Random

@Suppress("DEPRECATION", "NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class Home : AppCompatActivity(), MediaOnPlayListener, SettingsFragment.onSelectThemeListener {


    var dialog:Dialog? = null
    var firstTimeDialog:Dialog? = null
    val data = Data()
    var mediaControllerManager = MediaControllerManager()
    val context:Context = this

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("WrongConstant", "NewApi", "JavascriptInterface")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)


        dialog = Dialog(this)
        firstTimeDialog = Dialog(this)

        val homeFragment = HomeFragment()
        val djFragment = DjFragment()
        val favoriteFragment = FavoriteFragment()
        val settingsFragment = SettingsFragment()

        imageView3.setColorFilter(Color.parseColor("#2a2a2a"))
        ivOverlay.setColorFilter(Color.parseColor("#2a2a2a"))
        val fadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out)
        tvDownloadMusic.setOnClickListener { tvDownloadMusic.visibility = View.GONE
                                             tvDownloadMusic.startAnimation(fadeOut)}

        if (data.isFirstTimeUser(this)!!){
            firstUse()
        }
        if (data.getTheme(this) != ""){
            val color = JSONObject(data.getTheme(this))

            val iconsColorStates = ColorStateList(
                arrayOf(
                    intArrayOf(-android.R.attr.state_checked),
                    intArrayOf(android.R.attr.state_checked)
                ), intArrayOf(
                    Color.parseColor("#aaaaaa"),
                    Color.parseColor(color.getString("colorAccent"))
                )
            )

            val jowable = seekBar2.progressDrawable as LayerDrawable
            val jowable2 = seekBar3.progressDrawable as LayerDrawable
            val sbProgressJowable2 = jowable2.getDrawable(1)
            val sbProgressJowable = jowable.getDrawable(1)
            val jowable3 = progressBar.progressDrawable as LayerDrawable
            val sbProgressJowable3 = jowable3.getDrawable(2)
            sbProgressJowable3.setColorFilter(Color.parseColor(color.getString("colorAccent")), PorterDuff.Mode.SRC_IN)
            sbProgressJowable.setColorFilter(Color.parseColor(color.getString("colorAccent")), PorterDuff.Mode.SRC_IN)
            sbProgressJowable2.setColorFilter(Color.parseColor(color.getString("colorAccent")), PorterDuff.Mode.SRC_IN)
            playProgress.indeterminateTintList = ColorStateList.valueOf(Color.parseColor(color.getString("colorAccent")))
            expandedProgressBar.indeterminateTintList = ColorStateList.valueOf(Color.parseColor(color.getString("colorAccent")))
            ivPlayButton.setColorFilter(Color.parseColor(color.getString("colorAccent")))
            ibShuffleButton.setColorFilter(Color.parseColor(color.getString("colorAccent")))
            ibPreviousButton.setColorFilter(Color.parseColor(color.getString("colorAccent")))
            ibRepeatButton.setColorFilter(Color.parseColor(color.getString("colorAccent")))
            ivPlayPauseBurron.setColorFilter(Color.parseColor(color.getString("colorAccent")))
            ivSkipBurron.setColorFilter(Color.parseColor(color.getString("colorAccent")))
            ivSkipButton2.setColorFilter(Color.parseColor(color.getString("colorAccent")))
            bottomNav.itemTextColor = iconsColorStates
            bottomNav.itemIconTintList = iconsColorStates
            bottomNav.setBackgroundColor(Color.parseColor(color.getString("backgroundColor")))
        }


        ivSkipBurron.setOnClickListener { nextSong() }
        ivSkipButton2.setOnClickListener {nextSong()}
        ibPreviousButton.setOnClickListener {previousSong()}
        ibRepeatButton.setOnClickListener { repeatSong() }
        ibShuffleButton.setOnClickListener { shufflePlaylist() }
        ivPlayPauseBurron.setOnClickListener { playpause() }
        ivPlayButton.setOnClickListener { playpause() }

        setCurrentFragment(homeFragment)



        val bottomsheetbehavior = BottomSheetBehavior.from(bottomsheet)

        bottomsheet.visibility = View.GONE

        tvMediaTitle.setOnClickListener {
            if (data.getNowPlaying(this)?.contains("DJ")!!) {
                setCurrentFragment(djFragment)
            }else{
                MediaControllerCollapse.visibility = View.GONE
                MediaControllerCollapse.startAnimation(fadeOut)
                bottomsheetbehavior.state = BottomSheetBehavior.STATE_EXPANDED
            }

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
                    BottomSheetBehavior.STATE_DRAGGING -> {
                        if (data.getNowPlaying(this@Home)?.contains("DJ")!!) {
                            bottomsheetbehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                        }
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                /*Log.e("slide", slideOffset.toString())
                MediaControllerCollapse.visibility = View.VISIBLE
                MediaControllerExpanded.visibility = View.VISIBLE
                val negativeOffset = 1-slideOffset
                MediaControllerExpanded.alpha = slideOffset
                MediaControllerCollapse.alpha = negativeOffset*/
            }
        }
        bottomsheetbehavior.addBottomSheetCallback(bottomSheetCallback)

        bottomNav.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.navHome-> {
                    setCurrentFragment(homeFragment)
                    bottomsheetbehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                    return@setOnNavigationItemSelectedListener true
                }

                R.id.navDJ-> {
                    setCurrentFragment(djFragment)
                    bottomsheetbehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                    return@setOnNavigationItemSelectedListener true
                }

                R.id.navFavorites-> {
                    setCurrentFragment(favoriteFragment)
                    bottomsheetbehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.navSettings-> {
                    setCurrentFragment(settingsFragment)
                    bottomsheetbehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                    return@setOnNavigationItemSelectedListener true
                }

            }
            false

        }
        seekBar3.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                if (b) {
                    mediaControllerManager.mediaPlayer.seekTo(i)
                    seekBar2.progress = i
                    seekBar3.progress = i
                    progressBar.progress = i
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // Do something
            }
            override fun onStopTrackingTouch(seekBar: SeekBar) {
                // Do something
            }
        })



        ivTopImg.setOnClickListener {
            val url = "https://iglesianicristo.net/"

            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(url)
            startActivity(i)
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            connectivityManager.registerDefaultNetworkCallback(object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    //Toast.makeText(this@Home, "connected", Toast.LENGTH_SHORT).show()
                    data.connectivity(this@Home, true)
                    Toast.makeText(this@Home, "Connected", Toast.LENGTH_SHORT).show()
                }

                override fun onLost(network: Network?) {
                    data.connectivity(this@Home, false)
                    Toast.makeText(
                        this@Home,
                        "No Internet Connection Available",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onUnavailable() {
                    data.connectivity(this@Home, false)
                    Toast.makeText(
                        this@Home,
                        "No Internet Connection Available",
                        Toast.LENGTH_SHORT
                    ).show()

                }

            })
        }else{
            if (isConnectingToInternet()){
                data.connectivity(this, true)
            }else{
                data.connectivity(this, false)
            }
        }

        runOnUiThread(object : Runnable {
            override fun run() {
                if (mediaControllerManager.mediaPlayer != null) {
                    if (mediaControllerManager.mediaPlayer.isPlaying){
                        val message = Message()
                        message.what = mediaControllerManager.mediaPlayer.currentPosition
                        handler.sendMessage(message)
                    }

                }
                handler.postDelayed(this, 1000)
            }
        })

        registerReceiver(receiver, IntentFilter("home"))
        if (mediaControllerManager.mediaPlayer.duration != 0){
            updatePlayer()
            playProgress.visibility = View.GONE
            expandedProgressBar.visibility = View.GONE

            ivPlayPauseBurron.visibility = View.VISIBLE
            ivPlayButton.visibility = View.VISIBLE
            updatePlayPause()


            seekBar2.progress = mediaControllerManager.mediaPlayer.currentPosition
            seekBar3.progress = mediaControllerManager.mediaPlayer.currentPosition
        }
    }

    var receiver: BroadcastReceiver = object : BroadcastReceiver() {
        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        override fun onReceive(context: Context?, _intent: Intent) {
            val action = _intent.extras?.getString("UI")
            //Toast.makeText(this@Home, "yohoooo!!", Toast.LENGTH_LONG).show()
            playProgress.visibility = View.GONE
            when (action){
                "prepared" ->{
                    musicPrepared()
                }
                "nextSong" ->{
                    updatePlayer()
                }
                "updatePlayPause" ->{
                    updatePlayPause()
                }
            }
        }
    }

    @SuppressLint("HandlerLeak")
    private val handler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            seekBar2.progress = msg.what
            seekBar3.progress = msg.what
            progressBar.progress = msg.what
        }
    }

    fun updatePlayPause(){
        if (mediaControllerManager.mediaPlayer.isPlaying){
            ivPlayButton.setImageResource(R.drawable.ic_baseline_pause_24_d1a538)
            ivPlayPauseBurron.setImageResource(R.drawable.ic_baseline_pause_24_d1a538)
        }else{
            ivPlayButton.setImageResource(R.drawable.ic_baseline_play_arrow_24_d1a538)
            ivPlayPauseBurron.setImageResource(R.drawable.ic_baseline_play_arrow_24_d1a538)
        }
    }

    fun musicPrepared(){
        playProgress.visibility = View.GONE
        ivPlayPauseBurron.visibility = View.VISIBLE

        expandedProgressBar.visibility = View.GONE
        ivPlayButton.visibility = View.VISIBLE
    }

    fun updatePlayer(){
        if(data.getNowPlaying(this) != ""){
            val npdata = data.getNowPlaying(this)
            val nowplaying = JSONObject(npdata)
            val fade_in = AnimationUtils.loadAnimation(this, R.anim.fade_in)

            seekBar2.progress = 0
            seekBar3.progress = 0
            progressBar.progress = 0

            playProgress.visibility = View.VISIBLE
            expandedProgressBar.visibility = View.VISIBLE

            ivPlayPauseBurron.visibility = View.GONE
            ivPlayButton.visibility = View.GONE

            tvMediaTitle.text = nowplaying.getString("title")
            tvMediaTitle.isSelected = true
            tvMediaControllerHeaderTitle.text = nowplaying.getString("title")
            tvMediaControllerHeaderTitle.isSelected = true
            seekBar2.max = (nowplaying.getString("duration").toInt() * 1000)
            seekBar3.max = (nowplaying.getString("duration").toInt() * 1000)
            progressBar.max = (nowplaying.getString("duration").toInt() * 1000)
            Picasso.get()
                .load(nowplaying.getString("image"))
                .resize(300, 300)
                .centerCrop()
                .into(ivMediaAlbum)
            Picasso.get()
                .load(nowplaying.getString("image"))
                .resize(300, 300)
                .centerCrop()
                .into(ivMediaControllerHeaderThumb)
            Picasso.get()
                .load(nowplaying.getString("waveform_url"))
                .into(imageView3)

            bottomsheet.visibility = View.VISIBLE
            if (bottomsheet.visibility != View.VISIBLE){
                bottomsheet.startAnimation(fade_in)
            }

        }


    }

    fun isConnectingToInternet(): Boolean {
        val connectivity =
            this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivity != null) {
            val info = connectivity.allNetworkInfo
            if (info != null) for (i in info.indices) if (info[i]
                    .state == NetworkInfo.State.CONNECTED
            ) {
                return true
            }
        }
        return false
    }

    fun playpause() {

        if (mediaControllerManager.mediaPlayer != null && mediaControllerManager.mediaPlayer.isPlaying) {
            mediaControllerManager.mediaPlayer.pause()
            ivPlayPauseBurron.setImageResource(R.drawable.ic_baseline_play_arrow_24_d1a538)
            ivPlayButton.setImageResource(R.drawable.ic_baseline_play_arrow_24_d1a538)
        } else {
            mediaControllerManager.mediaPlayer.start()
            ivPlayPauseBurron.setImageResource(R.drawable.ic_baseline_pause_24_d1a538)
            ivPlayButton.setImageResource(R.drawable.ic_baseline_pause_24_d1a538)
        }

        val intent = Intent("ACTION").putExtra("actionName","updatePlayPause")
        application.applicationContext.sendBroadcast(intent)
    }

    override fun onMediaPlay(context: Context, Data: JSONObject) {
        var mcontext = (context as? Home)
        var fade_in = AnimationUtils.loadAnimation(mcontext, R.anim.fade_in)

        val intent = Intent(mcontext, MusicServices::class.java)
        intent.putExtra("title", Data.getString("title"))
        intent.putExtra("image", Data.getString("image"))
        intent.putExtra("duration", Data.getString("duration"))
        intent.putExtra("waveform_url", Data.getString("waveform_url"))
        intent.putExtra("trackID", Data.getString("trackID"))
        intent.putExtra("streamUrl", Data.getString("streamUrl"))
        intent.putExtra("id", Data.getString("id"))
        intent.putExtra("from", Data.getString("from"))
        mcontext?.startService(intent)

        data.nowPlaying(context, Data.toString())

        if (Data.getString("from") == "DjCue"){
            hideMediaControls(context)
        }else{
            showMediaControls(context)
        }
        mcontext?.seekBar2?.progress = 0
        mcontext?.seekBar3?.progress = 0
        mcontext?.progressBar?.progress = 0
        mcontext?.progressBar?.visibility = View.GONE

        mcontext?.playProgress?.visibility = View.VISIBLE
        mcontext?.expandedProgressBar?.visibility = View.VISIBLE

        mcontext?.ivPlayPauseBurron?.visibility = View.GONE
        mcontext?.ivPlayButton?.visibility = View.GONE

        mcontext?.ivPlayPauseBurron?.setImageResource(R.drawable.ic_baseline_pause_24_d1a538)
        mcontext?.ivPlayButton?.setImageResource(R.drawable.ic_baseline_pause_24_d1a538)

        mcontext?.tvMediaTitle?.text = Data.getString("title")
        mcontext?.tvMediaTitle?.isSelected = true
        mcontext?.tvMediaControllerHeaderTitle?.text = Data.getString("title")
        mcontext?.tvMediaControllerHeaderTitle?.isSelected = true
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
        Picasso.get()
            .load(Data.getString("waveform_url"))
            .into(mcontext?.imageView3)


        mcontext?.progressBar?.visibility = View.VISIBLE

        mcontext?.bottomsheet?.visibility = View.VISIBLE
        if (mcontext?.bottomsheet?.visibility != View.VISIBLE){
            mcontext?.bottomsheet?.startAnimation(fade_in)
        }
    }


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun nextSong(){
        val nowPlaying = data.getNowPlaying(this)
        val trackData = JSONObject(nowPlaying)
        val Data = JSONObject()

        val playlistTracks: JSONArray
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
        val nextIndex : Int
        if (playlistCount <= trackIndex){
            nextIndex = 0
        }else{
            nextIndex = trackIndex+1
        }
        val nextTrack = playlistTracks.getJSONObject(nextIndex)





        Data.put("title", nextTrack.getString("title"))
        Data.put("image", nextTrack.getString("thumb"))
        Data.put("duration", nextTrack.getString("duration"))
        Data.put("waveform_url", nextTrack.getString("waveform_url"))
        Data.put("trackID", nextTrack.getString("id"))
        Data.put("streamUrl", nextTrack.getString("stream_url"))
        Data.put("id", nextIndex)
        Data.put("from", trackData.getString("from"))
        onMediaPlay(this, Data)
    }


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun previousSong(){
        val nowPlaying = data.getNowPlaying(this)
        val trackData = JSONObject(nowPlaying)
        val Data = JSONObject()
        val playlistTracks: JSONArray
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
        val nextIndex: Int
        if (trackIndex <= 0){
            nextIndex = playlistCount-1
        }else{
            nextIndex = trackIndex-1
        }
        val nextTrack = playlistTracks.getJSONObject(nextIndex)


        Data.put("title", nextTrack.getString("title"))
        Data.put("image", nextTrack.getString("thumb"))
        Data.put("duration", nextTrack.getString("duration"))
        Data.put("waveform_url", nextTrack.getString("waveform_url"))
        Data.put("trackID", nextTrack.getString("id"))
        Data.put("streamUrl", nextTrack.getString("stream_url"))
        Data.put("id", nextIndex)
        Data.put("from", trackData.getString("from"))
        onMediaPlay(this, Data)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun repeatSong(){
        val nowPlaying = data.getNowPlaying(this)
        val trackData = JSONObject(nowPlaying)
        val Data = JSONObject()



        Data.put("title", trackData.getString("title"))
        Data.put("image", trackData.getString("image"))
        Data.put("duration", trackData.getString("duration"))
        Data.put("id", trackData.getString("id"))
        Data.put("trackID",trackData.getString("trackID"))
        Data.put("streamUrl",trackData.getString("streamUrl"))
        Data.put("waveform_url", trackData.getString("waveform_url"))
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
        val playlistTracks: JSONArray
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
        val nextIndex : Int = Random.nextInt(0, playlistCount)

        val nextTrack = playlistTracks.getJSONObject(nextIndex)


        Data.put("title", nextTrack.getString("title"))
        Data.put("image", nextTrack.getString("thumb"))
        Data.put("duration", nextTrack.getString("duration"))
        Data.put("waveform_url", nextTrack.getString("waveform_url"))
        Data.put("trackID", nextTrack.getString("id"))
        Data.put("streamUrl", nextTrack.getString("stream_url"))
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


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onSelectTheme(color: JSONObject) {
        val iconsColorStates = ColorStateList(
            arrayOf(
                intArrayOf(-android.R.attr.state_checked),
                intArrayOf(android.R.attr.state_checked)
            ), intArrayOf(
                Color.parseColor("#aaaaaa"),
                Color.parseColor(color.getString("colorAccent"))
            )
        )

        val jowable = seekBar2.progressDrawable as LayerDrawable
        val sbProgressJowable = jowable.getDrawable(1)
        sbProgressJowable.setColorFilter(Color.parseColor(color.getString("colorAccent")), PorterDuff.Mode.SRC_IN)
        val jowable2 = seekBar3.progressDrawable as LayerDrawable
        val sbProgressJowable2 = jowable2.getDrawable(1)
        sbProgressJowable2.setColorFilter(Color.parseColor(color.getString("colorAccent")), PorterDuff.Mode.SRC_IN)
        val jowable3 = progressBar.progressDrawable as LayerDrawable
        val sbProgressJowable3 = jowable3.getDrawable(2)
        sbProgressJowable3.setColorFilter(Color.parseColor(color.getString("colorAccent")), PorterDuff.Mode.SRC_IN)
        playProgress.indeterminateTintList = ColorStateList.valueOf(Color.parseColor(color.getString("colorAccent")))
        expandedProgressBar.indeterminateTintList = ColorStateList.valueOf(Color.parseColor(color.getString("colorAccent")))
        ivPlayButton.setColorFilter(Color.parseColor(color.getString("colorAccent")))
        ibShuffleButton.setColorFilter(Color.parseColor(color.getString("colorAccent")))
        ibPreviousButton.setColorFilter(Color.parseColor(color.getString("colorAccent")))
        ibRepeatButton.setColorFilter(Color.parseColor(color.getString("colorAccent")))
        ivPlayPauseBurron.setColorFilter(Color.parseColor(color.getString("colorAccent")))
        ivSkipBurron.setColorFilter(Color.parseColor(color.getString("colorAccent")))
        ivSkipButton2.setColorFilter(Color.parseColor(color.getString("colorAccent")))
        bottomNav.itemTextColor = iconsColorStates
        bottomNav.itemIconTintList = iconsColorStates
        bottomNav.setBackgroundColor(Color.parseColor(color.getString("backgroundColor")))
    }



    fun onMusicPrepared(context: Context) {
        val mcontext = (context as? Home)
        mcontext?.playProgress?.visibility = View.GONE
        mcontext?.expandedProgressBar?.visibility = View.GONE
        mcontext?.ivPlayPauseBurron?.visibility = View.VISIBLE
        mcontext?.ivPlayButton?.visibility = View.VISIBLE
    }

    fun songNotAvailable(context: Context){
        if (mediaControllerManager.mediaPlayer.isPlaying){
            mediaControllerManager.mediaPlayer.stop()
            mediaControllerManager.mediaPlayer.reset()
        }
        val mcontext = (context as? Home)
        mcontext?.bottomsheet?.visibility = View.GONE
        mcontext?.dialog?.setContentView(R.layout.unavailable_offline)
        val tofave = mcontext?.dialog?.findViewById<Button>(R.id.bToFave)
        val closediag = mcontext?.dialog?.findViewById<Button>(R.id.bPopupExit)
        mcontext?.dialog?.show()
        val favefrag = FavoriteFragment()

        closediag?.setOnClickListener { mcontext.dialog?.dismiss() }
        tofave?.setOnClickListener {
            mcontext.setCurrentFragment(favefrag)
            mcontext.dialog?.dismiss()
        }
    }

    fun firstUse (){
        val mcontext = (context as? Home)
        mcontext?.firstTimeDialog?.setContentView(R.layout.first_user_dialog)
        mcontext?.firstTimeDialog?.setCancelable(false)
        mcontext?.firstTimeDialog?.setCanceledOnTouchOutside(false)
        mcontext?.firstTimeDialog?.cbAgree?.setOnCheckedChangeListener { button, b ->
            mcontext.firstTimeDialog?.bContinue?.isEnabled = firstTimeDialog?.cbAgree?.isChecked!!
        }

        mcontext?.firstTimeDialog?.bContinue?.setOnClickListener {
            data.firstUser(this, false)
            mcontext.firstTimeDialog?.dismiss()
        }
        mcontext?.firstTimeDialog?.show()
    }

    @SuppressLint("SetTextI18n")
    fun downloadingMusic(context: Context, title:String, action:String){
        val mcontext = context as? Home
        val fade_in = AnimationUtils.loadAnimation(mcontext, R.anim.fade_in)


        mcontext?.tvDownloadMusic?.visibility = View.VISIBLE
        mcontext?.tvDownloadMusic?.startAnimation(fade_in)

        mcontext?.tvDownloadMusic?.text = "$title is $action \n \n Tap to close"

    }






}
