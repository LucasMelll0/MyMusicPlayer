package com.example.meplayermusic.services

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Binder
import android.os.IBinder
import android.util.Log
import com.example.meplayermusic.MUSIC_URI_EXTRA

class MediaPlayerService : Service() {

    private lateinit var _player: MediaPlayer
    val player: MediaPlayer get() = _player
    private val binder = MediaPlayerBinder()

    override fun onBind(intent: Intent?): IBinder? {
        return binder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            intent.getStringExtra(MUSIC_URI_EXTRA)?.let {
                Log.i("Tests", "onStartCommand: $it")
                val musicUri = Uri.parse(it)
                _player = MediaPlayer.create(this, musicUri)
                _player.isLooping = true
                _player.start()
            }
        }
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        _player.release()
    }

    inner class MediaPlayerBinder() : Binder() {
        fun getService(): MediaPlayerService = this@MediaPlayerService
    }
}