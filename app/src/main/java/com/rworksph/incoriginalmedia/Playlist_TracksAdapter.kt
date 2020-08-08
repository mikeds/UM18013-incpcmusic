package com.rworksph.incoriginalmedia

import android.content.Context
import android.os.Build
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


class Playlist_TracksAdapter(
    private val context: Context,
    private val dataList: ArrayList<HashMap<String, String>>,
    val intent: JSONObject
) : RecyclerView.Adapter<BaseViewHolder<*>>() {

    val playlistFragment = PlaylistFragment()

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_ITEMS = 1
    }

    val currentPosition : Int = 0


    var mediaControllerManager = MediaControllerManager()
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

                view.setOnClickListener{
                    //Toast.makeText(context, "tse tse tse", Toast.LENGTH_SHORT).show()
                }
                HeaderViewHolder(view)
            }

            TYPE_ITEMS -> {
                val view = LayoutInflater.from(context).inflate(R.layout.playlist_items, parent, false)

                ItemViewHolder(view)

            }
            else -> throw IllegalArgumentException("INvalid view type")
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }



    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
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

            holder.itemView.setOnClickListener {
                mediaControllerManager.mediaControllerManager(dataitem.get("streamUrl").toString())


                val trackData = JSONObject()
                trackData.put("title", dataitem.get("title"))
                trackData.put("image", dataitem.get("image"))
                trackData.put("duration", dataitem.get("duration"))
                trackData.put("streamUrl", dataitem.get("streamUrl"))
                trackData.put("trackID", dataitem.get("trackID"))
                trackData.put("id", position)
                trackData.put("from", intent.getString("playlistID"))
                home.onMediaPlay(context, trackData)
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

                            tofavorites.actionFavorite(context,"add", dataitem.get("trackID").toString(),intent.getString("playlistID"),dataitem.get("streamUrl").toString() )
                            true
                        }
                        R.id.action_popup_removetofavorites->{
                            tofavorites.actionFavorite(context,"remove", dataitem.get("trackID").toString(),intent.getString("playlistID"),dataitem.get("streamUrl").toString() )
                            true
                        }
                        else -> {false}
                    }
                }
                popup.show()
            })
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


