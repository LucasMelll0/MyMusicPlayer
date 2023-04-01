package com.example.meplayermusic.services.exoplayer

import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.content.Intent
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.util.Log
import androidx.media.MediaBrowserServiceCompat
import com.example.meplayermusic.constantes.MEDIA_FAVORITES_ID
import com.example.meplayermusic.constantes.MEDIA_ROOT_ID
import com.example.meplayermusic.repository.MusicRepository
import com.example.meplayermusic.services.exoplayer.callbacks.MediaPlayerEventListener
import com.example.meplayermusic.services.exoplayer.callbacks.MediaPlayerNotificationListener
import com.example.meplayermusic.services.exoplayer.callbacks.MediaPlayerPlaybackPreparer
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import com.google.android.exoplayer2.ext.mediasession.TimelineQueueNavigator
import com.google.android.exoplayer2.source.MediaSource
import org.koin.android.ext.android.inject

const val LOG_TAG = "Media Play Service"

class MediaPlayService : MediaBrowserServiceCompat() {

    private val exoPlayer: ExoPlayer by inject()
    var isForegroundService = false
    private val repository: MusicRepository by inject()
    private lateinit var mediaSession: MediaSessionCompat
    private var mediaSessionConnector: MediaSessionConnector? = null
    private var isPlayerInitialized = false
    private var currentPlayingMusic: MediaMetadataCompat? = null
    private lateinit var musicNotificationManager: MediaPlayerNotificationManager
    private lateinit var mediaPlayerEventListener: MediaPlayerEventListener


    companion object {
        var currentMusicDuration = 0L
            private set
        var playListId: String? = null
            private set
    }

    override fun onCreate() {
        super.onCreate()
        val activityIntent = packageManager?.getLaunchIntentForPackage(packageName)?.let {
            PendingIntent.getActivity(this, 0, it, FLAG_IMMUTABLE)
        }
        mediaSession = MediaSessionCompat(baseContext, LOG_TAG).apply {
            setSessionActivity(activityIntent)
            isActive = true
        }

        sessionToken = mediaSession.sessionToken

        musicNotificationManager = MediaPlayerNotificationManager(
            this@MediaPlayService,
            mediaSession.sessionToken,
            MediaPlayerNotificationListener(this@MediaPlayService)
        ) {
            currentMusicDuration = exoPlayer.duration
        }

        val mediaPlaybackPreparer = MediaPlayerPlaybackPreparer(repository = repository) {
            currentPlayingMusic = it
            preparePlayer(
                repository.getAllMusicMetaData(), it, repository.getAllMediaSource(), true
            )
        }

        mediaSessionConnector = MediaSessionConnector(mediaSession)
        mediaSessionConnector!!.setPlaybackPreparer(mediaPlaybackPreparer)
        mediaSessionConnector!!.setQueueNavigator(MediaQueueNavigator())
        mediaSessionConnector!!.setPlayer(exoPlayer)
        mediaPlayerEventListener = MediaPlayerEventListener(this)
        exoPlayer.addListener(mediaPlayerEventListener)
        musicNotificationManager.showNotification(exoPlayer)
    }

    override fun onGetRoot(
        clientPackageName: String, clientUid: Int, rootHints: Bundle?
    ): BrowserRoot {
        return BrowserRoot(MEDIA_ROOT_ID, null)
    }

    override fun onLoadChildren(
        parentId: String, result: Result<MutableList<MediaBrowserCompat.MediaItem>>
    ) {
        Log.d("Tests", "onLoadChildren: $parentId")
        when (parentId) {
            MEDIA_ROOT_ID -> {
                if (playListId != MEDIA_ROOT_ID) {
                    setPlayBackPreparer(MEDIA_ROOT_ID)
                }
                val mediaItems = repository.getAllMediaItems()
                result.sendResult(mediaItems)
                if (!isPlayerInitialized && mediaItems.isNotEmpty()) {
                    preparePlayer(
                        repository.getAllMusicMetaData(),
                        repository.getAllMusicMetaData()[0],
                        repository.getAllMediaSource(),
                        false
                    )
                    isPlayerInitialized = true
                }
            }
            MEDIA_FAVORITES_ID -> {
                if (playListId != MEDIA_FAVORITES_ID) {
                    setPlayBackPreparer(MEDIA_FAVORITES_ID)
                }
                val mediaItems = repository.getFavoritesMediaItems()
                result.sendResult(mediaItems)
                if (!isPlayerInitialized && mediaItems.isNotEmpty()) {
                    preparePlayer(
                        repository.getFavoritesMetadata(),
                        repository.getFavoritesMetadata()[0],
                        repository.getFavoritesMediaSource(),
                        false
                    )
                    isPlayerInitialized = true
                }
            }
        }
    }

    private fun setPlayBackPreparer(parentId: String) {
        Log.d("Tests", "setPlayBackPreparer: $parentId")
        val mediaPlaybackPreparer = MediaPlayerPlaybackPreparer(parentId, repository) {
            currentPlayingMusic = it
            preparePlayer(
                if (parentId == MEDIA_FAVORITES_ID) repository.getFavoritesMetadata() else repository.getAllMusicMetaData(),
                it,
                if (parentId == MEDIA_FAVORITES_ID) repository.getFavoritesMediaSource() else repository.getAllMediaSource(),
                true
            )
        }
        mediaSessionConnector!!.setPlaybackPreparer(mediaPlaybackPreparer)
        playListId = parentId
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        exoPlayer.stop()
    }

    private fun preparePlayer(
        musicList: List<MediaMetadataCompat>,
        musicToPlay: MediaMetadataCompat?,
        mediaSource: MediaSource,
        play: Boolean
    ) {
        val currentMusicIndex = currentPlayingMusic?.let { musicList.indexOf(musicToPlay) } ?: 0
        /*Log.d("Tests", "musicList: $musicList")
        Log.d("Tests", "firstItem: ${musicList[0]}")
        Log.d("Tests", "musicToPlay: $musicToPlay")
        Log.d("Tests", "indexToPlay: $currentMusicIndex")*/
        exoPlayer.setMediaSource(mediaSource)
        exoPlayer.prepare()
        exoPlayer.seekTo(currentMusicIndex, 0L)
        exoPlayer.playWhenReady = play
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaSession.release()
        exoPlayer.removeListener(mediaPlayerEventListener)
        exoPlayer.release()
    }

    private inner class MediaQueueNavigator : TimelineQueueNavigator(mediaSession) {
        override fun getMediaDescription(player: Player, windowIndex: Int): MediaDescriptionCompat {
            return when(playListId) {
                MEDIA_FAVORITES_ID -> repository.getFavoritesMetadata()[windowIndex].description
                else -> repository.getAllMusicMetaData()[windowIndex].description
            }

        }
    }

}