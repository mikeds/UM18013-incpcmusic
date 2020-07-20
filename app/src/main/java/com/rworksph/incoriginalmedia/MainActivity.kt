package com.rworksph.incoriginalmedia

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import java.net.URL


class MainActivity : AppCompatActivity() {
    //private val SPLASH_DELAY: Long = 3000 //3 seconds
    var data = Data()
    var SetsData = FetchData("https://api-v2.hearthis.at/mikeds/?type=playlists&page=1&count=20")
    var TracksData = FetchData("https://api-v2.hearthis.at/mikeds/?type=tracks&page=1&count=20")

    private val SPLASH_TIME_OUT:Long = 3000 // 1 sec
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        Handler().postDelayed({

            SetsData.execute()
            data.storeSetData(this, SetsData.get())
            TracksData.execute()
            data.storeTracksData(this,TracksData.get())



            Log.e("papasok na data sa app", SetsData.get())

            val intent = Intent(applicationContext, Home::class.java)
            startActivity(intent)
            finish()


            // close this activity
            finish()
        }, SPLASH_TIME_OUT)
    }





}