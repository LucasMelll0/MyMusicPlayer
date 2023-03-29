package com.example.meplayermusic.repository

import android.content.Context
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import com.example.meplayermusic.datasource.MusicDataSource
import com.example.meplayermusic.datasource.dao.MusicDao
import com.example.meplayermusic.model.Music
import com.google.android.exoplayer2.upstream.DefaultDataSource

class MusicRepository(
    private val dao: MusicDao,
    private val dataSourceFactory: DefaultDataSource.Factory
) {

    suspend fun addToFavorites(music: Music) = dao.save(music)

    suspend fun removeFromFavorites(music: Music) = dao.remove(music)

    fun getFavoritesLiveData() = dao.getAllLiveData()

    fun getFavoritesMediaItems(): MutableList<MediaBrowserCompat.MediaItem> {
        return getFavoritesLiveData().value?.map { music ->
            music.toMediaItem()
        }?.toMutableList() ?: mutableListOf()
    }

    fun getFavoritesMetaData(): MutableList<MediaMetadataCompat> {
        return getFavoritesLiveData().value?.map { music ->
            music.toMediaMetadata()
        }?.toMutableList() ?: mutableListOf()
    }
    fun fetchData(context: Context) = MusicDataSource.fetchMediaData(context)

    fun getAllMusicMetaData() = MusicDataSource.musicMetadataList

    fun getAllMediaItems() = MusicDataSource.asMediaItems()

    fun getAllMediaSource() = MusicDataSource.asMediaSource(dataSourceFactory)

    fun getAllMusics() = MusicDataSource.musicList
}