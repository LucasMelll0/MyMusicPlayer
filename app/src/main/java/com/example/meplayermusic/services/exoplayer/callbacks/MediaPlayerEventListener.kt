package com.example.meplayermusic.services.exoplayer.callbacks

import android.widget.Toast
import com.example.meplayermusic.R
import com.example.meplayermusic.services.exoplayer.MediaPlayService
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player

class MediaPlayerEventListener(
    private val mediaPlayService: MediaPlayService
) : Player.Listener {

    override fun onPlayerError(error: PlaybackException) {
        super.onPlayerError(error)
        Toast.makeText(
            mediaPlayService,
            mediaPlayService.getString(R.string.on_player_error_message),
            Toast.LENGTH_SHORT
        ).show()
    }
}