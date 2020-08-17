package com.rworksph.incoriginalmedia

import android.annotation.SuppressLint
import android.app.Dialog
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.Network
import android.net.Uri
import android.os.*
import android.support.v4.media.session.MediaSessionCompat
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.RemoteViews
import android.widget.SeekBar
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.Builder
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import androidx.media.app.NotificationCompat.MediaStyle
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.rworksph.incoriginalmedia.Services.NotificationReceiver
import com.rworksph.incoriginalmedia.Services.OnClearFromRecentServices
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.first_user_dialog.*
import kotlinx.android.synthetic.main.fragment_dj.*
import org.json.JSONArray
import org.json.JSONObject
import kotlin.random.Random

class Home : AppCompatActivity(), MediaOnPlayListener, SettingsFragment.onSelectThemeListener {
    internal var homePlaylistsList: MutableList<Home_Playlists> = ArrayList()
    val notificationManager: NotificationManagerCompat? = null
    val CHANNEL_ID = "incom"
    var dialog:Dialog? = null
    var firstTimeDialog:Dialog? = null
    var TracksList = ArrayList<HashMap<String, String>>()
    val data = Data()
    val init = Init()
    var mediaControllerManager = MediaControllerManager()
    val context:Context = this
    var requestID = System.currentTimeMillis().toInt()

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("WrongConstant", "NewApi", "JavascriptInterface")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)



        Log.e("UNANGBESES", data.isFirstTimeUser(this).toString())
        dialog = Dialog(this)
        firstTimeDialog = Dialog(this)
        val homeFragment = HomeFragment()
        val djFragment = DjFragment()
        val favoriteFragment = FavoriteFragment()
        val settingsFragment = SettingsFragment()
        imageView3.setColorFilter(Color.parseColor("#2a2a2a"))
        ivOverlay.setColorFilter(Color.parseColor("#2a2a2a"))

        if (data.isFirstTimeUser(this) == true){
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
            playProgress.setIndeterminateTintList(ColorStateList.valueOf(Color.parseColor(color.getString("colorAccent"))));
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
        ivPlayPauseBurron.setOnClickListener { playpause() }
        ivPlayButton.setOnClickListener { playpause() }




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

        ivTopImg.setOnClickListener {
            val url = "https://iglesianicristo.net/"

            val i = Intent(Intent.ACTION_VIEW)
            i.setData(Uri.parse(url))
            startActivity(i)
        }



        registerReceiver(receiver, IntentFilter("ACTION"))
        startService(Intent(baseContext, OnClearFromRecentServices::class.java))

        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        connectivityManager?.let {
            it.registerDefaultNetworkCallback(object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    //Toast.makeText(this@Home, "connected", Toast.LENGTH_SHORT).show()
                    data.connectivity(this@Home, true)
                    Toast.makeText(this@Home, "Connected", Toast.LENGTH_SHORT).show()
                }
                override fun onLost(network: Network?) {
                    data.connectivity(this@Home, false)
                    Toast.makeText(this@Home, "No Internet Connection Available", Toast.LENGTH_SHORT).show()
                }
            })
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
            seekBar2.progress = msg.what
            seekBar3.progress = msg.what
            progressBar.progress = msg.what
        }
    }


    fun playpause() {
        val nowplay = JSONObject(data.getNowPlaying(this))
        val title = nowplay.getString("title")
        if (mediaControllerManager.mediaPlayer != null && mediaControllerManager.mediaPlayer.isPlaying) {
            mediaControllerManager.mediaPlayer.pause()
            ivPlayPauseBurron.setImageResource(R.drawable.ic_baseline_play_arrow_24_d1a538)
            ivPlayButton.setImageResource(R.drawable.ic_baseline_play_arrow_24_d1a538)
            creatNotification(context, title, R.drawable.ic_baseline_play_arrow_24_d1a538, false)
        } else {
            mediaControllerManager.mediaPlayer.start()
            ivPlayPauseBurron.setImageResource(R.drawable.ic_baseline_pause_24_d1a538)
            ivPlayButton.setImageResource(R.drawable.ic_baseline_pause_24_d1a538)
            creatNotification(context, title, R.drawable.ic_baseline_pause_24_d1a538,true)
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

        mcontext?.playProgress?.visibility = View.VISIBLE
        mcontext?.ivPlayPauseBurron?.visibility = View.GONE
        mcontext?.ivPlayPauseBurron?.setImageResource(R.drawable.ic_baseline_pause_24_d1a538)
        mcontext?.ivPlayButton?.setImageResource(R.drawable.ic_baseline_pause_24_d1a538)

        mcontext?.tvMediaTitle?.text = Data.getString("title")
        mcontext?.tvMediaTitle?.isSelected = true
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

        creatNotification(context, Data.getString("title"), R.drawable.ic_baseline_pause_24_d1a538, true)

    }

    fun creatNotification(context: Context, Title:String, playButton:Int, isOngoing:Boolean){
        val mcontext = (context as? Home)
        val pauseIntent = Intent(mcontext, NotificationReceiver::class.java).setAction("pause")
        val pausePendingIntent: PendingIntent = PendingIntent.getBroadcast(
            mcontext,requestID, pauseIntent, FLAG_UPDATE_CURRENT)

        val prevIntent = Intent(mcontext, NotificationReceiver::class.java).setAction("previous")
        val prevPendingIntent: PendingIntent = PendingIntent.getBroadcast(
            mcontext,requestID, prevIntent, FLAG_UPDATE_CURRENT)

        val nextIntent = Intent(mcontext, NotificationReceiver::class.java).setAction("next")
        val nextPendingIntent: PendingIntent = PendingIntent.getBroadcast(
            mcontext,requestID, nextIntent, FLAG_UPDATE_CURRENT)

        val artwork : Bitmap = BitmapFactory.decodeResource(mcontext?.resources,R.drawable.applogo)
        val mediaSession : MediaSessionCompat = MediaSessionCompat(mcontext, "tag")

        var builder = Builder(mcontext!!, CHANNEL_ID)
            .setSmallIcon(R.drawable.applogo)
            .setLargeIcon(artwork)
            .setContentTitle(Title)
            .addAction(R.drawable.ic_baseline_skip_previous_24_d1a538, "Previous", prevPendingIntent) // #0
            .addAction(playButton, "Pause", pausePendingIntent) // #1
            .addAction(R.drawable.ic_baseline_skip_next_24_d1a538, "Next", nextPendingIntent) // #2
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setStyle(MediaStyle().setShowActionsInCompactView(0,1,2)
                .setMediaSession(mediaSession.sessionToken))
            .setSubText("is Playing")
            .setOngoing(isOngoing)


        with(NotificationManagerCompat.from(mcontext)) {
            // notificationId is a unique int for each notification that you must define
            notify(1, builder.build())
        }
    }

    val receiver: BroadcastReceiver = object : BroadcastReceiver() {
        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        override fun onReceive(context: Context?, _intent: Intent) {
            val action = _intent.extras?.getString("actionName")
            //Toast.makeText(this@Home, "yohoooo!!", Toast.LENGTH_LONG).show()
            when (action){
                "pause" ->{
                    playpause()
                }
                "previous" ->{
                    previousSong()
                }
                "next" ->{
                    nextSong()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        notificationManager?.cancelAll()
        unregisterReceiver(receiver)
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

        if(trackData.getString("from") == "favorites"){
            mediaControllerManager.playFavorites(nextTrack.getString("id"), context)
        }else{
            mediaControllerManager.mediaControllerManager(nextTrack.getString("stream_url"),context)
        }


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
        if(trackData.getString("from") == "favorites"){
            mediaControllerManager.playFavorites(nextTrack.getString("id"), context)
        }else{
            mediaControllerManager.mediaControllerManager(nextTrack.getString("stream_url"),context)
        }
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
            mediaControllerManager.mediaControllerManager(trackData.getString("streamUrl"),context)
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
        var nextIndex : Int = Random.nextInt(0, playlistCount)

        val nextTrack = playlistTracks.getJSONObject(nextIndex)
        if(trackData.getString("from") == "favorites"){
            mediaControllerManager.playFavorites(nextTrack.getString("id"), context)
        }else{
            mediaControllerManager.mediaControllerManager(nextTrack.getString("stream_url"),context)
        }
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

        playProgress.setIndeterminateTintList(ColorStateList.valueOf(Color.parseColor(color.getString("colorAccent"))));
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
        var mcontext = (context as? Home)
        mcontext?.playProgress?.visibility = View.GONE
        mcontext?.ivPlayPauseBurron?.visibility = View.VISIBLE
    }

    fun songNotAvailable(context: Context){
        if (mediaControllerManager.mediaPlayer.isPlaying){
            mediaControllerManager.mediaPlayer.stop()
            mediaControllerManager.mediaPlayer.reset()
        }
        var mcontext = (context as? Home)
        mcontext?.bottomsheet?.visibility = View.GONE
        mcontext?.dialog?.setContentView(R.layout.unavailable_offline)
        var tofave = mcontext?.dialog?.findViewById<Button>(R.id.bToFave)
        var closediag = mcontext?.dialog?.findViewById<Button>(R.id.bPopupExit)
        mcontext?.dialog?.show()
        val favefrag = FavoriteFragment()

        closediag?.setOnClickListener { mcontext?.dialog?.dismiss() }
        tofave?.setOnClickListener {
            mcontext?.setCurrentFragment(favefrag)
            mcontext?.dialog?.dismiss()
        }
    }

    fun firstUse (){
        var mcontext = (context as? Home)
        mcontext?.firstTimeDialog?.setContentView(R.layout.first_user_dialog)
        mcontext?.firstTimeDialog?.setCancelable(false)
        mcontext?.firstTimeDialog?.setCanceledOnTouchOutside(false)
        mcontext?.firstTimeDialog?.cbAgree?.setOnCheckedChangeListener { button, b ->
            mcontext?.firstTimeDialog?.bContinue?.isEnabled = firstTimeDialog?.cbAgree?.isChecked!!
        }

        mcontext?.firstTimeDialog?.bContinue?.setOnClickListener {
            data.firstUser(this, false)
            mcontext?.firstTimeDialog?.dismiss()
        }
        mcontext?.firstTimeDialog?.show()
        Log.e("pumasokbadito","oo")
    }






}
