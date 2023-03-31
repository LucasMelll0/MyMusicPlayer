package com.example.meplayermusic.services.exoplayer.callbacks

import android.net.Uri
import android.os.Bundle
import android.os.ResultReceiver
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import com.example.meplayermusic.constantes.MEDIA_FAVORITES_ID
import com.example.meplayermusic.constantes.MEDIA_ROOT_ID
import com.example.meplayermusic.repository.MusicRepository
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector

class MediaPlayerPlaybackPreparer(
    private val parentId: String = MEDIA_ROOT_ID,
    private val repository: MusicRepository,
    private val playerPrepared: (MediaMetadataCompat?) -> Unit
) : MediaSessionConnector.PlaybackPreparer {
    override fun onCommand(
        player: Player,
        command: String,
        extras: Bundle?,
        cb: ResultReceiver?
    ): Boolean = false

    override fun getSupportedPrepareActions(): Long {
        return PlaybackStateCompat.ACTION_PREPARE_FROM_MEDIA_ID or
                PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID
    }

    override fun onPrepare(playWhenReady: Boolean) = Unit

    override fun onPrepareFromMediaId(mediaId: String, playWhenReady: Boolean, extras: Bundle?) {
        val itemToPlay = when (parentId) {
            MEDIA_ROOT_ID -> {
                repository.getAllMusicMetaData().find { mediaId == it.description.mediaId }
            }
            MEDIA_FAVORITES_ID -> {
                repository.getFavoritesMetadata().find { mediaId == it.description.mediaId }
            }
            else -> repository.getAllMusicMetaData().find { mediaId == it.description.mediaId }
        }
        Log.d("Tests", "onPrepareFromMediaId: ${itemToPlay?.description?.mediaId}")
        playerPrepared(itemToPlay)
    }

    override fun onPrepareFromSearch(query: String, playWhenReady: Boolean, extras: Bundle?) = Unit

    override fun onPrepareFromUri(uri: Uri, playWhenReady: Boolean, extras: Bundle?) = Unit
}