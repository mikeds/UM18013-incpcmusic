package com.rworksph.incoriginalmedia

import android.content.Context
import android.os.Build
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
                  private val dataList: ArrayList<HashMap<String, String>>) : RecyclerView.Adapter<HomeAdapter.ViewHolder>() {

    val mContext = context

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view:View = LayoutInflater.from(parent.context).inflate(R.layout.home_all_songs_item, parent, false)

        view.setOnClickListener{
            Toast.makeText(context,"songs to", Toast.LENGTH_SHORT).show()
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
        val steamUrl =dataitem.get("streamUrl")
        rowView.itemView.setOnClickListener{

        }

    }

    class ViewHolder(itemView: View) :RecyclerView.ViewHolder(itemView) {
       val title = itemView.findViewById<TextView>(R.id.tvHomeSongs)
       val image = itemView.findViewById<ImageView>(R.id.ivHomeSongAlbum)

        
    }







}