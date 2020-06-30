package com.example.original_music.ui.youtube

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.original_music.R

class YoutubeFragment : Fragment() {

    private lateinit var youtubeViewModel: YoutubeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        youtubeViewModel =
            ViewModelProviders.of(this).get(YoutubeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_youtube, container, false)
        val textView: TextView = root.findViewById(R.id.text_youtube)
        youtubeViewModel.text.observe(this, Observer {
            textView.text = it
        })
        return root
    }
}