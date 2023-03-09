package com.example.meplayermusic.model

class Music(
    val image: String?,
    val title: String?,
    val author: String?,
    val duration: String?
) {
    override fun toString(): String {
        return "image: $image \n title: $title \n author: $author \n duration: $duration"
    }
}