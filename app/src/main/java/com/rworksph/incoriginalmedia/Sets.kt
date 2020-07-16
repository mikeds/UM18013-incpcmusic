package com.rworksph.incoriginalmedia

class Sets {
    var songSetTitle: String? =null
    var songSetImage: String? =null
    var songSetSongCount: String? =null
    var songSetUrl: String? =null

    constructor(songSetTitle:String, songSetImage:String, songSetSongCount:String, songSetUrl:String){
        this.songSetTitle =  songSetTitle
        this.songSetImage =  songSetImage
        this.songSetSongCount =  "($songSetSongCount songs)"
        this.songSetUrl =  songSetUrl
    }
}