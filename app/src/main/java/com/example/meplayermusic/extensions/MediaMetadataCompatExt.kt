package com.example.meplayermusic.extensions

import android.support.v4.media.MediaMetadataCompat
import com.example.meplayermusic.model.Music

fun MediaMetadataCompat.toMusic(): Music? {
    return description?.let {
        Music(
            id = it.mediaId ?: "",
            image = it.iconUri,
            title = it.title.toString(),
            artist = it.subtitle.toString(),
            uri = it.mediaUri.toString()
        )
    }
}