package com.example.meplayermusic.ui.player

import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.meplayermusic.model.Music

class PlayerViewModel : ViewModel() {

    private val _musicMetaData: MutableLiveData<Music?> = MutableLiveData(null)
    internal val musicMetaData: LiveData<Music?> get() = _musicMetaData

    private val _playerServiceIntent: MutableLiveData<Intent?> = MutableLiveData(null)
    internal val playerServiceIntent: LiveData<Intent?> get() = _playerServiceIntent


    fun setMusicMetaData(music: Music) {
        _musicMetaData.postValue(music)
    }

    fun getMusicMetaData(): Music? {
        return musicMetaData.value
    }

    fun savePlayerServiceIntent(intent: Intent) {
        _playerServiceIntent.postValue(intent)
    }

    fun getPlayerServiceIntent(): Intent? {
        return playerServiceIntent.value
    }

}