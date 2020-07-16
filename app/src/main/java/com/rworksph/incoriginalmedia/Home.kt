package com.rworksph.incoriginalmedia

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.OrientationHelper
import kotlinx.android.synthetic.main.activity_home.*
import org.json.JSONArray

class Home : AppCompatActivity() {

    //var task = FetchData("https://api-v2.hearthis.at/mikeds/?type=playlists")
    internal var datalist:MutableList<Sets> = ArrayList()
    var songList = ArrayList<HashMap<String, String>>()
    var data = Data()
    var init = inits()

    @SuppressLint("WrongConstant")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        init.initListData(this, datalist)
        init.initHomeSongs(this, songList)
        //initHomeSongs()
        val adapter = ListsAdapter(this, datalist)
        view_pager.adapter =adapter

        rvHome.layoutManager = LinearLayoutManager(this, OrientationHelper.HORIZONTAL, false)
        rvHome.adapter = HomeAdapter(this, songList)


        //Log.e("petsdata", FetchData().execute().status.toString())
        /*val sharedPreference =  getSharedPreferences("Data",Context.MODE_PRIVATE)
        val data = sharedPreference.getString("jsonData","")*/



    }







}