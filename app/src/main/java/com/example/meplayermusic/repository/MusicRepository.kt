package com.example.meplayermusic.repository

import android.content.Context
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import com.example.meplayermusic.datasource.MusicDataSource
import com.example.meplayermusic.datasource.dao.MusicDao
import com.example.meplayermusic.model.Music
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.ConcatenatingMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSource

class MusicRepository(
    private val dao: MusicDao,
    private val dataSourceFactory: DefaultDataSource.Factory
) {

    companion object {
        private var favorites = mutableListOf<Music>()
        private var favoritesMetaData = mutableListOf<MediaMetadataCompat>()
        private var allMusics = mutableListOf<Music>()
        private fun updateAllMusics() {
            allMusics = MusicDataSource.musicList
            allMusics.forEach { music ->
                music.isFavorite = favorites.firstOrNull { it == music }?.let {
                    true
                } ?: false
            }
        }

        private fun favoritesToMetadata() {
            favoritesMetaData = favorites.map { music ->
                music.toMediaMetadata()
            }.toMutableList()
        }

    }

    fun updateFavorites(favoritesUpdated: List<Music>) {
        favorites = favoritesUpdated.toMutableList()
        updateAllMusics()
        favoritesToMetadata()
    }



    fun getAllMusics() = allMusics


    fun getFavoritesMetadata(): List<MediaMetadataCompat> {
        return favoritesMetaData.toList()
    }

    suspend fun addToFavorites(music: Music) = dao.save(music)

    suspend fun removeFromFavorites(music: Music) = dao.remove(music)

    fun getFavoritesLiveData() = dao.getAllLiveData()

    suspend fun getAllFavorites() = dao.getAll()

    fun getFavoritesMediaItems(): MutableList<MediaBrowserCompat.MediaItem> {
        return favorites.map { music ->
            music.toMediaItem()
        }.toMutableList()
    }

    fun getFavoritesMediaSource(): MediaSource {
        val concatenatingMediaSource = ConcatenatingMediaSource()
        favorites.forEach { music ->
            val mediaItemExoPlayer = MediaItem.fromUri(music.uri)
            val mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(mediaItemExoPlayer)
            concatenatingMediaSource.addMediaSource(mediaSource)
        }
        return concatenatingMediaSource
    }


    fun fetchData(context: Context) = MusicDataSource.fetchMediaData(context)

    fun getAllMusicMetaData() = MusicDataSource.musicMetadataList

    fun getAllMediaItems() = MusicDataSource.asMediaItems()

    fun getAllMediaSource() = MusicDataSource.asMediaSource(dataSourceFactory)

}