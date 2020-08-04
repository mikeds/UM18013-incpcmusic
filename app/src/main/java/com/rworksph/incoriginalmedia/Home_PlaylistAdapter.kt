package com.rworksph.incoriginalmedia

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import com.squareup.picasso.Picasso

class Home_PlaylistAdapter (internal var context: Context, internal var list: List<Home_Playlists>):
    PagerAdapter() {

    override fun isViewFromObject(p0: View, p1: Any): Boolean {
        return p0 == p1
    }

    override fun getCount(): Int {
        return list.size
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)

    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        var dataitem = list[position]

        val view = LayoutInflater.from(context).inflate(R.layout.home_playlist_item, container, false)

        view.findViewById<TextView>(R.id.tvSetTitle).text = dataitem.title
        view.findViewById<TextView>(R.id.tvSetSongCount).text = dataitem.trackCount

        Picasso.get()
            .load(dataitem.thumb)
            .resize(300, 300)
            .centerCrop()
            .into(view.findViewById<ImageView>(R.id.ivSetCard))

        view.setOnClickListener{
            val intent = Intent(context, Playlist::class.java)
            intent.putExtra("playlistID", dataitem.playlistID)
            intent.putExtra("albumThumb", dataitem.thumb)
            intent.putExtra("albumTitle", dataitem.title)
            intent.putExtra("uri", dataitem.playlistUrl)
            context.startActivity(intent)
        }
        container.addView(view)
        return view


    }


}