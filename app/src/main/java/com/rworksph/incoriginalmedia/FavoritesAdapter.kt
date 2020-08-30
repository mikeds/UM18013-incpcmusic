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
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.playlist_items.view.*
import org.json.JSONObject

class FavoritesAdapter (
    private val context: Context,
    private val dataList: ArrayList<HashMap<String, String>>
) : RecyclerView.Adapter<BaseViewHolder<*>>() {

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_ITEMS = 0
        private const val TYPE_EMPTY = 2
    }




    var home = Home()
    var tofavorites = ToFavorites()
    override fun onCreateViewHolder(parent: ViewGroup, position: Int): BaseViewHolder<*> {
        return when (position) {


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
        return dataList.size +1
    }



    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {


        if (position < dataList.size){
            var dataitem = dataList[position]
            holder.itemView.findViewById<TextView>(R.id.tvSetTracksTitle).text = dataitem.get("title")

            Picasso.get()
                .load(dataitem.get("image"))
                .resize(300, 300)
                .centerCrop()
                .into(holder.itemView.findViewById<ImageView>(R.id.ivSetTracksThumb))


            if (dataitem.get("downloaded").equals("0")){
                holder.itemView.tvDownloading.visibility = View.VISIBLE
                holder.itemView.llitem.setBackgroundColor(Color.parseColor("#ededed"))
            }else{
                holder.itemView.tvDownloading.visibility = View.GONE
                holder.itemView.llitem.setBackgroundColor(Color.parseColor("#ffffff"))
                holder.itemView.ibPlayPause.setOnClickListener { playsong(dataitem, position) }
                holder.itemView.setOnClickListener {playsong(dataitem, position)}
            }


            val popup = PopupMenu(context, holder.itemView)
            popup.inflate(R.menu.popup_menu)



            if (dataitem.get("favorited").equals("true")){
                popup.menu.findItem(R.id.action_popup_removetofavorites).setVisible(true)
                popup.menu.findItem(R.id.action_popup_addtofavorites).setVisible(false)
            }




            holder.itemView.ibMore.setOnClickListener(View.OnClickListener {
                popup.setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.action_popup_addtofavorites ->{
                            tofavorites.actionFavorite(context,"add", dataitem.get("trackID").toString(),"favorites", dataitem.get("streamUrl").toString(), dataitem.get("title").toString() )
                            true
                        }
                        R.id.action_popup_removetofavorites->{

                            tofavorites.actionFavorite(context,"remove", dataitem.get("trackID").toString(),"favorites", dataitem.get("streamUrl").toString() ,dataitem.get("title").toString() )
                            dataList.removeAt(position)
                            notifyDataSetChanged()
                            true
                        }
                        else -> {false}
                    }
                }
                popup.show()
            })
        }


    }
    fun playsong(item_data : HashMap<String, String>, position: Int){

            val trackData = JSONObject()
            trackData.put("title", item_data.get("title"))
            trackData.put("image", item_data.get("image"))
            trackData.put("duration", item_data.get("duration"))
            trackData.put("streamUrl", item_data.get("streamUrl"))
            trackData.put("trackID", item_data.get("trackID"))
            trackData.put("waveform_url", item_data.get("waveform_url"))
            trackData.put("id", position)
            trackData.put("from", "favorites")
            home.onMediaPlay(context, trackData)

    }
    override fun getItemViewType(position: Int): Int {

        if(position < dataList.size){
            return TYPE_ITEMS
        }else{
            return TYPE_EMPTY
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