package com.rworksph.incoriginalmedia

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class HomeAdapter(private val context: Context,
                  private val dataList: ArrayList<HashMap<String, String>>
) : RecyclerView.Adapter<HomeAdapter.ViewHolder>() {


    var home = Home()
    var data = Data()
    var mediaOnPlayListener : MediaOnPlayListener? = null
    var mediaPlayer = MediaPlayer()
    var mediaControllerManager = MediaControllerManager()
    val mContext = context
    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view:View = LayoutInflater.from(parent.context).inflate(R.layout.home_all_songs_item, parent, false)

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
            val list = arrayListOf<String>(dataitem.get("title").toString(), dataitem.get("image").toString(), dataitem.get("duration").toString())
            home.onMediaPlay(context, list)
        })

    }

    class ViewHolder(itemView: View) :RecyclerView.ViewHolder(itemView) {
       val title = itemView.findViewById<TextView>(R.id.tvHomeSongs)
       val image = itemView.findViewById<ImageView>(R.id.ivHomeSongAlbum)

        
    }







}