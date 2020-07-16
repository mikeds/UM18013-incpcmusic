package com.rworksph.incoriginalmedia

import android.content.Context
import org.json.JSONArray

class inits {
    var data = Data()

    fun initListData(context: Context, datalist:MutableList<Sets> = ArrayList()): MutableList<Sets> {
        val usersArr = JSONArray(data.getStoredJsonData(context))
        for (i in 0 until usersArr.length()) {
            val singleUser = usersArr.getJSONObject(i)

            datalist.add(Sets(singleUser.getString("title"), singleUser.getString("thumb"), singleUser.getString("track_count"), singleUser.getString("uri")))

        }
        return datalist
    }

    fun initHomeSongs(context: Context, songList : ArrayList<HashMap<String, String>>){
        val usersArr = JSONArray(data.getStoredJsonData(context))
        for (i in 0 until usersArr.length()) {
            val singleUser = usersArr.getJSONObject(i)

            val map = HashMap<String, String>()
            map["title"] = singleUser.getString("title")
            map["id"] = singleUser.getString("description")
            map["duration"] = "Tracks: "+singleUser.getString("track_count")
            map["image"] = singleUser.getString("thumb")
            map["streamUrl"] = singleUser.getString("uri")

            songList.add(map)

        }
    }
}