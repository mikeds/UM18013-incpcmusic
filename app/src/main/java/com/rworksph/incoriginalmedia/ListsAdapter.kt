package com.rworksph.incoriginalmedia

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.viewpager.widget.PagerAdapter
import com.squareup.picasso.Picasso

class ListsAdapter(internal var context: Context, internal var list: List<Sets>):PagerAdapter() {

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

        val view = LayoutInflater.from(context).inflate(R.layout.set_card_item, container, false)

        view.findViewById<TextView>(R.id.tvSetTitle).text = dataitem.songSetTitle
        view.findViewById<TextView>(R.id.tvSetSongCount).text = dataitem.songSetSongCount

        Picasso.get()
            .load(dataitem.songSetImage)
            .resize(300, 300)
            .centerCrop()
            .into(view.findViewById<ImageView>(R.id.ivSetCard))

        view.setOnClickListener{
            val intent = Intent(context, SetTracks::class.java)
            intent.putExtra("albumThumb", dataitem.songSetImage)
            intent.putExtra("albumTitle", dataitem.songSetTitle)
            intent.putExtra("uri", dataitem.songSetUrl)
            context.startActivity(intent)
        }
        container.addView(view)
        return view


    }


}