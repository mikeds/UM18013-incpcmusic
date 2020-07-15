package com.rworksph.incoriginalmedia

import android.os.AsyncTask
import java.net.URL

class FetchData(url0: String) : AsyncTask<String, Void, String>(){

    val url1 = url0
    var dataList = ArrayList<HashMap<String, String>>()
    override fun onPreExecute() {
        super.onPreExecute()
        //findViewById<ProgressBar>(R.id.loader).visibility = View.VISIBLE
    }

    override fun doInBackground(vararg params: String?): String? {
        return URL(url1).readText(
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