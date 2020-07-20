package com.rworksph.incoriginalmedia

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.OrientationHelper
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.media_controller.*

class Home : AppCompatActivity(),MediaOnPlayListener {

    //var task = FetchData("https://api-v2.hearthis.at/mikeds/?type=playlists")
    internal var datalist:MutableList<Sets> = ArrayList()
    var songList = ArrayList<HashMap<String, String>>()
    var data = Data()
    var init = inits()

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("WrongConstant")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)


        var mediaControllerManager = MediaControllerManager()

        init.initListData(this, datalist)
        init.initHomeSongs(this, songList)
        //initHomeSongs()
        val adapter = ListsAdapter(this, datalist)
        view_pager.adapter =adapter

        rvHome.layoutManager = LinearLayoutManager(this, OrientationHelper.HORIZONTAL, false)
        rvHome.adapter = HomeAdapter(this, songList)


        //bottomSheetLayout.visibility = View.GONE
        Toast.makeText(this, mediaControllerManager.Sig.toString(), Toast.LENGTH_SHORT).show()
        ivPlayPauseBurron.setOnClickListener{
            mediaControllerManager.mediaPlayer.pause()
            ivPlayPauseBurron.setImageResource(R.drawable.ic_baseline_play_arrow_24_d1a538)
        }
        bHome.setOnClickListener{
            //mediaControllerManager.play("https://api-v2.hearthis.at/mikeds/spotlight-pinugay-daryltanda-ng-pag-ibig-ng-ama/listen/?s=zFr")
        }

       /* bHome.setOnClickListener{
            bottomSheetLayout.visibility = View.VISIBLE
        }*/
        //Log.e("petsdata", FetchData().execute().status.toString())
        /*val sharedPreference =  getSharedPreferences("Data",Context.MODE_PRIVATE)
        val data = sharedPreference.getString("jsonData","")*/



    }

     override fun onMediaPlay(context: Context) {
         (context as? Home)?.ivPlayPauseBurron?.setImageResource(R.drawable.ic_baseline_pause_24_d1a538)

    }


}