package com.example.meplayermusic.ui.musiclist.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.meplayermusic.constantes.MEDIA_FAVORITES_ID
import com.example.meplayermusic.constantes.MEDIA_ROOT_ID
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
        getAll()
    }

    suspend fun getAllFavorites() {
        repository.getAllFavorites().also { update(it) }
    }

    fun searchByName(query: String, parentId: String): List<Music> = when (parentId) {
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
                musicList.firstOrNull { it == music } ?: run {
                    music.isFavorite = true
                    repository.addToFavorites(music)
                }
            }
        }
    }

    fun removeFromFavorites(music: Music) {
        viewModelScope.launch {
            favorites.value?.let { musicList ->
                musicList.firstOrNull { it == music }?.let {
                    repository.removeFromFavorites(it)
                    getAllFavorites()
                }
            }

        }
    }

    fun getAll() {
        _musics.postValue(Resource.loading(null))
        repository.getAllMusics().also {
            _musics.postValue(Resource.success(it))
        }
    }
}


