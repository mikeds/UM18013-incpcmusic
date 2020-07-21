package com.rworksph.incoriginalmedia

import android.content.Context
import android.util.Log
import org.json.JSONArray

class Data {

    val songList = ArrayList<HashMap<String, String>>()

    fun storeSetData(context: Context, data: String) {
        val sharedPreference =  context.getSharedPreferences("Data",Context.MODE_PRIVATE)
        var editor = sharedPreference.edit()
        editor.putString("SetData",data)
        editor.commit()

    }

    fun getStoredSetData(context: Context): String? {
        val sharedPreference =  context.getSharedPreferences("Data",Context.MODE_PRIVATE)
        val data = sharedPreference.getString("SetData","")
        return data
    }

    fun storeTracksData(context: Context, data: String) {

        val sharedPreference =  context.getSharedPreferences("Data",Context.MODE_PRIVATE)
        var editor = sharedPreference.edit()
        editor.putString("TracksData",data)
        editor.commit()
    }

    fun getTracksData(context: Context): String? {
        val sharedPreference =  context.getSharedPreferences("Data",Context.MODE_PRIVATE)
        val data = sharedPreference.getString("TracksData","")
        return data
    }

    fun storeSetTracksData(context: Context, data: String) {

        val sharedPreference =  context.getSharedPreferences("Data",Context.MODE_PRIVATE)
        var editor = sharedPreference.edit()
        editor.putString("SetTracksData",data)
        editor.commit()
    }

    fun getSetTracksData(context: Context): String? {
        val sharedPreference =  context.getSharedPreferences("Data",Context.MODE_PRIVATE)
        val data = sharedPreference.getString("SetTracksData","")
        return data
    }






}