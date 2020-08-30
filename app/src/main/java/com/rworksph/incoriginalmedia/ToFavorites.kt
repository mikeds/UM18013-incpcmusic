package com.rworksph.incoriginalmedia

import android.content.Context
import android.os.AsyncTask
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class ToFavorites {
    val data=Data()



    @RequiresApi(Build.VERSION_CODES.KITKAT)
    fun actionFavorite(context: Context, action:String, trackID:String, from: String, url:String, title: String){
        val home = Home()
        when(action){
            "add" ->{
                if(data.getFavorites(context).equals("")){
                    addToFavorites(context, trackID, from, url)
                }else{
                    if (JSONArray(data.getFavorites(context)).length() >= 15){
                        Toast.makeText(context, "Maximum Favorite Count Reached", Toast.LENGTH_LONG).show()
                    }else{
                        home.downloadingMusic(context, title,"Downloading" )
                        addToFavorites(context, trackID, from, url)
                    }
                }

            }
            "remove" -> {

                removeToFavorites(context, trackID, from, url)
            }
        }

    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    fun addToFavorites(context: Context, trackID: String, from: String, url: String){
        val playlistArr = JSONArray(data.getPlaylistTracks(context,from))
        var favArr = JSONArray()
        if (data.getFavorites(context).toString() == ""){
            favArr = JSONArray()
        }else{
            favArr = JSONArray(data.getFavorites(context))
        }

        //if (favArr.toString().contains("\"id\":\""+trackID+"\""))
        val date = Calendar.getInstance()
        date.add(Calendar.DAY_OF_MONTH, 15)
        val expireTime = SimpleDateFormat("yyyyMMdd").format(date.time)

        for (i in 0 until playlistArr.length()) {
            val playlistData = playlistArr.getJSONObject(i)
            if (playlistData.getString("id").equals(trackID)){
                playlistData.put("favorited","true")
                playlistData.put("downloadable","0")
                playlistData.put("fromPlaylist",from)
                playlistData.put("expiration", expireTime)
                favArr.put(playlistData)

            }
        }

        data.favorites(context, favArr.toString())
        Log.e("favarr", favArr.toString())
        val task = MyDownloadTask(context,
            url,
            "$trackID.mp3",
            trackID.toInt(), //if you don't have id then you can pass any value here
            object : MyDownloadTask.DownloadListener {
                override fun onDownloadComplete(download: Boolean, pos: Int) {
                    if (download) {
                        val home = Home()

                        var title = ""
                        val newfavorites = JSONArray()
                        val favarray = JSONArray(data.getFavorites(context))
                        for (i in 0 until favarray.length()){
                            val favoriteData = favarray.getJSONObject(i)
                            if (favoriteData.getString("id") == trackID){
                                favoriteData.put("downloadable","1")
                                title = favoriteData.getString("title")
                            }

                            newfavorites.put(favoriteData)
                        }
                        home.downloadingMusic(context, title, "Added to Favorites")
                        data.favorites(context,newfavorites.toString())
                    }
                }

                override fun downloadProgress(status: Int) {

                }
            })
        task.execute()

    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    fun removeToFavorites(
        context: Context,
        trackID: String,
        from: String,
        url: String
    ){


        val favarr = JSONArray(data.getFavorites(context))
        val newFav = JSONArray()
        for (i in 0 until favarr.length()){
            val favData = favarr.getJSONObject(i)
            if (favData.getString("id") != trackID){
                newFav.put(favData)
            }
        }

        data.favorites(context, newFav.toString())
        val folder = File(context.filesDir, "elpaboritos")
        val documentFile = File("$folder/$trackID.mp3")
        documentFile.delete()
        //call on data change to playlist fragment
    }



}