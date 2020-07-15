package com.rworksph.incoriginalmedia

import android.content.Context

class Data {

    fun storeJsonData(context: Context, data: String) {
        val sharedPreference =  context.getSharedPreferences("Data",Context.MODE_PRIVATE)
        var editor = sharedPreference.edit()
        editor.putString("jsonData",data)
        editor.commit()
    }

    fun getStoredJsonData(context: Context): String? {
        val sharedPreference =  context.getSharedPreferences("Data",Context.MODE_PRIVATE)
        val data = sharedPreference.getString("jsonData","")
        return data
    }

    fun getSetTracks(){

    }




}