package com.rworksph.incoriginalmedia

import android.content.Context
import android.os.Build
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContentProviderCompat.requireContext
import org.json.JSONArray
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import java.util.*

class ToFavorites {
    val data=Data()



    @RequiresApi(Build.VERSION_CODES.KITKAT)
    fun actionFavorite(context: Context, action:String, trackID:String, from: String, url:String){
        when(action){
            "add" ->{
                if (JSONArray(data.getFavorites(context)).length() >= 15){
                    Toast.makeText(context, "Maximum Favorite Count Reached", Toast.LENGTH_LONG).show()
                }else{
                    addToFavorites(context, trackID, from, url)
                }

            }
            "remove" -> {
                removeToFavorites(context, trackID, from)
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

        for (i in 0 until oldArr.length()) {
            val oldData = oldArr.getJSONObject(i)
            if (oldData.getString("id").equals(trackID)){
                oldData.put("favorited","true")
                oldData.put("expiration", Calendar.getInstance().add(Calendar.DAY_OF_MONTH, 15))
                favArr.put(oldData)
                newArr.put(oldData)
            }else{
                newArr.put(oldData)
            }
        }
        data.storePlaylistTracks(context,from, newArr.toString())
        Log.e("favarr", favArr.toString())

        //download file fun
        //call on data change to playlist fragment
        //val file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS.toString()).absolutePath
        //playlistFragment.update()

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
    fun removeToFavorites(context: Context, trackID: String, from: String){
        val oldArr = JSONArray(data.getPlaylistTracks(context,from))
        val newArr = JSONArray()
        val newfav = JSONArray()

        val favArr = JSONArray(data.getFavorites(context))
        Log.e("faveCountBefore", favArr.length().toString())
        for (i in 0 until favArr.length()){
            Log.e("TAG", i.toString())
            val faveData = favArr.getJSONObject(i)
            if (faveData.getString("id") != trackID){
                newfav.put(faveData)
                //Log.e("id", i.toString())
            }
        }
        Log.e("faveCountAfter", newfav.length().toString())
        Log.e("faveCountAfter", newfav.toString())

        for (i in 0 until oldArr.length()) {
            val oldData = oldArr.getJSONObject(i)
            if (oldData.getString("id").equals(trackID)){
                oldData.put("favorited","false")
                Log.e("sss", i.toString())
                newArr.put(oldData)

            }else{
                Log.e("sss", i.toString())
                newArr.put(oldData)
            }
        }


        data.storePlaylistTracks(context,from, newArr.toString())
        data.favorites(context,newfav.toString())
        val folder = File(context.filesDir, "elpaboritos")
        val documentFile = File("$folder/$trackID.mp3")
        documentFile.delete()
        //call on data change to playlist fragment
    }



}