package com.example.meplayermusic.extensions

import java.text.SimpleDateFormat
import java.util.*

fun Long.toMinutesAndSeconds(): String {
    val dateFormat = SimpleDateFormat("mm:ss", Locale.getDefault())
    return dateFormat.format(this)
}