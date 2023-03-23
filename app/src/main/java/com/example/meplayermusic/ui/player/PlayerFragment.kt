package com.example.meplayermusic.ui.player

import android.os.Bundle
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.example.meplayermusic.R
import com.example.meplayermusic.databinding.FragmentPlayerBinding
import com.example.meplayermusic.extensions.*
import com.example.meplayermusic.ui.main.viewmodel.MainViewModel
import com.example.meplayermusic.ui.player.viewmodel.PlayerViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlayerFragment : Fragment() {

    private var _binding: FragmentPlayerBinding? = null
    private val binding get() = _binding!!
    private val playerViewModel: PlayerViewModel by viewModel()
    private val mainViewModel: MainViewModel by viewModel()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setsUpSliderLabelFormatter()
    }

    private fun setsUpSliderLabelFormatter() {
        val sliderMusicPosition = binding.sliderMusicPlayerFragment
        sliderMusicPosition.setLabelFormatter { value: Float ->
            value.toLong().toMinutesAndSeconds()
        }
    }

    override fun onStart() {
        super.onStart()
        setsUpToolbarNavigationButton()
        setsUpObservers()
        setsUpSliderOnChangeListener()
        setsUpPlayerButtons()
    }

    private fun setsUpToolbarNavigationButton() {
        val toolbar = binding.toolbarPlayerFragment
        toolbar.setNavigationOnClickListener {
            goBack()
        }
    }

    private fun setsUpPlayerButtons() {
        val buttonPlayPause = binding.fabPlayPausePlayerFragment
        val buttonForward = binding.imagebuttonForwardPlayerFragment
        val buttonRewind = binding.imagebuttonRewindPlayerFragment
        val buttonShuffle = binding.imagebuttonShufflePlayerFragment
        val buttonRepeat = binding.imagebuttonRepeatPlayerFragment
        buttonPlayPause.setOnClickListener {
            playerViewModel.play()
        }
        buttonForward.setOnClickListener {
            playerViewModel.skipToNextMusic()
        }
        buttonRewind.setOnClickListener {
            playerViewModel.backToPreviousMusic()
        }
        buttonShuffle.setOnClickListener {
            playerViewModel.shuffle()
        }
        buttonRepeat.setOnClickListener {
            playerViewModel.repeat()
        }
    }

    private fun setsUpSliderOnChangeListener() {
        val sliderMusicPosition = binding.sliderMusicPlayerFragment
        sliderMusicPosition.addOnChangeListener { _, value, fromUser ->
            if (fromUser) {
                playerViewModel.seekTo(value.toLong())
            }
        }
    }

    private fun setsUpObservers() {
        mainViewModel.currentPlayingMusic.observe(this) {
            it?.let {
                updateCurrentMusicMetadata(it)
            }
        }
        mainViewModel.currentMusicDuration.observe(this) {
            it?.let {
                if (it > 0) {
                    updateCurrentDuration(it)
                }
            }
        }
        mainViewModel.currentMusicPosition.observe(this) {
            it?.let {
                if (it > 0) {
                    updateCurrentPosition(it)
                }
            }
        }
        playerViewModel.playbackState.observe(this) {
            it?.let {
                updateButtonPlayPause(it)
            }
        }
        playerViewModel.shuffleMode.observe(this) {
            setsUpShuffleButtonIcon(it)
            Log.d("Tests", "Shuffle: $it")
        }

        playerViewModel.repeatMode.observe(this) {
            setsUpRepeatButtonIcon(it)
            Log.d("Tests", "Repeat: $it")
        }

    }

    private fun updateCurrentDuration(duration: Long) {
        val textViewMusicDuration = binding.textviewMusicDurationPlayerFragment
        val sliderMusicPosition = binding.sliderMusicPlayerFragment
        sliderMusicPosition.valueTo = duration.toFloat()
        textViewMusicDuration.text = duration.toMinutesAndSeconds()
    }

    private fun setsUpRepeatButtonIcon(enabled: Boolean) {
        val buttonRepeat = binding.imagebuttonRepeatPlayerFragment
        buttonRepeat.setImageResource(if (enabled) R.drawable.ic_repeat_enabled else R.drawable.ic_repeat)
    }

    private fun setsUpShuffleButtonIcon(enabled: Boolean) {
        val buttonShuffle = binding.imagebuttonShufflePlayerFragment
        buttonShuffle.setImageResource(if (enabled) R.drawable.ic_shuffle_enabled else R.drawable.ic_shuffle)
    }

    private fun updateButtonPlayPause(it: PlaybackStateCompat) {
        val buttonPlayPause = binding.fabPlayPausePlayerFragment
        buttonPlayPause.icon =
            if (it.isPlaying) ResourcesCompat.getDrawable(
                resources,
                R.drawable.ic_pause,
                null
            ) else ResourcesCompat.getDrawable(
                resources,
                R.drawable.ic_play,
                null
            )
    }

    private fun updateCurrentPosition(it: Long) {
        val textViewMusicPosition = binding.textviewMusicPositionPlayerFragment
        val sliderMusicPosition = binding.sliderMusicPlayerFragment
        sliderMusicPosition.value = it.toFloat()
        textViewMusicPosition.text = it.toMinutesAndSeconds()

    }

    private fun updateCurrentMusicMetadata(mediaMetadataCompat: MediaMetadataCompat) {
        mediaMetadataCompat.toMusic()?.let { music ->
            val imageViewMusic = binding.imageviewMusicImagePlayerFragment
            val textViewTitle = binding.textviewMusicTitlePlayerFragment
            val textViewArtist = binding.textviewMusicArtistPlayerFragment
            imageViewMusic.tryLoad(music.image)
            textViewTitle.text = music.title
            textViewArtist.text = music.artist
        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}