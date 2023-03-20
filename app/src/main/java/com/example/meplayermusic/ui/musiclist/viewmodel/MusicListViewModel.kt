package com.example.meplayermusic.ui.musiclist.viewmodel

import android.support.v4.media.MediaBrowserCompat
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
import com.example.meplayermusic.services.exoplayer.MEDIA_ROOT_ID
import com.example.meplayermusic.services.exoplayer.MediaPlayService
import com.example.meplayermusic.services.exoplayer.callbacks.MusicServiceConnection
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MusicListViewModel(
    private val musicServiceConnection: MusicServiceConnection
) : ViewModel() {

    private val _mediaItems = MutableLiveData<Resource<List<Music>>>()
    internal val mediaItems: LiveData<Resource<List<Music>>> = _mediaItems

    val currentPlayingMusic = musicServiceConnection.currentPlayingSong
    internal val playbackState = musicServiceConnection.playbackState

    private val _currentMusicPosition = MutableLiveData<Long?>()
    internal val currentMusicPosition: LiveData<Long?> = _currentMusicPosition

    private val _currentMusicDuration = MutableLiveData<Long?>()
    internal val currentMusicDuration: LiveData<Long?> = _currentMusicDuration

    init {
        _mediaItems.postValue(Resource.loading(null))
        musicServiceConnection.subscribe(
            MEDIA_ROOT_ID,
            object : MediaBrowserCompat.SubscriptionCallback() {
                override fun onChildrenLoaded(
                    parentId: String,
                    children: MutableList<MediaBrowserCompat.MediaItem>
                ) {
                    super.onChildrenLoaded(parentId, children)
                    val items = children.map {
                        Music(
                            id = it.mediaId!!,
                            image = it.description.iconBitmap,
                            title = it.description.title.toString(),
                            artist = it.description.subtitle.toString(),
                            uri = it.description.mediaUri.toString()
                        )
                    }
                    _mediaItems.postValue(Resource.success(items))
                }
            })
        updateCurrentlyMusicDuration()
    }

    private fun updateCurrentlyMusicDuration() {
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

    fun skipToNextMusic() {
        musicServiceConnection.transportControls.skipToNext()
    }

    fun playOrToggleMusic(music: Music, toggle: Boolean = false) {
        val isPrepared = playbackState.value?.isPrepared ?: false
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

    override fun onCleared() {
        super.onCleared()
        musicServiceConnection.unsubscribe(
            MEDIA_ROOT_ID,
            object : MediaBrowserCompat.SubscriptionCallback() {})
    }
}