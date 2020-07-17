package com.rworksph.incoriginalmedia

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewParent
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_set_tracks.view.*
import java.text.FieldPosition

class SetTracksAdapter(private val context: Context,private val dataList: ArrayList<HashMap<String, String>>, val intent: Intent) : RecyclerView.Adapter<BaseViewHolder<*>>() {
    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_ITEMS = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): BaseViewHolder<*> {
        return when (position) {
            TYPE_HEADER -> {
                val view = LayoutInflater.from(context).inflate(R.layout.set_tracks_header, parent, false)


                view.findViewById<TextView>(R.id.tvSetTracksHeaderTitle).text = intent.getStringExtra("albumTitle")
                Picasso.get()
                    .load(intent.getStringExtra("albumThumb"))
                    .resize(300, 300)
                    .centerCrop()
                    .into(view.findViewById<ImageView>(R.id.ivSetTracksHeaderThumb))

                view.setOnClickListener{
                    Toast.makeText(context, "tse tse tse", Toast.LENGTH_SHORT).show()
                }
                HeaderViewHolder(view)
            }

            TYPE_ITEMS -> {
                val view = LayoutInflater.from(context).inflate(R.layout.set_tracks_items, parent, false)

                ItemViewHolder(view)

            }
            else -> throw IllegalArgumentException("INvalid view type")
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {


        if (position.equals(0)){

        }else{
            var dataitem = dataList[position-1]
            holder.itemView.findViewById<TextView>(R.id.tvSetTracksTitle).text = dataitem.get("title")

            Picasso.get()
                .load(dataitem.get("image"))
                .resize(300, 300)
                .centerCrop()
                .into(holder.itemView.findViewById<ImageView>(R.id.ivSetTracksThumb))
        }


    }

    override fun getItemViewType(position: Int): Int {
        if (position == 0) {
            return TYPE_HEADER
        }

        return TYPE_ITEMS
    }


    inner class HeaderViewHolder(itemView: View): BaseViewHolder<View>(itemView) {



        override fun bind(item: View) {

        }
    }

    inner class ItemViewHolder(itemView: View): BaseViewHolder<View>(itemView) {


        override fun bind(item: View) {

        }
    }
}