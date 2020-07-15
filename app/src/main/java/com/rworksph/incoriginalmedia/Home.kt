package com.rworksph.incoriginalmedia

import android.content.Context
import android.os.AsyncTask.execute
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

class Home : AppCompatActivity() {

    var task = FetchData()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        //Log.e("petsdata", FetchData().execute().status.toString())
        /*val sharedPreference =  getSharedPreferences("Data",Context.MODE_PRIVATE)
        val data = sharedPreference.getString("jsonData","")*/



    }




}