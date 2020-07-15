package com.rworksph.incoriginalmedia

import android.os.AsyncTask
import android.util.Log
import org.json.JSONArray
import java.net.URL

class FetchData() : AsyncTask<String, Void, String>(){

    var dataList = ArrayList<HashMap<String, String>>()
    override fun onPreExecute() {
        super.onPreExecute()
        //findViewById<ProgressBar>(R.id.loader).visibility = View.VISIBLE
    }

    override fun doInBackground(vararg params: String?): String? {
        return URL("https://api-v2.hearthis.at/mikeds/?type=playlists").readText(
            Charsets.UTF_8
        )
    }

    override fun onPostExecute(result: String?) {
        super.onPostExecute(result)

        /*val usersArr = JSONArray(result)
        for (i in 0 until usersArr.length()) {
            val singleUser = usersArr.getJSONObject(i)

            val map = HashMap<String, String>()
            map["title"] = singleUser.getString("title")
            map["id"] = singleUser.getString("description")
            map["duration"] = "Tracks: " + singleUser.getString("track_count")
            map["image"] = singleUser.getString("thumb")
            map["streamUrl"] = singleUser.getString("uri")

            dataList.add(map)
        }

        Log.e("data", dataList.toString())*/
        return

    }


}