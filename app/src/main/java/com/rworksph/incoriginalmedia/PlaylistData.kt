package com.rworksph.incoriginalmedia

import android.content.Context
import org.json.JSONObject

interface PlaylistData {
    fun playlistData(context: Context, data: JSONObject)
}