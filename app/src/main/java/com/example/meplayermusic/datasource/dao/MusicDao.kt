package com.example.meplayermusic.datasource.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.meplayermusic.model.Music

@Dao
interface MusicDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(music: Music)

    @Delete
    fun remove(music: Music)

    @Query("SELECT * FROM Music")
    fun getAll(): LiveData<List<Music>>
}