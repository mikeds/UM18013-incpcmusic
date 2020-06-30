package com.example.original_music.ui.songs.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.original_music.R
import com.example.original_music.model.Track
import com.squareup.picasso.Picasso

class TrackViewHolder(view: View): RecyclerView.ViewHolder(view) {
    var thumbnail: ImageView = view.findViewById(R.id.thumbnailTrackImageView)
    var title: TextView = view.findViewById(R.id.titleTrackTextView)
}

class TracksAdapter(private var tracks: List<Track>): RecyclerView.Adapter<TrackViewHolder>() {
    private val TAG = "TracksAdapter"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        // Called by the layout manager when it needs a new view
        Log.d(TAG, "onCreateViewHolder new view requested")
        val view = LayoutInflater.from(parent.context).inflate(R.layout.track_item, parent, false)
        return TrackViewHolder(view)
    }

    override fun getItemCount(): Int {
        Log.d(TAG, "getItemCount called")
        return if(tracks.isNotEmpty()) tracks.size else 0
    }

    fun loadNewData(newPlayLists: List<Track>) {
        tracks = newPlayLists
        notifyDataSetChanged()
    }

    fun getPlaylist(position: Int): Track? {
        return if(tracks.isNotEmpty()) tracks[position] else null
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        val track = tracks[position]
        Log.d(TAG, "onBindViewHolder: ${track.title} --> $position")
        Picasso.get().load(track.image)
            .error(R.drawable.placeholder)
            .placeholder(R.drawable.placeholder)
            .into(holder.thumbnail)

        holder.title.text = track.title}
}