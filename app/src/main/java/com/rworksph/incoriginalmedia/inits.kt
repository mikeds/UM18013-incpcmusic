package com.rworksph.incoriginalmedia

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_set_tracks.*
import org.json.JSONArray

class inits {
    var data = Data()

    fun initListData(context: Context, datalist:MutableList<Sets> = ArrayList()): MutableList<Sets> {
        val usersArr = JSONArray(data.getStoredSetData(context))
        for (i in 0 until usersArr.length()) {
            val singleUser = usersArr.getJSONObject(i)

            datalist.add(Sets(singleUser.getString("title"), singleUser.getString("thumb"), singleUser.getString("track_count"), singleUser.getString("uri")))

        }
        return datalist
    }

    fun initHomeSongs(context: Context, songList : ArrayList<HashMap<String, String>>){
        val usersArr = JSONArray(data.getTracksData(context))
        for (i in 0 until usersArr.length()) {
            val singleUser = usersArr.getJSONObject(i)

            val map = HashMap<String, String>()
            map["title"] = singleUser.getString("title")
            map["id"] = singleUser.getString("description")
            map["image"] = singleUser.getString("thumb")
            map["streamUrl"] = singleUser.getString("uri")

            songList.add(map)

        }
    }

    fun initSetTracks(context: Context, songList : ArrayList<HashMap<String, String>>){
        val usersArr = JSONArray(data.getTracksData(context))
        for (i in 0 until usersArr.length()) {
            val singleUser = usersArr.getJSONObject(i)

            val map = HashMap<String, String>()
            map["title"] = singleUser.getString("title")
            map["id"] = singleUser.getString("description")
            map["image"] = singleUser.getString("thumb")
            map["streamUrl"] = singleUser.getString("uri")

            songList.add(map)

        }
    }
}