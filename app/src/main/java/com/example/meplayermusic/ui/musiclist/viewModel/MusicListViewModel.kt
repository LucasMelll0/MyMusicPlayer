package com.example.meplayermusic.ui.musiclist.viewModel

import com.example.meplayermusic.constantes.MEDIA_FAVORITES_ID
import com.example.meplayermusic.constantes.MEDIA_ROOT_ID
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.meplayermusic.extensions.toMusic
import com.example.meplayermusic.model.Music
import com.example.meplayermusic.other.Resource
import com.example.meplayermusic.repository.MusicRepository
import kotlinx.coroutines.launch

class MusicListViewModel(
    private val repository: MusicRepository
) : ViewModel() {

    private val _musics: MutableLiveData<Resource<List<Music>>?> = MutableLiveData()
    internal val musics: LiveData<Resource<List<Music>>?> = _musics

    internal val favorites = repository.getFavoritesLiveData()

    fun update(favorites: List<Music>) {
        repository.updateFavorites(favorites)
        repository.getAllMusicMetaData().mapNotNull { it.toMusic() }.also { musicList ->
            for (music in musicList) {
                if (favorites.find { it == music } != null) {
                    music.isFavorite = true
                }
            }
            _musics.value = Resource.success(musicList)
        }
    }

    fun searchByName(query: String, parentId: String): List<Music> = when(parentId) {
        MEDIA_ROOT_ID -> musics.value?.data?.filter { music ->
            music.title.contains(query, true)
        } ?: emptyList()
        MEDIA_FAVORITES_ID -> favorites.value?.filter { music ->
            music.title.contains(query, true)
        } ?: emptyList()
        else -> emptyList()
    }


    fun addToFavorites(music: Music) {
        viewModelScope.launch {
            favorites.value?.let { musicList ->
                var found = false
                for (item in musicList) {
                    if (item == music) {
                        found = true
                    }
                }
                if (!found) {
                    music.isFavorite = true
                    repository.addToFavorites(music)
                }
            }
        }
    }

    fun removeFromFavorites(music: Music) {
        viewModelScope.launch {
            favorites.value?.let { musicList ->
                var found = false
                for (item in musicList) {
                    if (item == music) {
                        found = true
                    }
                }
                if (found) {
                    repository.removeFromFavorites(music)
                }
            }
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


