package com.rworksph.incoriginalmedia

import android.content.Context

class Datas {

    fun storeJsonData(context: Context, data: String) {
        val sharedPreference =  context.getSharedPreferences("Data",Context.MODE_PRIVATE)
        var editor = sharedPreference.edit()
        editor.putString("jsonData",data)
        editor.commit()
    }

    fun getStoreData(context: Context, data: String){
        val sharedPreference =  context.getSharedPreferences("Data",Context.MODE_PRIVATE)
        val data = sharedPreference.getString("jsonData",data)
    }




}