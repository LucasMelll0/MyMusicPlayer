package com.example.meplayermusic.repository

import android.content.Context
import com.example.meplayermusic.datasource.MusicDataSource
import com.example.meplayermusic.datasource.dao.MusicDao
import com.example.meplayermusic.model.Music
import com.google.android.exoplayer2.upstream.DefaultDataSource

class MusicRepository(
    private val context: Context,
    private val dao: MusicDao,
    private val dataSourceFactory: DefaultDataSource.Factory
) {

    fun saveFavorite(music: Music) = dao.save(music)

    fun removeFavorite(music: Music) = dao.remove(music)

    fun getAllFavorite() = dao.getAll()

    fun fetchData() = MusicDataSource.fetchMediaData(context)

    fun getAllMusicMetaData() = MusicDataSource.musicMetadataList

    fun getAllMediaItems() = MusicDataSource.asMediaItems()

    fun getAllMediaSource() = MusicDataSource.asMediaSource(dataSourceFactory)
}