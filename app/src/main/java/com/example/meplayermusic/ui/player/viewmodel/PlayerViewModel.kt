package com.example.meplayermusic.ui.player.viewmodel

import android.support.v4.media.session.PlaybackStateCompat.*
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.meplayermusic.extensions.isPlayEnabled
import com.example.meplayermusic.extensions.isPlaying
import com.example.meplayermusic.extensions.isPrepared
import com.example.meplayermusic.services.exoplayer.callbacks.MusicServiceConnection

class PlayerViewModel(
    private val musicServiceConnection: MusicServiceConnection
) : ViewModel() {

    private val currentPlayingMusic = musicServiceConnection.currentPlayingSong
    internal val playbackState = musicServiceConnection.playbackState

    companion object {
        private val shuffleModePrivate = MutableLiveData(false)
        internal val shuffleMode: LiveData<Boolean> = shuffleModePrivate

        private val repeatModePrivate = MutableLiveData(false)
        internal val repeatMode: LiveData<Boolean> = repeatModePrivate
    }

    fun play() {
        val isPrepared = playbackState.value?.isPrepared ?: false
        if (isPrepared && currentPlayingMusic.value != null) {
            playbackState.value?.let { state ->
                when {
                    state.isPlaying -> musicServiceConnection.transportControls.pause()
                    state.isPlayEnabled -> musicServiceConnection.transportControls.play()
                    else -> Unit
                }
            }
        } else {
            currentPlayingMusic.value?.let {
                musicServiceConnection.transportControls.playFromMediaId(
                    it.description.mediaId,
                    null
                )
            }
        }
    }

    fun skipToNextMusic() = musicServiceConnection.transportControls.skipToNext()


    fun backToPreviousMusic() = musicServiceConnection.transportControls.skipToPrevious()

    fun seekTo(position: Long) = musicServiceConnection.transportControls.seekTo(position)

    fun shuffle() {
        shuffleMode.value?.let {
            if (it) {
                musicServiceConnection.transportControls.setShuffleMode(SHUFFLE_MODE_NONE)
                shuffleModePrivate.postValue(false)
            } else {
                musicServiceConnection.transportControls.setShuffleMode(SHUFFLE_MODE_ALL)
                shuffleModePrivate.postValue(true)
            }
        }
    }

    fun repeat() {
        repeatMode.value?.let {
            if (it) {
                musicServiceConnection.transportControls.setRepeatMode(REPEAT_MODE_NONE)
                repeatModePrivate.postValue(false)
            } else {
                musicServiceConnection.transportControls.setRepeatMode(REPEAT_MODE_ALL)
                repeatModePrivate.postValue(true)
            }
        }
    }
}