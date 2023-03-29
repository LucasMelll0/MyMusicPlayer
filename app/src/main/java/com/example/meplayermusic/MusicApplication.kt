package com.example.meplayermusic

import android.app.Application
import com.example.meplayermusic.di.*
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class MusicApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@MusicApplication)
            modules(
                playerModule,
                roomModule,
                repositoryModule,
                allMusicsModule,
                favoritesModule
            )
        }
    }
}