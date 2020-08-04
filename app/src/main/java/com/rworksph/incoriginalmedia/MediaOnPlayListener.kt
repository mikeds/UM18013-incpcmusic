package com.rworksph.incoriginalmedia

import android.content.Context
import org.json.JSONObject

interface MediaOnPlayListener {
       fun onMediaPlay(context: Context, data: JSONObject)
}