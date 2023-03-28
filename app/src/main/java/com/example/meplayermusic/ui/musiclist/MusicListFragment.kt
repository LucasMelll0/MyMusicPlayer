package com.example.meplayermusic.ui.musiclist

import android.os.Bundle
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import coil.load
import com.example.meplayermusic.R
import com.example.meplayermusic.databinding.FragmentMusicListBinding
import com.example.meplayermusic.extensions.goTo
import com.example.meplayermusic.extensions.isPlaying
import com.example.meplayermusic.extensions.toMusic
import com.example.meplayermusic.extensions.tryLoad
import com.example.meplayermusic.model.Music
import com.example.meplayermusic.other.Status
import com.example.meplayermusic.ui.main.viewmodel.MainViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class MusicListFragment : Fragment() {

    private var _binding: FragmentMusicListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by viewModel()
    private var currentMusic: Music? = null
    private var playbackState: PlaybackStateCompat? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMusicListBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setsUpNavigationToPlayerFragment()
    }

    private fun setsUpToolbar() {
        val toolbar = binding.toolbarMusicListFragment
        toolbar.inflateMenu(R.menu.menu_music_list_fragment)
        toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.searchview_menu_music_list_fragment -> {
                    /*setsUpSearchView(it.actionView as? SearchView)*/
                    true
                }
                else -> false
            }
        }
    }

    /*private fun setsUpSearchView(searchView: SearchView?) {
        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    val filteredList = viewModel.searchByName(it)
                    adapter.submitList(filteredList)

                } ?: setsUpMediaItemsObserver()
                return false
            }
        })
        searchView?.setOnCloseListener {
            setsUpMediaItemsObserver()
            false
        }
    }*/

    private fun setsUpNavigationToPlayerFragment() {
        val textViewMusicTitle = binding.textviewMusicTitleMiniPlayerListFragment
        textViewMusicTitle.setOnClickListener {
            goTo(R.id.action_musicListFragment_to_PlayerFragment)
        }
    }


    override fun onStart() {
        super.onStart()
        setsUpTransportControllers()
        setsUpObservers()
        setsUpToolbar()
    }

    private fun setsUpTransportControllers() {
        val buttonPlayPause = binding.imagebuttonPlaypauseMiniPlayerListFragment
        val buttonForward = binding.imagebuttonForwardMiniPlayerListFragment
        buttonPlayPause.setOnClickListener {
            currentMusic?.let { music ->
                Log.d("Tests", "setsUpTransportControllers: $music")
                viewModel.playOrToggleMusic(music, true)
            }
        }
        buttonForward.setOnClickListener {
            viewModel.skipToNextMusic()
        }
    }

    private fun setsUpObservers() {
        setsUpMediaItemsObserver()
        viewModel.currentPlayingMusic.observe(this) {
            it?.let {
                currentMusic = it.toMusic()
                currentMusic?.let {
                    if (currentMusic!!.id.isNotEmpty()) {
                        updateCurrentlyMusic(currentMusic!!)
                    }
                }
            }
        }
        viewModel.playbackState.observe(this) {
            it?.let {
                playbackState = it
                updatePlayerState(playbackState!!)
            }
        }
        updateProgressBar()
    }

    private fun setsUpMediaItemsObserver() {
        if (viewModel.mediaItems.hasObservers()) {
            viewModel.mediaItems.removeObservers(this)
        }
        viewModel.mediaItems.observe(this) {
            it?.let { result ->
                when (result.status) {
                    Status.SUCCESS -> {
                        result.data?.let { musicList ->
                            adapter.submitList(musicList)
                            if (musicList.isNotEmpty()) {
                                val imageViewMiniPlayer =
                                    binding.imageviewMusicImageMiniPlayerListFragment
                                imageViewMiniPlayer.load((currentMusic ?: musicList[0]).image)
                            }
                        }
                    }
                    Status.ERROR -> Unit
                    Status.LOADING -> Unit
                }
            }
        }
    }

    private fun updateProgressBar() {
        val progressBarPosition = binding.progressbarMiniplayerPosition
        viewModel.currentMusicDuration.observe(this) {
            it?.let {
                progressBarPosition.max = it.toInt()
            }
        }
        viewModel.currentMusicPosition.observe(this) {
            it?.let {
                progressBarPosition.progress = it.toInt()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun updatePlayerState(pbState: PlaybackStateCompat) {
        val buttonPlayPause = binding.imagebuttonPlaypauseMiniPlayerListFragment
        buttonPlayPause.setImageResource(
            if (pbState.isPlaying) R.drawable.ic_pause else R.drawable.ic_play
        )
    }

    private fun updateCurrentlyMusic(music: Music) {
        val imageViewMusic = binding.imageviewMusicImageMiniPlayerListFragment
        val textViewTitle = binding.textviewMusicTitleMiniPlayerListFragment
        val textViewArtist = binding.textviewMusicArtistMiniPlayerListFragment
        music.apply {
            imageViewMusic.tryLoad(image)
            textViewTitle.text = title
            textViewTitle.isSelected = true
            textViewArtist.text = artist
        }
    }


}