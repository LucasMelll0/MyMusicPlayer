package com.example.meplayermusic.datasource

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.meplayermusic.datasource.dao.MusicDao
import com.example.meplayermusic.model.Music

@Database(
    version = 1,
    entities = [Music::class],
    exportSchema = true
)
abstract class AppDataBase : RoomDatabase() {

    abstract fun musicDao(): MusicDao
}