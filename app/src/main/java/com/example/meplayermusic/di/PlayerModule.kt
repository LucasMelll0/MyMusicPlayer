package com.example.meplayermusic.di

import com.example.meplayermusic.ui.player.PlayerViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val playerModule = module {
    viewModel {
        PlayerViewModel()
    }
}