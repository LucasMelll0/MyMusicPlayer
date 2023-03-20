package com.example.meplayermusic.extensions

import android.util.Log
import android.widget.ImageView
import androidx.core.content.res.ResourcesCompat
import coil.load
import com.example.meplayermusic.R

const val TAG = "ImageView Extensions"

fun ImageView.tryLoad(image: Any?) {
    try {
        this.load(image) {
            fallback(R.drawable.ic_note)
            error(R.drawable.ic_note)
        }
    } catch (e: Exception) {
        Log.w(TAG, "TryLoad: ", e)
        this.load(ResourcesCompat.getDrawable(resources, R.drawable.ic_note, null))
    }

}