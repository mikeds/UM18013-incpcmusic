package com.rworksph.incoriginalmedia

import android.content.Context
import android.os.AsyncTask.execute
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_home.*
import org.json.JSONArray

class Home : AppCompatActivity() {

    //var task = FetchData("https://api-v2.hearthis.at/mikeds/?type=playlists")
    internal var datalist:MutableList<Sets> = ArrayList()
    var data = Data()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)


        //data.getStoredJsonData(this)
        initListData()
        val adapter = ListsAdapter(this, datalist)
        view_pager.adapter =adapter


        //Log.e("petsdata", FetchData().execute().status.toString())
        /*val sharedPreference =  getSharedPreferences("Data",Context.MODE_PRIVATE)
        val data = sharedPreference.getString("jsonData","")*/



    }
    private fun initListData(){
        val usersArr = JSONArray(data.getStoredJsonData(this))
        for (i in 0 until usersArr.length()) {
            val singleUser = usersArr.getJSONObject(i)

            datalist.add(Sets(singleUser.getString("title"), singleUser.getString("thumb"), singleUser.getString("track_count"), singleUser.getString("uri")))

        }

    }




}