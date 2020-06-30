package com.example.original_music.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.original_music.R

private const val TAG = "MusicPlayerFragment"

class MusicPlayerFragment : Fragment() {

    companion object {
        fun newInstance(): MusicPlayerFragment {
            return MusicPlayerFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater,
                                container: ViewGroup?,
                                savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_musicplayer, container, false)
        }
}
