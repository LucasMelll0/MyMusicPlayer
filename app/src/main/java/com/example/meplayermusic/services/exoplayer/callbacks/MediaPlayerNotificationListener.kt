package com.example.meplayermusic.services.exoplayer.callbacks

import android.app.Notification
import android.app.Service.STOP_FOREGROUND_REMOVE
import android.content.Intent
import androidx.core.content.ContextCompat
import com.example.meplayermusic.services.exoplayer.MediaPlayService
import com.example.meplayermusic.services.exoplayer.NOTIFICATION_ID
import com.google.android.exoplayer2.ui.PlayerNotificationManager

class MediaPlayerNotificationListener(
    private val mediaPlayService: MediaPlayService
) : PlayerNotificationManager.NotificationListener {

    override fun onNotificationCancelled(notificationId: Int, dismissedByUser: Boolean) {
        super.onNotificationCancelled(notificationId, dismissedByUser)
        mediaPlayService.apply {
            stopForeground(STOP_FOREGROUND_REMOVE)
            isForegroundService = false
            stopSelf()
        }
    }

    override fun onNotificationPosted(
        notificationId: Int,
        notification: Notification,
        ongoing: Boolean
    ) {
        super.onNotificationPosted(notificationId, notification, ongoing)
        mediaPlayService.apply {
            if (ongoing && !isForegroundService) {
                ContextCompat.startForegroundService(
                    this,
                    Intent(applicationContext, this::class.java)
                )
                startForeground(NOTIFICATION_ID, notification)
                isForegroundService = true
            }
        }
    }
}