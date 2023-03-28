package com.example.meplayermusic.di

import androidx.room.Room
import com.example.meplayermusic.constantes.DATABASE_NAME
import com.example.meplayermusic.datasource.AppDataBase
import com.example.meplayermusic.repository.MusicRepository
import com.example.meplayermusic.services.exoplayer.callbacks.MusicServiceConnection
import com.example.meplayermusic.ui.musiclist.recyclerview.MusicAdapter
import com.example.meplayermusic.ui.main.viewmodel.MainViewModel
import com.example.meplayermusic.ui.player.viewmodel.PlayerViewModel
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.upstream.DefaultDataSource
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val playerModule = module {
    viewModel {
        PlayerViewModel(get())
    }

    viewModel {
        MainViewModel(get())
    }

    single {
        MusicServiceConnection(androidContext())
    }

    single {
        DefaultDataSource.Factory(androidContext())
    }

    single {
        AudioAttributes.Builder()
            .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
            .setUsage(C.USAGE_MEDIA)
            .build()
    }


    single {
        ExoPlayer.Builder(androidContext()).build().apply {
            setHandleAudioBecomingNoisy(true)
            setAudioAttributes(get(), true)
        }
    }

    single {
        MusicAdapter()
    }
}

val roomModule = module {
    single {
        Room.databaseBuilder(
            androidContext(),
            AppDataBase::class.java,
            DATABASE_NAME
        ).build()
    }

    single {
        get<AppDataBase>().musicDao()
    }
}

val repositoryModule = module {
    single { MusicRepository(androidContext(), get(), get()) }
}