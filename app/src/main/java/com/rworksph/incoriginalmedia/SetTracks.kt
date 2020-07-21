package com.rworksph.incoriginalmedia

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_set_tracks.*
import kotlinx.android.synthetic.main.media_controller.*

class SetTracks : AppCompatActivity(),MediaOnPlayListener {

    private lateinit var itemsAdapter: SetTracksAdapter
    var songList = ArrayList<HashMap<String, String>>()
    var init = inits()
    val data = Data()
    var boolean : Boolean = false

    //
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_tracks)
        imageView3.setColorFilter(Color.parseColor("#2a2a2a"))
        ivOverlay.setColorFilter(Color.parseColor("#2a2a2a"))

        bottomSheetLayout.setOnProgressListener { progress -> onprog() }
        tvMediaTitle.setOnClickListener { _ -> bottomSheetLayout.toggle()  }
        var mediaControllerManager = MediaControllerManager()


        //Log.e("NULL??", intent.getStringExtra("uri"))
        val url = intent.getStringExtra("uri")
        var SetTracksData = FetchData(url).execute().get()
        data.storeSetTracksData(this, SetTracksData)

        init.initSetTracks(this,songList)
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rvSetTracks.layoutManager = layoutManager
        rvSetTracks.adapter = SetTracksAdapter(this, songList, intent)

        bottomSheetLayout.visibility = View.GONE

        if (mediaControllerManager.mediaPlayer.isPlaying){
            bottomSheetLayout.visibility = View.VISIBLE
            loadOnPlayData()
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

    override fun onMediaPlay(context: Context, data: ArrayList<String>) {
        var mcontext = (context as? SetTracks)
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