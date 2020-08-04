package com.rworksph.incoriginalmedia

class Home_Playlists {
    var title: String? =null
    var thumb: String? =null
    var trackCount: String? =null
    var playlistUrl: String? =null
    var playlistID: String? =null

    constructor(title:String, thumb:String, trackCount:String, playlistUrl:String, playlistID:String){
        this.title =  title
        this.thumb =  thumb
        this.trackCount =  "($trackCount songs)"
        this.playlistUrl =  playlistUrl
        this.playlistID =  playlistID
    }
}