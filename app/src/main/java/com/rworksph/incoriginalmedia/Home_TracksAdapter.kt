package com.rworksph.incoriginalmedia

import android.content.Context
import android.media.MediaPlayer
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import org.json.JSONArray
import org.json.JSONObject

class Home_TracksAdapter (private val context: Context,
                          private val dataList: ArrayList<HashMap<String, String>>
) : RecyclerView.Adapter<Home_TracksAdapter.ViewHolder>() {

    var home = Home()
    var data = Data()
    //var mediaOnPlayListener : MediaOnPlayListener? = null
    var mediaPlayer = MediaPlayer()
    var mediaControllerManager = MediaControllerManager()
    val mContext = context
    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.home_song_item, parent, false)

        view.setOnClickListener{

        }
        return ViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onBindViewHolder(rowView: ViewHolder, position: Int) {
        var dataitem = dataList[position]
        rowView.title.text = dataitem.get("title")
        Picasso.get()
            .load(dataitem.get("image"))
            .resize(150, 150)
            .centerCrop()
            .into(rowView.image)
        val steamUrl =dataitem.get("streamUrl").toString()

        rowView.itemView.setOnClickListener(View.OnClickListener {
            mediaControllerManager.mediaControllerManager(steamUrl)
            val trackData = JSONObject()
            trackData.put("title", dataitem.get("title"))
            trackData.put("image", dataitem.get("image"))
            trackData.put("duration", dataitem.get("duration"))
            trackData.put("streamUrl", dataitem.get("streamUrl"))
            trackData.put("trackID", dataitem.get("trackID"))
            trackData.put("id", position)
            trackData.put("from", "allsongs")
            home.onMediaPlay(context, trackData)
        })

    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title = itemView.findViewById<TextView>(R.id.tvHomeSongs)
        val image = itemView.findViewById<ImageView>(R.id.ivHomeSongAlbum)
    }







}