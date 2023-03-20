package com.example.meplayermusic.ui.musiclist.recyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.meplayermusic.databinding.MusicItemBinding
import com.example.meplayermusic.extensions.tryLoad
import com.example.meplayermusic.model.Music

class MusicAdapter(
    var onClick: (music: Music) -> Unit = {}
) : ListAdapter<Music, MusicAdapter.MusicViewHolder>(differCallBack) {

    companion object {
        private val differCallBack = object : DiffUtil.ItemCallback<Music>() {
            override fun areItemsTheSame(oldItem: Music, newItem: Music): Boolean {
                return oldItem.uri == newItem.uri
            }

            override fun areContentsTheSame(oldItem: Music, newItem: Music): Boolean {
                return oldItem == newItem
            }
        }
    }

    inner class MusicViewHolder(private val binding: MusicItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindItem(music: Music) {
            val imageViewMusic = binding.imageviewMusicItem
            val textViewTitle = binding.textviewTitleMusicItem
            val textViewAuthor = binding.textviewAuthorMusicItem
            music.apply {
                imageViewMusic.tryLoad(image)
                textViewTitle.text = title
                textViewAuthor.text = artist
            }
            binding.root.setOnClickListener {
                onClick(music)
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