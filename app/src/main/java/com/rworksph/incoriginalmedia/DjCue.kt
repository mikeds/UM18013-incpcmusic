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

class DjCue : AppCompatActivity() {
    var boolean : Boolean = false


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

        ivDjPlayPause.setOnClickListener {
            if (tvMediaTitle.text == "DJ's Cue Live Streaming"){
                mediaControllerManager.play()
            }else{
                mediaControllerManager.mediaControllerManager("https://edge.mixlr.com/channel/wycvw")
                bottomSheetLayout.visibility = View.VISIBLE
                ivPlayPauseBurron?.setImageResource(R.drawable.ic_baseline_pause_24_d1a538)
                ivPlayButton?.setImageResource(R.drawable.ic_baseline_pause_24_d1a538)
                val data = arrayListOf<String>("DJ's Cue Live Streaming", "https://images.hearthis.at/1/5/9/_/uploads/9341074/image_user/incpc----w800_q70_----1594624193823.jpg", "0")
                val sharedPreference =  this.getSharedPreferences("Data",Context.MODE_PRIVATE)
                var editor = sharedPreference.edit()
                editor.putString("onPlayData",data.toString())
                editor.commit()
                ivSkipBurron.visibility = View.GONE
                tvMediaControllerHeaderTitle.text = "DJ's Cue Live Streaming"
                tvMediaTitle.text = "DJ's Cue Live Streaming"
                seekBar2.max = 0
                seekBar3.max = 0
                progressBar.max = 0
                seekBar2.setProgress(0)
                seekBar3.setProgress(0)
                progressBar.setProgress(0)
                Picasso.get()
                    .load(R.drawable.applogo)
                    .resize(300, 300)
                    .centerCrop()
                    .into(ivMediaAlbum)
                Picasso.get()
                    .load(R.drawable.applogo)
                    .resize(300, 300)
                    .centerCrop()
                    .into(ivMediaControllerHeaderThumb)

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