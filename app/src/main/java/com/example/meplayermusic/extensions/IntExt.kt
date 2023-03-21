package com.example.meplayermusic.extensions

fun Int?.orEmpty(default: Int = 0): Int {
    return this ?: default
}