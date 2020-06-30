package com.example.original_music.ui.dj

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.original_music.R

class DJFragment : Fragment() {

    private lateinit var djViewModel: DJViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        djViewModel =
            ViewModelProviders.of(this).get(DJViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_dj, container, false)
        val textView: TextView = root.findViewById(R.id.text_dj)
        djViewModel.text.observe(this, Observer {
            textView.text = it
        })
        return root
    }
}