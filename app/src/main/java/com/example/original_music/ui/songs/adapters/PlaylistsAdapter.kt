package com.example.original_music.ui.songs.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.original_music.R
import com.example.original_music.model.Playlist
import com.squareup.picasso.Picasso

class PlaylistViewHolder(view: View): RecyclerView.ViewHolder(view) {
    var thumbnail: ImageView = view.findViewById(R.id.thumbnailPlaylistImageView)
    var title: TextView = view.findViewById(R.id.titlePlaylistTextView)
}

class PlaylistsAdapter(private var playlists : List<Playlist>) : RecyclerView.Adapter<PlaylistViewHolder>() {
    private val TAG = "PlaylistsAdapter"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        // Called by the layout manager when it needs a new view
        Log.d(TAG, "onCreateViewHolder new view requested")
        val view = LayoutInflater.from(parent.context).inflate(R.layout.playlist_item, parent, false)
        return PlaylistViewHolder(view)
    }

    override fun getItemCount(): Int {
        Log.d(TAG, "getItemCount called")
        return if(playlists.isNotEmpty()) playlists.size else 0
    }

    fun loadNewData(newPlayLists: List<Playlist>) {
        playlists = newPlayLists
        notifyDataSetChanged()
    }

    fun getPlaylist(position: Int): Playlist? {
        return if(playlists.isNotEmpty()) playlists[position] else null
    }

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        // Called by layout manager when it wants new data in an existing view

        val playlist = playlists[position]
        Log.d(TAG, "onBindViewHolder: ${playlist.title} --> $position")
        Picasso.get().load(playlist.image)
            .error(R.drawable.placeholder)
            .placeholder(R.drawable.placeholder)
            .into(holder.thumbnail)

        holder.title.text = playlist.title
    }


}