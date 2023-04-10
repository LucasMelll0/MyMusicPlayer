package com.example.meplayermusic.ui.musiclist.recyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.meplayermusic.databinding.MusicItemBinding
import com.example.meplayermusic.extensions.tryLoad
import com.example.meplayermusic.model.Music

abstract class MusicAdapter(
    var onClick: (music: Music) -> Unit = {},
    var onCheckBoxClick: (music: Music, isChecked: Boolean) -> Unit = { _, _ -> },
    differCallback: DiffUtil.ItemCallback<Music> = object :
        DiffUtil.ItemCallback<Music>() {
        override fun areItemsTheSame(oldItem: Music, newItem: Music): Boolean {
            return false
        }

        override fun areContentsTheSame(oldItem: Music, newItem: Music): Boolean {
            return false
        }
    }
) : ListAdapter<Music, MusicAdapter.MusicViewHolder>(differCallback) {

    inner class MusicViewHolder(private val binding: MusicItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindItem(music: Music) {
            val imageViewMusic = binding.imageviewMusicItem
            val textViewTitle = binding.textviewTitleMusicItem
            val textViewAuthor = binding.textviewAuthorMusicItem
            val checkBoxFav = binding.checkboxFavoriteMusicItem
            music.apply {
                imageViewMusic.tryLoad(image)
                textViewTitle.text = title
                textViewAuthor.text = artist
            }
            checkBoxFav.isChecked = music.isFavorite
            binding.root.setOnClickListener {
                onClick(music)
            }
            checkBoxFav.setOnClickListener {
                if (it is AppCompatCheckBox) onCheckBoxClick(music, it.isChecked)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicViewHolder {
        val binding = MusicItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MusicViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MusicViewHolder, position: Int) {
        holder.bindItem(getItem(position))
    }
}