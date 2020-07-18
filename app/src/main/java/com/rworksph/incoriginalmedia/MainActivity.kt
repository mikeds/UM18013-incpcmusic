package com.rworksph.incoriginalmedia

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import java.net.URL


class MainActivity : AppCompatActivity() {
    private var mDelayHandler: Handler? = null
    private val SPLASH_DELAY: Long = 3000 //3 seconds
    var data = Data()
    var SetsData = FetchData("https://api-v2.hearthis.at/mikeds/?type=playlists&page=1&count=20")
    var TracksData = FetchData("https://api-v2.hearthis.at/mikeds/?type=tracks&page=1&count=20")

    internal val mRunnable: Runnable = Runnable {
        if (!isFinishing) {

            SetsData.execute()
            data.storeSetData(this, SetsData.get())
            TracksData.execute()
            data.storeTracksData(this,TracksData.get())



            Log.e("papasok na data sa app", SetsData.get())

            val intent = Intent(applicationContext, Home::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Initialize the Handler
        mDelayHandler = Handler()

        //Navigate with delay
        mDelayHandler!!.postDelayed(mRunnable, SPLASH_DELAY)

    }

    public override fun onDestroy() {

        if (mDelayHandler != null) {
            mDelayHandler!!.removeCallbacks(mRunnable)
        }

        super.onDestroy()
    }
}