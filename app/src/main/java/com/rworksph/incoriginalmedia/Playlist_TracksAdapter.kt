package com.rworksph.incoriginalmedia

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.playlist_items.view.*
import org.json.JSONObject


class Playlist_TracksAdapter(
    private val context: Context,
    private val dataList: ArrayList<HashMap<String, String>>,
    val intent: JSONObject
) : RecyclerView.Adapter<BaseViewHolder<*>>() {

    val data = Data()


    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_ITEMS = 1
        private const val TYPE_EMPTY = 2
    }

    var rowIndex = -1

    var home = Home()
    var tofavorites = ToFavorites()
    override fun onCreateViewHolder(parent: ViewGroup, position: Int): BaseViewHolder<*> {
        return when (position) {
            TYPE_HEADER -> {
                val view = LayoutInflater.from(context).inflate(R.layout.playlist_header, parent, false)


                view.findViewById<TextView>(R.id.tvSetTracksHeaderTitle).text = intent.getString("albumTitle")
                Picasso.get()
                    .load(intent.getString("albumThumb"))
                    .resize(300, 300)
                    .centerCrop()
                    .into(view.findViewById<ImageView>(R.id.ivSetTracksHeaderThumb))


                HeaderViewHolder(view)
            }

            TYPE_ITEMS -> {
                val view = LayoutInflater.from(context).inflate(R.layout.playlist_items, parent, false)

                ItemViewHolder(view)

            }

            TYPE_EMPTY -> {
                val view = LayoutInflater.from(context).inflate(R.layout.empty_playlist_items, parent, false)

                EmptyViewHolder(view)

            }

            else -> throw IllegalArgumentException("INvalid view type")
        }
    }

    override fun getItemCount(): Int {
        return dataList.size + 2
    }



    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {


        if (position.equals(0)){

        }else if(position <= dataList.size){
            val dataitem = dataList[position-1]
            holder.itemView.findViewById<TextView>(R.id.tvSetTracksTitle).text = dataitem["title"]

            Picasso.get()
                .load(dataitem["image"])
                .resize(300, 300)
                .centerCrop()
                .into(holder.itemView.findViewById<ImageView>(R.id.ivSetTracksThumb))


            holder.itemView.ibPlayPause.setOnClickListener {
                /*rowIndex = position
                notifyDataSetChanged()*/
                playsong(dataitem, position) }

            holder.itemView.setOnClickListener {
                /*rowIndex = position
                notifyDataSetChanged()*/
                playsong(dataitem, position)}

            /*if (rowIndex == position){
                holder.itemView.ibPlayPause.setImageResource(R.drawable.ic_baseline_pause_24_d1a538)
            }else{
                holder.itemView.ibPlayPause.setImageResource(R.drawable.ic_baseline_play_arrow_24_d1a538)
            }*/

            if (data.getFavorites(context) != ""){
                if (data.getFavorites(context).toString().contains(dataitem["trackID"].toString())){
                    dataitem["favorited"] = "true"
                }
            }

            val popup = PopupMenu(context, holder.itemView)
            popup.inflate(R.menu.popup_menu)
            if (dataitem["favorited"].equals("true")){
                popup.menu.findItem(R.id.action_popup_removetofavorites).isVisible = true
                popup.menu.findItem(R.id.action_popup_addtofavorites).isVisible = false
            }

            holder.itemView.ibMore.setOnClickListener {

                popup.setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.action_popup_addtofavorites ->{

                            //dataitem["favorited"] = "true"
                            tofavorites.actionFavorite(context,
                                "add",
                                dataitem["trackID"].toString(),
                                intent.getString("playlistID"),
                                dataitem["streamUrl"].toString(),
                                dataitem["title"].toString()  )
                            notifyItemChanged(holder.adapterPosition)

                            true
                        }
                        R.id.action_popup_removetofavorites->{
                            dataitem["favorited"] = "false"
                            //Log.e("from", intent.getString("playlistID"))
                            tofavorites.actionFavorite(context,
                                "remove",
                                dataitem["trackID"].toString(),
                                intent.getString("playlistID"),
                                dataitem["streamUrl"].toString(),
                                dataitem["title"].toString()  )
                            notifyItemChanged(holder.adapterPosition)
                            true
                        }
                        else -> {false}

                    }

                }
                popup.show()
            }
        }
    }

    fun playsong(item_data : HashMap<String, String>, position: Int){
        Log.e("connectivity", data.isConnected(context).toString())

        if (data.isConnected(context)==true){

            val trackData = JSONObject()
            trackData.put("title", item_data["title"])
            trackData.put("image", item_data["image"])
            trackData.put("duration", item_data["duration"])
            trackData.put("streamUrl", item_data["streamUrl"])
            trackData.put("trackID", item_data["trackID"])
            trackData.put("waveform_url", item_data["waveform_url"])
            trackData.put("id", position-1)
            trackData.put("from", intent.getString("playlistID"))
            home.onMediaPlay(context, trackData)
        }else{
            home.songNotAvailable(context)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            position == 0 -> {
                TYPE_HEADER
            }
            position <= dataList.size -> {
                TYPE_ITEMS
            }
            else -> {
                TYPE_EMPTY
            }
        }


    }


    inner class HeaderViewHolder(itemView: View): BaseViewHolder<View>(itemView) {
        override fun bind(item: View) {

        }
    }

    inner class ItemViewHolder(itemView: View): BaseViewHolder<View>(itemView) {
        override fun bind(item: View) {

        }
    }

    inner class EmptyViewHolder(itemView: View): BaseViewHolder<View>(itemView) {
        override fun bind(item: View) {

        }
    }








}


