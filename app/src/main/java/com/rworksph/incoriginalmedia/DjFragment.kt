package com.rworksph.incoriginalmedia

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_dj_cue.*
import kotlinx.android.synthetic.main.fragment_dj.view.*
import kotlinx.android.synthetic.main.fragment_playlist.view.*
import org.json.JSONObject

class DjFragment : Fragment() {

    var mediaControllerManager = MediaControllerManager()
    val data = Data()

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("WrongConstant")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        val view  : View = inflater.inflate(R.layout.fragment_playlist, container, false)


        view.ivDjPlayPause.setOnClickListener {
            if (textView.text.equals("Now Playing")) {
                if (mediaControllerManager.mediaPlayer != null && mediaControllerManager.mediaPlayer.isPlaying) {
                    mediaControllerManager.mediaPlayer.pause()
                    ivDjPlayPause.setImageResource(R.drawable.ic_baseline_play_arrow_24_d1a538)
                } else {
                    mediaControllerManager.mediaPlayer.start()
                    ivDjPlayPause.setImageResource(R.drawable.ic_baseline_pause_24_d1a538)
                }
            } else {
                mediaControllerManager.mediaControllerManager("https://edge.mixlr.com/channel/wycvw")
                ivDjPlayPause.setImageResource(R.drawable.ic_baseline_pause_24_d1a538)
                textView.text = "Now Playing"
                //bottomSheetLayout.visibility = View.GONE
                val Data = JSONObject()
                Data.put("title", "DJ's Cue Live Streaming")
                Data.put(
                    "image",
                    "https://images.hearthis.at/1/5/9/_/uploads/9341074/image_user/incpc----w800_q70_----1594624193823.jpg"
                )
                Data.put("duration", "0")
                Data.put("id", "0")
                Data.put("from", "DjCue")
                data.nowPlaying(activity!!,Data.toString())

            }
        }
            return view
        }
    }
