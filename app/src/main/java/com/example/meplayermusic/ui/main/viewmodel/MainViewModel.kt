package com.example.meplayermusic.ui.main.viewmodel

import android.content.Context
import android.support.v4.media.MediaBrowserCompat
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.meplayermusic.extensions.currentPlaybackPosition
import com.example.meplayermusic.extensions.isPlayEnabled
import com.example.meplayermusic.extensions.isPlaying
import com.example.meplayermusic.extensions.isPrepared
import com.example.meplayermusic.model.Music
import com.example.meplayermusic.other.Resource
import com.example.meplayermusic.constantes.MEDIA_ROOT_ID
import com.example.meplayermusic.repository.MusicRepository
import com.example.meplayermusic.services.exoplayer.MediaPlayService
import com.example.meplayermusic.services.exoplayer.callbacks.MusicServiceConnection
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainViewModel(
    private val musicServiceConnection: MusicServiceConnection,
    private val repository: MusicRepository

) : ViewModel() {

    private val subscription = MutableLiveData<String?>(null)

    private val _mediaItems = MutableLiveData<Resource<List<Music>>>()
    internal val mediaItems: LiveData<Resource<List<Music>>> = _mediaItems

    val currentPlayingMusic = musicServiceConnection.currentPlayingSong
    internal val playbackState = musicServiceConnection.playbackState

    private val _currentMusicPosition = MutableLiveData<Long?>()
    internal val currentMusicPosition: LiveData<Long?> = _currentMusicPosition

    private val _currentMusicDuration = MutableLiveData<Long?>()
    internal val currentMusicDuration: LiveData<Long?> = _currentMusicDuration

    fun fetchData(context: Context) {
        repository.fetchData(context)
    }
    private fun updateCurrentlyMusicPosition() {
        viewModelScope.launch {
            while (true) {
                val currentlyPosition = playbackState.value?.currentPlaybackPosition
                if (currentlyPosition != currentMusicPosition.value) {
                    _currentMusicPosition.postValue(currentlyPosition)
                    _currentMusicDuration.postValue(MediaPlayService.currentMusicDuration)
                }
                delay(200)
            }
        }
    }

    fun addToFavorites(music: Music) {
        viewModelScope.launch {
            repository.addToFavorites(music)
        }
    }

    fun removeFromFavorites(music: Music) {
        viewModelScope.launch {
            repository.removeFromFavorites(music)
        }
    }
    fun getCurrentlySubscription(): String? = subscription.value

    fun skipToNextMusic() {
        musicServiceConnection.transportControls.skipToNext()
    }

    fun playOrToggleMusic(music: Music, toggle: Boolean = false, parentId: String = MEDIA_ROOT_ID) {
        val isPrepared = playbackState.value?.isPrepared ?: false
        checkSubscription(parentId) {
            Log.d("Tests", "playOrToggleMusic: ${music.id}")
            if (music.id.trim().isEmpty()) {
                mediaItems.value?.data?.get(0)?.id?.let {
                    musicServiceConnection.transportControls.playFromMediaId(it, null)
                    return@checkSubscription
                }
            }
            if (isPrepared && music.id == currentPlayingMusic.value?.description?.mediaId) {
                playbackState.value?.let { state ->
                    when {
                        state.isPlaying -> if (toggle) musicServiceConnection.transportControls.pause()
                        state.isPlayEnabled -> musicServiceConnection.transportControls.play()
                        else -> Unit
                    }
                }
            } else {
                musicServiceConnection.transportControls.playFromMediaId(music.id, null)
            }
        }
    }

    private fun checkSubscription(parentId: String, onSubscribed: () -> Unit) {
        subscription.value?.let {
            if (it == parentId) {
                onSubscribed()
            } else {
                subscribe(parentId) {
                    subscription.postValue(parentId)
                    onSubscribed()
                }
            }
        } ?: run {
            subscribe(parentId) {
                subscription.postValue(parentId)
                onSubscribed()
            }
        }
    }

    private fun subscribe(parentId: String, onSubscribed: () -> Unit) {
        _mediaItems.postValue(Resource.loading(null))
        musicServiceConnection.subscribe(
            parentId,
            object : MediaBrowserCompat.SubscriptionCallback() {
                override fun onChildrenLoaded(
                    parentId: String,
                    children: MutableList<MediaBrowserCompat.MediaItem>
                ) {
                    super.onChildrenLoaded(parentId, children)
                    val items = children.map {
                        Music(
                            id = it.mediaId!!,
                            image = it.description.iconUri.toString(),
                            title = it.description.title.toString(),
                            artist = it.description.subtitle.toString(),
                            uri = it.description.mediaUri.toString()
                        )
                    }
                    _mediaItems.postValue(Resource.success(items))
                }
            })
        updateCurrentlyMusicPosition()
        onSubscribed()
    }

    fun searchByName(query: String): List<Music> =
        mediaItems.value?.data?.filter { music ->
            music.title.contains(query, true)
        } ?: emptyList()


    override fun onCleared() {
        super.onCleared()
        musicServiceConnection.unsubscribe(
            MEDIA_ROOT_ID,
            object : MediaBrowserCompat.SubscriptionCallback() {})
    }
}