package com.example.meplayermusic.datasource

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.MediaMetadataCompat.*
import androidx.core.net.toUri
import com.example.meplayermusic.model.Music
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.ConcatenatingMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSource

class MusicDataSource {

    companion object {

        private val musicList: MutableList<Music> = mutableListOf()
        var musicMetadataList = emptyList<MediaMetadataCompat>()

        fun fetchMediaData(context: Context) {
            getAllAudioFiles(context)
            musicMetadataList = musicList.map { music ->
                MediaMetadataCompat.Builder()
                    .putString(METADATA_KEY_ARTIST, music.artist)
                    .putString(METADATA_KEY_MEDIA_ID, music.id)
                    .putString(METADATA_KEY_TITLE, music.title)
                    .putString(METADATA_KEY_DISPLAY_SUBTITLE, music.artist)
                    .putString(METADATA_KEY_DISPLAY_TITLE, music.title)
                    .putString(METADATA_KEY_ALBUM_ART_URI, music.image.toString())
                    .putString(METADATA_KEY_MEDIA_URI, music.uri)
                    .putString(METADATA_KEY_DISPLAY_DESCRIPTION, music.artist)
                    .build()
            }
        }

        private fun getAllAudioFiles(context: Context): List<Music> {
            musicList.clear()
            val contextResolver = context.contentResolver
            val projection = arrayOf(
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.ALBUM_ID
            )

            val selection = "${MediaStore.Audio.Media.MIME_TYPE} = ?"
            val selectionArgs = arrayOf("audio/mpeg")

            val sortOrder = "${MediaStore.Audio.Media.TITLE} ASC"

            val cursor = contextResolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                sortOrder
            )

            cursor?.use {
                val fullPathColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
                val nameColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
                val artistColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
                val durationColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
                val albumIdColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)


                while (it.moveToNext()) {
                    val fullPath = it.getString(fullPathColumn)
                    val name = it.getString(nameColumn)
                    val artist = it.getString(artistColumn)
                    val duration = it.getInt(durationColumn)
                    val uri = Uri.parse(fullPath)
                    val albumId = it.getLong(albumIdColumn)
                    if (!name.startsWith("AUD")) {
                        val albumImage = getAlbumImage(albumId).toString()
                        val music = Music(
                            image = albumImage,
                            title = name,
                            artist = artist,
                            duration = duration,
                            uri = uri.toString(),
                        )
                        musicList.add(music)
                    }
                }
            }
            return musicList
        }

        private fun getAlbumImage(albumId: Long): Uri {
            val sImage = Uri.parse("content://media/external/audio/albumart")
            return ContentUris.withAppendedId(sImage, albumId)
        }

        fun asMediaItems() = musicMetadataList.map { music ->
            val desc = MediaDescriptionCompat.Builder()
                .setMediaUri(music.getString(METADATA_KEY_MEDIA_URI).toUri())
                .setTitle(music.description.title)
                .setSubtitle(music.description.subtitle)
                .setMediaId(music.description.mediaId)
                .setIconBitmap(music.description.iconBitmap)
                .build()
            MediaBrowserCompat.MediaItem(desc, MediaBrowserCompat.MediaItem.FLAG_PLAYABLE)
        }.toMutableList()

        fun asMediaSource(dataSourceFactory: DefaultDataSource.Factory): MediaSource {
            val concatenatingMediaSource = ConcatenatingMediaSource()
            musicList.forEach { music ->
                val mediaItemExoPlayer = MediaItem.fromUri(music.uri)
                val mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(mediaItemExoPlayer)
                concatenatingMediaSource.addMediaSource(mediaSource)
            }
            return concatenatingMediaSource
        }
    }
}