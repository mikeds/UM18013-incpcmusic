package com.rworksph.incoriginalmedia

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.SeekBar
import androidx.annotation.ColorInt
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.OrientationHelper
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.media_controller.*

class Home : AppCompatActivity(),MediaOnPlayListener {

    //var task = FetchData("https://api-v2.hearthis.at/mikeds/?type=playlists")
    internal var datalist:MutableList<Sets> = ArrayList()
    var songList = ArrayList<HashMap<String, String>>()
    var data = Data()
    var init = inits()
    var boolean : Boolean = false


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("WrongConstant")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        imageView3.setColorFilter(Color.parseColor("#2a2a2a"))
        ivOverlay.setColorFilter(Color.parseColor("#2a2a2a"))

        bottomSheetLayout.setOnProgressListener { progress -> onprog() }
        tvMediaTitle.setOnClickListener { _ -> bottomSheetLayout.toggle()  }
        var mediaControllerManager = MediaControllerManager()

        //albums
        init.initListData(this, datalist)

        //all songs
        init.initHomeSongs(this, songList)
        val adapter = ListsAdapter(this, datalist)
        view_pager.adapter =adapter

        rvHome.layoutManager = LinearLayoutManager(this, OrientationHelper.HORIZONTAL, false)
        rvHome.adapter = HomeAdapter(this, songList)

        bottomSheetLayout.visibility = View.GONE

        if (mediaControllerManager.mediaPlayer.isPlaying){
            bottomSheetLayout.visibility = View.VISIBLE
            loadOnPlayData()
        }

        //Toast.makeText(this, mediaControllerManager.Sig.toString(), Toast.LENGTH_SHORT).show()
        ivPlayPauseBurron.setOnClickListener{
            mediaControllerManager.play()
            if (boolean){
                ivPlayPauseBurron.setImageResource(R.drawable.ic_baseline_pause_24_d1a538)
                ivPlayButton.setImageResource(R.drawable.ic_baseline_pause_24_d1a538)

            }else{
                ivPlayPauseBurron.setImageResource(R.drawable.ic_baseline_play_arrow_24_d1a538)
                ivPlayButton.setImageResource(R.drawable.ic_baseline_play_arrow_24_d1a538)

            }
            boolean = !boolean
        }

        ivPlayButton.setOnClickListener{
            mediaControllerManager.play()
            if (boolean){
                ivPlayPauseBurron.setImageResource(R.drawable.ic_baseline_pause_24_d1a538)
                ivPlayButton.setImageResource(R.drawable.ic_baseline_pause_24_d1a538)

            }else{
                ivPlayPauseBurron.setImageResource(R.drawable.ic_baseline_play_arrow_24_d1a538)
                ivPlayButton.setImageResource(R.drawable.ic_baseline_play_arrow_24_d1a538)

            }
            boolean = !boolean
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
     override fun onMediaPlay(context: Context, data: ArrayList<String>) {
         var mcontext = (context as? Home)
         var fade_in = AnimationUtils.loadAnimation(mcontext, R.anim.fade_in)
         if (boolean){
             boolean = !boolean
         }
         val sharedPreference =  context.getSharedPreferences("Data",Context.MODE_PRIVATE)
         var editor = sharedPreference.edit()
         editor.putString("onPlayData",data.toString())
         editor.commit()
         mcontext?.ivPlayPauseBurron?.setImageResource(R.drawable.ic_baseline_pause_24_d1a538)
         mcontext?.ivPlayButton?.setImageResource(R.drawable.ic_baseline_pause_24_d1a538)
         val array : ArrayList<String> = data
         mcontext?.tvMediaTitle?.setText(array[0])
         mcontext?.tvMediaControllerHeaderTitle?.setText(array[0])
         mcontext?.seekBar2?.max = (array[2].toInt()*1000)
         mcontext?.seekBar3?.max = (array[2].toInt()*1000)
         mcontext?.progressBar?.max = (array[2].toInt()*1000)

         //Toast.makeText(context, array[1], Toast.LENGTH_LONG).show()
         Picasso.get()
             .load(array[1].toString())
             .resize(300, 300)
             .centerCrop()
             .into(mcontext?.ivMediaAlbum)
         Picasso.get()
             .load(array[1].toString())
             .resize(300, 300)
             .centerCrop()
             .into(mcontext?.ivMediaControllerHeaderThumb)
         mcontext?.bottomSheetLayout?.visibility = View.VISIBLE
         mcontext?.bottomSheetLayout?.startAnimation(fade_in)
    }

    fun loadOnPlayData(){
        val sharedPreference =  this.getSharedPreferences("Data",Context.MODE_PRIVATE)
        val data = sharedPreference.getString("onPlayData","")

        val formattedData = data?.replace("[", "")?.replace("]", "")
        val newData = formattedData?.split(",")?.toTypedArray()


        ivPlayPauseBurron?.setImageResource(R.drawable.ic_baseline_pause_24_d1a538)
        ivPlayButton?.setImageResource(R.drawable.ic_baseline_pause_24_d1a538)
        //Toast.makeText(this, newData!![0]+","+newData[1]+","+newData[2], Toast.LENGTH_LONG).show()
        val test = newData!![0]
        tvMediaTitle.setText(newData[0])
        tvMediaControllerHeaderTitle.setText(newData[0])
        Picasso.get()
            .load(newData[1].trim())
            .resize(300, 300)
            .centerCrop()
            .into(ivMediaAlbum)
        Picasso.get()
            .load(newData[1].trim())
            .resize(300, 300)
            .centerCrop()
            .into(ivMediaControllerHeaderThumb)
        seekBar2.max = ((newData[2].trim().toInt())*1000)
        seekBar3.max = ((newData[2].trim().toInt())*1000)
        progressBar?.max = ((newData[2].trim().toInt())*1000)
    }




}