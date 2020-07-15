package com.rworksph.incoriginalmedia

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log


class MainActivity : AppCompatActivity() {
    private var mDelayHandler: Handler? = null
    private val SPLASH_DELAY: Long = 3000 //3 seconds
    var task = FetchData()
    internal val mRunnable: Runnable = Runnable {
        if (!isFinishing) {

            task.execute()

            val sharedPreference =  getSharedPreferences("Data",Context.MODE_PRIVATE)
            var editor = sharedPreference.edit()
            editor.putString("jsonData",task.get())
            editor.commit()

            Log.e("papasok na data sa app", task.get())
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