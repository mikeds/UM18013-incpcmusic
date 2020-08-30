package com.rworksph.incoriginalmedia

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.graphics.Color
import android.media.MediaRecorder
import android.media.audiofx.Visualizer
import android.media.audiofx.Visualizer.OnDataCaptureListener
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.rworksph.incoriginalmedia.customwave.DrawView
import com.rworksph.incoriginalmedia.customwave.UpdaterThread
import kotlinx.android.synthetic.main.fragment_dj.view.*
import org.json.JSONObject
import java.io.IOException
import java.nio.ByteBuffer


@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class DjFragment : Fragment() {

    var mediaControllerManager = MediaControllerManager()
    val data = Data()
    val home = Home()
    var up : UpdaterThread? = null
    var drawView : DrawView? = null
    var REFRESH_INTERVAL_MS: Long = 50
    private var keepGoing = false
    var layout: LinearLayout? = null
    var tr = 10.0f
    private var bytes: ByteArray = byteArrayOf(1)
    private var visualizer: Visualizer? = null

    var thread : Thread? = null


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("WrongConstant", "SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        val view  : View = inflater.inflate(R.layout.fragment_dj, container, false)
        bytes[0] = -128


        drawView = view.findViewById(R.id.root_adjust)

        if (data.getTheme(activity!!) != ""){
            val color = JSONObject(data.getTheme(activity!!))
            view.ivDjPlayPause.setColorFilter(Color.parseColor(color.getString("colorAccent")))
            drawView?.setWaveColor(Color.parseColor(color.getString("colorAccent")))
        }

        up = UpdaterThread(100, drawView!!, activity!!)
        up!!.start()




        thread = Thread(Runnable {
            while (keepGoing) {
                try {
                    Thread.sleep(
                        Math.max(
                            0,
                            REFRESH_INTERVAL_MS - redraw()
                        )
                    )
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        })





        if (data.getNowPlaying(activity!!)?.contains("DJ")!! && mediaControllerManager.mediaPlayer.isPlaying){
            view.textView.text = "Playing"
            view.ivDjPlayPause.visibility = View.GONE
            setPlayer(mediaControllerManager.mediaPlayer.audioSessionId)
            keepGoing = true
            if (thread?.isAlive != true){
                thread?.start()
            }
        }else{
            view.textView.text = "Not Playing"
            view.ivDjPlayPause.visibility = View.VISIBLE
        }




        view.ivDjPlayPause.setOnClickListener {

            if (data.isConnected(activity!!) == true){
                if (ContextCompat.checkSelfPermission(activity!!, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED){
                    if (view.textView.text == "Playing") {
                        if (mediaControllerManager.mediaPlayer != null && mediaControllerManager.mediaPlayer.isPlaying) {
                            mediaControllerManager.mediaPlayer.pause()

                            view.textView.text = "Not Playing"
                        } else {
                            mediaControllerManager.mediaPlayer.start()

                        }
                    } else {

                        //startVoiceRecorder()
                        view.ivDjPlayPause.visibility = View.GONE
                        //mediaControllerManager.mediaControllerManager("https://edge.mixlr.com/channel/wycvw",activity!!)

                        setPlayer(mediaControllerManager.mediaPlayer.audioSessionId)
                        keepGoing = true
                        thread?.start()
                        //view.ivDjPlayPause.setImageResource(R.drawable.ic_baseline_pause_24_d1a538)
                        view.textView.text = "Playing"
                        //bottomSheetLayout.visibility = View.GONE
                        val Data = JSONObject()
                        Data.put("title", "DJ's Cue Live Streaming")
                        Data.put(
                            "image",
                            "https://images.hearthis.at/1/5/9/_/uploads/9341074/image_user/incpc----w800_q70_----1594624193823.jpg"
                        )
                        Data.put("duration", "0")
                        Data.put("id", "0")
                        Data.put("trackID", "0")
                        Data.put("from", "DjCue")
                        Data.put("streamUrl", "https://edge.mixlr.com/channel/wycvw")
                        Data.put("waveform_url", "https://images.hearthis.at/1/5/9/_/uploads/9341074/image_user/incpc----w800_q70_----1594624193823.jpg")
                        home.onMediaPlay(activity!!,Data)


                    }
                }else{
                    requestMicPermission()
                }

            }else{
                home.songNotAvailable(activity!!)
            }

        }


            return view
        }

    private fun requestMicPermission(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(activity!!, Manifest.permission.RECORD_AUDIO)){
            AlertDialog.Builder(activity!!)
                .setTitle("Permission Needed")
                .setMessage("Need Permision to play DJ's Cuew")
                .setPositiveButton("OK", DialogInterface.OnClickListener { dialogInterface, i ->
                    ActivityCompat.requestPermissions(activity!!, arrayOf(Manifest.permission.RECORD_AUDIO) , 1)
                })
                .setNegativeButton("cancel", DialogInterface.OnClickListener { dialogInterface, i ->
                    dialogInterface.dismiss()
                }).create().show()
        }else{
            ActivityCompat.requestPermissions(activity!!, arrayOf(Manifest.permission.RECORD_AUDIO) , 1)
        }


    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if(requestCode == 1){
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED ){
                Toast.makeText(activity!!, "Permission Granted", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(activity!!, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDetach() {
        super.onDetach()

        keepGoing = false
    }



    private fun redraw(): Long {
        val t = System.currentTimeMillis()
        display_game()
        return System.currentTimeMillis() - t
    }


    private fun display_game() {
        activity!!.runOnUiThread(Runnable {
            drawView?.setMaxAmplitude((bytes[0] + 128) * 20f)
        })
    }




    fun setPlayer(audioSessionId: Int) {
        visualizer = Visualizer(audioSessionId)
        visualizer!!.enabled = false
        visualizer!!.captureSize = Visualizer.getCaptureSizeRange()[0]
        visualizer!!.setDataCaptureListener(object : OnDataCaptureListener {
            override fun onWaveFormDataCapture(
                visualizer: Visualizer, bytes: ByteArray,
                samplingRate: Int
            ) {
                this@DjFragment.bytes = bytes

            }

            override fun onFftDataCapture(
                visualizer: Visualizer, bytes: ByteArray,
                samplingRate: Int
            ) {
            }
        }, Visualizer.getMaxCaptureRate(), true, false)
        visualizer!!.setEnabled(true)
    }



}


