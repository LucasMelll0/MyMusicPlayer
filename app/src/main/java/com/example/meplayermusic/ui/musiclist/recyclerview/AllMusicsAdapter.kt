package com.example.meplayermusic.ui.musiclist.recyclerview

import androidx.recyclerview.widget.DiffUtil
import com.example.meplayermusic.model.Music

class AllMusicsAdapter(
    onClick: (music: Music) -> Unit = {},
    onCheckBoxClick: (music: Music, isChecked: Boolean) -> Unit = { _, _ -> }
) : MusicAdapter(onClick, onCheckBoxClick, differCallBack) {

    companion object {
        private val differCallBack = object : DiffUtil.ItemCallback<Music>() {
            override fun areItemsTheSame(oldItem: Music, newItem: Music): Boolean {
                return oldItem.uri == newItem.uri
            }

            override fun areContentsTheSame(oldItem: Music, newItem: Music): Boolean {
                return oldItem == newItem && oldItem.isFavorite != newItem.isFavorite
            }
        }
    }
}