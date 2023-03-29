package com.example.meplayermusic.datasource.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.meplayermusic.model.Music

@Dao
interface MusicDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(music: Music)

    @Delete
    suspend fun remove(music: Music)

    @Query("SELECT * FROM Music")
    fun getAllLiveData(): LiveData<List<Music>>

    @Query("SELECT * FROM Music")
    suspend fun getAll(): List<Music>
}