package com.example.original_music.model

class Track {
    var title: String = ""
    var image: String = ""
    var waveform: String = ""
    var audioStream: String = ""
    var id: String = ""

    override fun toString(): String {
        return """
            title: $title
        """.trimIndent()
    }
}