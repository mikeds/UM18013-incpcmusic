package com.rworksph.incoriginalmedia

import android.content.Context
import android.os.AsyncTask
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import org.json.JSONArray
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class ToFavorites {
    val data=Data()



    @RequiresApi(Build.VERSION_CODES.KITKAT)
    fun actionFavorite(context: Context, action:String, trackID:String, from: String, url:String){
        when(action){
            "add" ->{
                if(data.getFavorites(context).equals("")){
                    addToFavorites(context, trackID, from, url)
                }else{
                    if (JSONArray(data.getFavorites(context)).length() >= 15){
                        Toast.makeText(context, "Maximum Favorite Count Reached", Toast.LENGTH_LONG).show()
                    }else{
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
        val oldArr = JSONArray(data.getPlaylistTracks(context,from))
        val newArr = JSONArray()
        var favArr = JSONArray()
        if (data.getFavorites(context).toString() == ""){
            favArr = JSONArray()
        }else{
            favArr = JSONArray(data.getFavorites(context))
        }

        //if (favArr.toString().contains("\"id\":\""+trackID+"\""))
        val date = Calendar.getInstance()
        date.add(Calendar.DAY_OF_MONTH, 1)
        val expireTime = SimpleDateFormat("yyyyMMdd").format(date.time)
        for (i in 0 until oldArr.length()) {
            val oldData = oldArr.getJSONObject(i)
            if (oldData.getString("id").equals(trackID)){
                oldData.put("favorited","true")
                oldData.put("expiration", expireTime)
                favArr.put(oldData)
                newArr.put(oldData)
            }else{
                newArr.put(oldData)
            }
        }
        data.storePlaylistTracks(context,from, newArr.toString())
        Log.e("favarr", favArr.toString())


        val task = MyDownloadTask(context,
            url,
            "$trackID.mp3",
            trackID.toInt(), //if you don't have id then you can pass any value here
            object : MyDownloadTask.DownloadListener {
                override fun onDownloadComplete(download: Boolean, pos: Int) {
                    if (download) {

                        data.favorites(context, favArr.toString())
                        Toast.makeText(context, "added to favorites", Toast.LENGTH_SHORT)
                            .show()
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

        val oldArr = JSONArray(data.getPlaylistTracks(context,from))
        val newArr = JSONArray()
        val newfav = JSONArray()
        val favArr = JSONArray(data.getFavorites(context))
        for (i in 0 until oldArr.length()){
            val oldData = oldArr.getJSONObject(i)
            if (oldData.getString("id") == trackID){
                oldData.put("favorited","false")
                newArr.put(oldData)
            }else{newArr.put(oldData)}

            if (oldData.getString("favorited") == "true"){
                newfav.put(oldData)
            }
        }
        /*for (i in 0 until favArr.length()){
            Log.e("TAG", i.toString())
            val faveData = favArr.getJSONObject(i)
            if (faveData.getString("id") != trackID){
                newfav.put(faveData)

            }
        }


        for (i in 0 until oldArr.length()) {
            val oldData = oldArr.getJSONObject(i)
            if (oldData.getString("id").equals(trackID)){
                oldData.put("favorited","false")

                newArr.put(oldData)

            }else{

                newArr.put(oldData)
            }
        }*/


        data.storePlaylistTracks(context,from, newArr.toString())
        val checkjson = JSONArray(data.getPlaylistTracks(context,from))
        Log.e("remove", checkjson.length().toString())

        data.favorites(context,newfav.toString())
        val folder = File(context.filesDir, "elpaboritos")
        val documentFile = File("$folder/$trackID.mp3")
        documentFile.delete()
        //call on data change to playlist fragment
    }



}