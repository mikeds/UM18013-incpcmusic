package com.example.original_music.ui.youtube

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class YoutubeViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is youtube Fragment"
    }
    val text: LiveData<String> = _text
}