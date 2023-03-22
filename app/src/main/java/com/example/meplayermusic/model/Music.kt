package com.example.meplayermusic.model

import android.net.Uri
import java.util.*

data class Music(
    val id: String = UUID.randomUUID().toString(),
    var image: Uri? = null,
    val title: String = "",
    val artist: String = "",
    val duration: Int = 0,
    val uri: String = ""
) {
    override fun toString(): String {
        return "id: $id \n image: $image \n title: $title " +
                "\n author: $artist \n duration: $duration \n $uri"
    }
}