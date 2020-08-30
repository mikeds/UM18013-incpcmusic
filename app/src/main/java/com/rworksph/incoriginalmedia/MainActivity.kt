package com.rworksph.incoriginalmedia

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseApp
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONArray
import org.json.JSONObject
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.ceil
import com.google.firebase.crashlytics.FirebaseCrashlytics

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {
    //private val SPLASH_DELAY: Long = 3000 //3 seconds
    var data = Data()

    var allTracksData = FetchAllTracks("https://api-v2.hearthis.at/incplaylist/")
    var PlaylistsData = FetchPlaylists("https://api-v2.hearthis.at/incplaylist/")
    private val SPLASH_TIME_OUT:Long = 2000 // 1 sec
    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        createNotificationChannel()

        Handler().postDelayed({
            val currentTime = SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance().time)



            if (data.getFavorites(this) != ""){
                val favArr = JSONArray(data.getFavorites(this))
                val newArr = JSONArray()
                for (i in 0 until favArr.length()){
                    val favdata= favArr.getJSONObject(i)
                    if (currentTime.toInt() <= favdata.get("expiration").toString().toInt()){
                        newArr.put(favdata)
                    }else{
                        val NewData = JSONArray()
                        val playlistArr = JSONArray(data.getPlaylistTracks(this,favdata.getString("fromPlaylist")))
                        for(j in 0 until playlistArr.length()){
                            val listData = playlistArr.getJSONObject(j)
                            if (listData.getString("id") == favdata.getString("id")){
                                listData.put("favorited", "false")
                                NewData.put(listData)
                            }else{
                                NewData.put(listData)
                            }
                        }
                        data.storePlaylistTracks(this, favdata.getString("fromPlaylist"), NewData.toString())
                    }
                }

                 data.favorites(this, newArr.toString())
            }


            if(isConnectingToInternet()){


                if(data.getAllSongs(this) == ""){
                    allTracksData.execute()
                }else{
                    progressSplash.setProgress(100)
                    val playlistcount = JSONArray(checkAllTracks("https://api-v2.hearthis.at/incplaylist/").execute().get()).length()

                    val playlistcount2 = JSONArray(data.getAllSongs(this)).length()
                    if (playlistcount > playlistcount2){
                        allTracksData.execute()

                    }else{

                        val intent = Intent(this, Home::class.java)
                        startActivity(intent)
                        finish()
                    }

                }
            }else{
                data.connectivity(this, false)

                if(data.getAllSongs(this) == ""){
                    Toast.makeText(this, "Internet Connection Needed", Toast.LENGTH_LONG).show()
                }else{
                    progressSplash.setProgress(100)
                    val intent = Intent(this, Home::class.java)
                    startActivity(intent)
                    finish()
                }

            }





        }, SPLASH_TIME_OUT)
    }

    fun isConnectingToInternet(): Boolean {
        val connectivity =
            this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivity != null) {
            val info = connectivity.allNetworkInfo
            if (info != null) for (i in info.indices) if (info[i]
                    .state == NetworkInfo.State.CONNECTED
            ) {
                return true
            }
        }
        return false
    }
    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "INC Playlist"
            val descriptionText = ""
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val CHANNEL_ID = "incom"


            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText

            }
            channel.setSound(null,null)
            channel.vibrationPattern = longArrayOf(0)
            channel.setShowBadge(false)
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    inner class checkAllTracks(url0: String) : AsyncTask<String, Void, String>(){

        val url1 = url0

        override fun onPreExecute() {

        }

        override fun doInBackground(vararg params: String?): String? {

            val Artist = URL(url1).readText(Charsets.UTF_8)
            val artistData = JSONObject(Artist)
            val track_count = artistData.getString("track_count").toDouble()
            val track_pages = ceil(track_count/20)

            progressSplash.max = track_count.toInt()

            val allTracksArray = JSONArray()
            for (i in 1 until track_pages.toInt()+1) run {
                val tracksData = URL("https://api-v2.hearthis.at/incplaylist/?type=tracks&page=$i&count=20").readText(Charsets.UTF_8)
                val tracksArray = JSONArray(tracksData)

                for (j in 0 until tracksArray.length()){
                    val singleTrackData = tracksArray.getJSONObject(j)
                    allTracksArray.put(singleTrackData)
                }
                //Log.e("jsonarrays", allTracksArray.length().toString())
                progressSplash.setProgress(allTracksArray.length())
            }

            return allTracksArray.toString()

        }
    }

    inner class FetchAllTracks(url0: String) : AsyncTask<String, Void, String>(){

        val url1 = url0

        override fun onPreExecute() {
            super.onPreExecute()

        }

        override fun doInBackground(vararg params: String?): String? {
            val Artist = URL(url1).readText(Charsets.UTF_8)
            val artistData = JSONObject(Artist)
            val track_count = artistData.getString("track_count").toDouble()
            val track_pages = ceil(track_count/20)
            progressSplash.max = track_count.toInt()

            val allTracksArray = JSONArray()
            for (i in 1 until track_pages.toInt()+1) run {
                val tracksData = URL("https://api-v2.hearthis.at/incplaylist/?type=tracks&page=$i&count=20").readText(Charsets.UTF_8)
                val tracksArray = JSONArray(tracksData)
                //val PlaylistObj = JSONObject()
                for (j in 0 until tracksArray.length()){
                    val singleTrackData = tracksArray.getJSONObject(j)
                    allTracksArray.put(singleTrackData)
                }
               // Log.e("jsonarrays", allTracksArray.length().toString())

                progressSplash.setProgress(allTracksArray.length()/2)
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
            val PlaylistsData = JSONObject(Playlists)
            val playlist_count = PlaylistsData.getString("playlist_count").toDouble()
            val playlist_pages = ceil(playlist_count/20)
            val max = progressSplash.max
            val remaining = max - progressSplash.progress
           // Log.e("remaining: $remaining", playlist_count.toString())
            val progress = remaining/playlist_count

            val playlistArray = JSONArray()
            for (i in 1 until playlist_pages.toInt()+1){
                val Playlists = URL("https://api-v2.hearthis.at/incplaylist/?type=playlists&page=$i&count=20").readText(Charsets.UTF_8)
                val PlaylistsArr = JSONArray(Playlists)

                for (j in 0 until PlaylistsArr.length()){

                    val PlaylistObj = PlaylistsArr.getJSONObject(j)
                    playlistArray.put(PlaylistObj)
                    val PlaylistID = PlaylistObj.getString("id")

                    val PlaylistData = JSONArray()
                    val PlaylistTracks = URL(PlaylistObj.getString("uri")).readText(Charsets.UTF_8)
                    val PlaylistTracksArr = JSONArray(PlaylistTracks)

                    for (k in 0 until PlaylistTracksArr.length()){
                        val tracks = PlaylistTracksArr.getJSONObject(k)
                        tracks.put("favorited", tracks.getBoolean("favorited").toString())

                        PlaylistData.put(tracks)
                    }
                    data.storePlaylistTracks(this@MainActivity,PlaylistID, PlaylistData.toString())

                    progressSplash.progress = progressSplash.progress+progress.toInt()
                    //Log.e("progress", progress.toString())
                }
            }
            return playlistArray.toString()

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

                PlayListsArr.put(PlaylistObj)



             }



            data.storePlaylists(this@MainActivity, PlayListsArr.toString())
            progressSplash.progress = progressSplash.max
            val intent = Intent(this@MainActivity, Home::class.java)
            startActivity(intent)
            finish()
            return

        }
    }
    





}