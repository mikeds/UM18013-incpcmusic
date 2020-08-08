package com.rworksph.incoriginalmedia

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONArray
import org.json.JSONObject
import java.net.URL
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.math.ceil


class MainActivity : AppCompatActivity() {
    //private val SPLASH_DELAY: Long = 3000 //3 seconds
    var data = Data()

    var allTracksData = FetchAllTracks("https://api-v2.hearthis.at/mikeds/")
    var PlaylistsData = FetchPlaylists("https://api-v2.hearthis.at/mikeds/?type=playlists&page=1&count=20")

    private val SPLASH_TIME_OUT:Long = 2000 // 1 sec
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        Handler().postDelayed({

            if (data.getFavorites(this) == ""){}else{
                val favArr = JSONArray(data.getFavorites(this))
                val newArr = JSONArray()
                for (i in 0 until favArr.length()){
                    val favdata= favArr.getJSONObject(i)
                    if (Calendar.getInstance() >= favdata.get("expiration") as Calendar){}
                    else{newArr.put(favdata)}
                }
                data.favorites(this, newArr.toString())
            }

            if (data.getAllSongs(this) == ""){
                allTracksData.execute()
                //Log.e("data2", data.getAllSongs(this).toString())
            }else{
                progressBar.setProgress(100)
                val intent = Intent(this, Home::class.java)
                startActivity(intent)
                finish()
            }




        }, SPLASH_TIME_OUT)
    }


    inner class FetchAllTracks(url0: String) : AsyncTask<String, Void, String>(){
        private val hdlr = Handler()
        val url1 = url0
         var dataList = ArrayList<HashMap<String, String>>()
        override fun onPreExecute() {
            super.onPreExecute()
            progressBar.max = 100
            progressBar.setProgress(29)
        }

        override fun doInBackground(vararg params: String?): String? {
            val Artist = URL(url1).readText(Charsets.UTF_8)
            val artistData = JSONObject(Artist)
            val track_count = artistData.getString("track_count").toDouble()
            val track_pages = ceil(track_count/20)

            val allTracksArray = JSONArray()
            for (i in 1 until track_pages.toInt()+1) run {
                val tracksData = URL("https://api-v2.hearthis.at/mikeds/?type=tracks&page=$i&count=20").readText(Charsets.UTF_8)
                val tracksArray = JSONArray(tracksData)
                //val PlaylistObj = JSONObject()
                for (j in 0 until tracksArray.length()){
                    val singleTrackData = tracksArray.getJSONObject(j)
                    allTracksArray.put(singleTrackData)
                }
                Log.e("jsonarrays", allTracksArray.length().toString())
                var prog = progressBar.progress
                progressBar.setProgress(prog+10)
            }

            return allTracksArray.toString()

        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            PlaylistsData.execute()
            data.storeAllSongs(this@MainActivity, result.toString())

            return
        }
    }

    inner class FetchPlaylists(url0: String) : AsyncTask<String, Void, String>(){
        val url1 = url0
        override fun onPreExecute() {
            super.onPreExecute()

        }

        override fun doInBackground(vararg params: String?): String? {
            val Playlists = URL(url1).readText(Charsets.UTF_8)
            val PlaylistsArr = JSONArray(Playlists)

            for (i in 0 until PlaylistsArr.length()){
                val PlaylistObj = PlaylistsArr.getJSONObject(i)
                val PlaylistID = PlaylistObj.getString("id")
                val PlaylistData = JSONArray()
                val PlaylistTracks = URL(PlaylistObj.getString("uri")).readText(Charsets.UTF_8)
                val PlaylistTracksArr = JSONArray(PlaylistTracks)
                for (j in 0 until PlaylistTracksArr.length()){
                    val tracks = PlaylistTracksArr.getJSONObject(j)
                    tracks.put("favorited", tracks.getBoolean("favorited").toString())
                    PlaylistData.put(tracks)
                }
                data.storePlaylistTracks(this@MainActivity,PlaylistID, PlaylistData.toString())
               // Log.e(PlaylistID, PlaylistData.toString())

            }



            return URL(url1).readText(
                Charsets.UTF_8
            )

        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)

            val usersArr = JSONArray(result)
            val PlayListsArr = JSONArray()
            for (i in 0 until usersArr.length()) {
                val singleUser = usersArr.getJSONObject(i)
                val PlaylistObj = JSONObject()

                PlaylistObj.put("id", singleUser.getString("id"))
                PlaylistObj.put("title", singleUser.getString("title"))
                PlaylistObj.put("uri", singleUser.getString("uri"))
                PlaylistObj.put("track_count", singleUser.getString("track_count"))
                PlaylistObj.put("thumb", singleUser.getString("thumb"))

                /*val map = HashMap<String, String>()
                map["title"] = singleUser.getString("title")
                map["id"] = singleUser.getString("description")
                map["duration"] = "Tracks: " + singleUser.getString("track_count")
                map["image"] = singleUser.getString("thumb")
                map["streamUrl"] = singleUser.getString("uri")

                dataList.add(map)*/
                PlayListsArr.put(PlaylistObj)

                //Log.e("data", PlaylistObj.toString())}
                // Log.e("data", PlayListsArr.toString())

             }
            progressBar.setProgress(100)
            Log.e("data", PlayListsArr.toString())
            data.storePlaylists(this@MainActivity, PlayListsArr.toString())
            val intent = Intent(this@MainActivity, Home::class.java)
            startActivity(intent)
            return

        }
    }





}