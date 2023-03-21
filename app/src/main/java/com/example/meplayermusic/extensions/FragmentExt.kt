package com.example.meplayermusic.extensions

import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.meplayermusic.model.Music

fun Fragment.goBack() {
    context?.let {
        findNavController().popBackStack()
    }
}
fun Fragment.goTo(destination: Int) {
    context?.let {
        findNavController().safeNavigate(destination)
    }
}

fun Fragment.findMusicMetaData(uri: Uri): Music? {
    return context?.let {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(context, uri)
        val image = retriever.embeddedPicture?.let { BitmapFactory.decodeByteArray(it, 0, it.size) }
        val title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)
        val author = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST)
        val duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
        Music(
            image = image,
            title = title ?: "",
            artist = author ?: "",
            duration = duration?.toInt() ?: 0,
            uri = ""
        )
    }
}