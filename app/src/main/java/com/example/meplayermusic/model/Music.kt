package com.example.meplayermusic.model

import android.net.Uri
import android.support.v4.media.MediaBrowserCompat.MediaItem
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import androidx.core.net.toUri
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
    val uri: String = "",
    var isFavorite: Boolean = false
) {

    override fun equals(other: Any?): Boolean {
        return this.uri == (other as Music).uri && this.title.equals(
            other.title,
            true
        )
    }

    override fun toString(): String {
        return "id: $id \n image: $image \n title: $title " +
                "\n author: $artist \n duration: $duration \n $uri"
    }

    fun toMediaMetadata(): MediaMetadataCompat {
        return MediaMetadataCompat.Builder()
            .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, this.artist)
            .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, this.id)
            .putString(MediaMetadataCompat.METADATA_KEY_TITLE, this.title)
            .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE, this.artist)
            .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE, this.title)
            .putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI, this.image.toString())
            .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI, this.uri)
            .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_DESCRIPTION, this.artist)
            .build()
    }

    fun toMediaItem(): MediaItem {
        val desc = MediaDescriptionCompat.Builder()
            .setMediaUri(
                toMediaMetadata().getString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI).toUri()
            )
            .setTitle(toMediaMetadata().description.title)
            .setSubtitle(toMediaMetadata().description.subtitle)
            .setMediaId(toMediaMetadata().description.mediaId)
            .setIconBitmap(toMediaMetadata().description.iconBitmap)
            .build()
        return MediaItem(desc, MediaItem.FLAG_PLAYABLE)
    }

    override fun hashCode(): Int {
        var result = image?.hashCode() ?: 0
        result = 31 * result + title.hashCode()
        result = 31 * result + artist.hashCode()
        result = 31 * result + duration
        result = 31 * result + uri.hashCode()
        return result
    }
}