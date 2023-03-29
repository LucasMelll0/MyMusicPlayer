package com.example.meplayermusic.ui.musiclist.all.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.meplayermusic.extensions.toMusic
import com.example.meplayermusic.model.Music
import com.example.meplayermusic.other.Resource
import com.example.meplayermusic.repository.MusicRepository

class MusicListViewModel(
    private val repository: MusicRepository
) : ViewModel() {

    private val _musics: MutableLiveData<Resource<List<Music>>?> = MutableLiveData()
    internal val musics: LiveData<Resource<List<Music>>?> = _musics

    internal val favorites = repository.getFavoritesLiveData()

    fun update(favorites: List<Music>) {
        repository.getAllMusicMetaData().mapNotNull { it.toMusic() }.also { musicList ->
            for (music in musicList) {
                if (favorites.firstOrNull { it.uri == music.uri } != null) {
                    music.isFavorite = true
                }
            }
            Log.d("Tests", "update: $favorites")
            Log.d("Tests", "update: ${musicList.filter { it.isFavorite }}")
            _musics.value = Resource.success(musicList)
        }
    }

    fun getAll() {
        _musics.postValue(Resource.success(null))
        repository.getAllMusicMetaData().mapNotNull { it.toMusic() }.also { musicList ->
            Log.d("Tests", "getAll: ${musicList.filter { it.isFavorite }}")
            _musics.postValue(Resource.success(musicList))
        }
    }
}
