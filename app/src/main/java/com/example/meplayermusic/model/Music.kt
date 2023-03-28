package com.example.meplayermusic.model

import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*
@Entity
data class Music(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    var image: String? = Uri.EMPTY.toString(),
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