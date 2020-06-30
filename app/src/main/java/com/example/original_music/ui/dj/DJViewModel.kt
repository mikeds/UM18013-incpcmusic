package com.example.original_music.ui.dj

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DJViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is DJ Fragment"
    }
    val text: LiveData<String> = _text
}