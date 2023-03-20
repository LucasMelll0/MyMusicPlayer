package com.example.meplayermusic.di

import com.example.meplayermusic.services.exoplayer.callbacks.MusicServiceConnection
import com.example.meplayermusic.ui.musiclist.recyclerview.MusicAdapter
import com.example.meplayermusic.ui.musiclist.viewmodel.MusicListViewModel
import com.example.meplayermusic.ui.player.PlayerViewModel
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.upstream.DefaultDataSource
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val playerModule = module {
    viewModel {
        PlayerViewModel()
    }

    viewModel {
        MusicListViewModel(get())
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