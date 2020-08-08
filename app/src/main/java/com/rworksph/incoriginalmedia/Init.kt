package com.rworksph.incoriginalmedia

import android.content.Context
import android.util.Log
import org.json.JSONArray

class Init {
    var data = Data()


    fun initPlaylists(context: Context, datalist:MutableList<Home_Playlists> = ArrayList()): MutableList<Home_Playlists> {
        val usersArr = JSONArray(data.getPlaylists(context))
        for (i in 0 until usersArr.length()) {
            val singleUser = usersArr.getJSONObject(i)
            datalist.add(
                Home_Playlists(
                    singleUser.getString("title"),
                    singleUser.getString("thumb"),
                    singleUser.getString("track_count"),
                    singleUser.getString("uri"),
                    singleUser.getString("id")
                )
            )


        }
        return datalist
    }

    fun initTracks(songList : ArrayList<HashMap<String, String>>, data: String){
        Log.e("data", "Dumadaan ba ko dito?")
        val usersArr = JSONArray(data)
        for (i in 0 until usersArr.length()) {
            val singleUser = usersArr.getJSONObject(i)

            val map = HashMap<String, String>()
            map["id"] = i.toString()
            map["trackID"] = singleUser.getString("id")
            map["title"] = singleUser.getString("title")
            map["image"] = singleUser.getString("thumb")
            map["streamUrl"] = singleUser.getString("stream_url")
            map["duration"] = singleUser.getString("duration")
            map["favorited"] = singleUser.getString("favorited")


            songList.add(map)

        }
    }
}