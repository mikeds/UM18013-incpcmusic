package com.rworksph.incoriginalmedia

import android.content.Context

class Data() {

    val songList = ArrayList<HashMap<String, String>>()


    fun storePlaylists(context:Context,data: String) {
        val sharedPreference =  context.getSharedPreferences("Data",Context.MODE_PRIVATE)
        var editor = sharedPreference.edit()
        editor.putString("PlaylistData",data)
        editor.commit()

    }

    fun getPlaylists(context:Context): String? {
        val sharedPreference =  context.getSharedPreferences("Data",Context.MODE_PRIVATE)
        val data = sharedPreference.getString("PlaylistData","")
        return data
    }

    fun storeAllSongs(context:Context,data: String) {
        val sharedPreference =  context.getSharedPreferences("Data",Context.MODE_PRIVATE)
        var editor = sharedPreference.edit()
        editor.putString("AllTracks",data)
        editor.commit()
    }

    fun getAllSongs(context:Context): String? {
        val sharedPreference =  context.getSharedPreferences("Data",Context.MODE_PRIVATE)
        val data = sharedPreference.getString("AllTracks","")
        return data
    }

    fun storePlaylistTracks(context:Context,id: String,data: String) {
        val sharedPreference =  context.getSharedPreferences("Data",Context.MODE_PRIVATE)
        var editor = sharedPreference.edit()
        editor.putString(id,data)
        editor.commit()
    }

    fun getPlaylistTracks(context:Context,id: String): String? {
        val sharedPreference =  context.getSharedPreferences("Data",Context.MODE_PRIVATE)
        return sharedPreference.getString(id,"")
    }

    fun nowPlaying(context:Context,data: String){
        val sharedPreference =  context.getSharedPreferences("Data",Context.MODE_PRIVATE)
        var editor = sharedPreference.edit()
        editor.putString("nowPlaying",data)
        editor.commit()
    }

    fun getNowPlaying(context:Context): String? {
        val sharedPreference =  context.getSharedPreferences("Data",Context.MODE_PRIVATE)
        return sharedPreference.getString("nowPlaying","")
    }

    fun favorites(context:Context,data: String){
        val sharedPreference =  context.getSharedPreferences("Data",Context.MODE_PRIVATE)
        var editor = sharedPreference.edit()
        editor.putString("favorites",data)
        editor.commit()
    }

    fun getFavorites(context:Context): String? {
        val sharedPreference =  context.getSharedPreferences("Data",Context.MODE_PRIVATE)
        return sharedPreference.getString("favorites","")
    }

    fun setTheme(context: Context, data: String){
        val sharedPreference =  context.getSharedPreferences("Data",Context.MODE_PRIVATE)
        var editor = sharedPreference.edit()
        editor.putString("Theme",data)
        editor.commit()
    }

    fun getTheme(context: Context):String?{
        val sharedPreference =  context.getSharedPreferences("Data",Context.MODE_PRIVATE)
        return sharedPreference.getString("Theme","")
    }

    fun connectivity(context: Context, isConnected:Boolean){
        val sharedPreference =  context.getSharedPreferences("Data",Context.MODE_PRIVATE)
        var editor = sharedPreference.edit()
        editor.putBoolean("connected",isConnected)
        editor.commit()
    }

    fun isConnected(context: Context):Boolean?{
        val sharedPreference =  context.getSharedPreferences("Data",Context.MODE_PRIVATE)
        return sharedPreference.getBoolean("connected", false)
    }

    fun firstUser(context: Context, unangbeses:Boolean){
        val sharedPreference =  context.getSharedPreferences("Data",Context.MODE_PRIVATE)
        val editor = sharedPreference.edit()
        editor.putBoolean("firstTimer",unangbeses)
        editor.commit()
    }

    fun isFirstTimeUser(context: Context):Boolean?{
        val sharedPreference =  context.getSharedPreferences("Data",Context.MODE_PRIVATE)
        return sharedPreference.getBoolean("firstTimer", true)
    }




}