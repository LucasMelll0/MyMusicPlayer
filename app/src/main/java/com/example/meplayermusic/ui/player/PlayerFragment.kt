package com.example.meplayermusic.ui.player

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.meplayermusic.MUSIC_URI_EXTRA
import com.example.meplayermusic.PAUSE_ICON
import com.example.meplayermusic.PLAY_ICON
import com.example.meplayermusic.R
import com.example.meplayermusic.databinding.FragmentPlayerBinding
import com.example.meplayermusic.datasource.MusicDataSource
import com.example.meplayermusic.extensions.findMusicMetaData
import com.example.meplayermusic.extensions.requestPermission
import com.example.meplayermusic.extensions.tryLoad
import com.example.meplayermusic.model.Music
import com.example.meplayermusic.services.MediaPlayerService
import com.google.android.material.slider.Slider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlayerFragment : Fragment() {

    private var _binding: FragmentPlayerBinding? = null
    private val binding get() = _binding!!
    private lateinit var mediaPlayerService: MediaPlayerService
    private var musicUri: Uri? = null
    private val viewModel: PlayerViewModel by viewModel()

    // Flag para verificar se o serviço esta vinculado
    private var mBound: Boolean = false

    // Define callbacks para o serviço vinculado, passado para o metodo bindService()
    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as MediaPlayerService.MediaPlayerBinder
            mediaPlayerService = binder.getService()
            mBound = true
            val player = mediaPlayerService.player
            if (player.isPlaying) {
                toggleFabPlayPause(PAUSE_ICON)
            } else {
                toggleFabPlayPause(PLAY_ICON)
            }
            setsUpProgressBar()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            mBound = false
        }
    }


    private val pickAudio =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                activity?.let {
                    musicUri = uri
                    val music = findMusicMetaData(uri)
                    music?.let {
                        viewModel.setMusicMetaData(music)
                        updateUi(music)
                    }
                    initMusicPlayerService(uri)

                }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpImageViewMusic()
        setsUpFabPlayPause()
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            launch {
                viewModel.getMusicMetaData()?.let {
                    Log.i("Tests", "musicMetadataObserver")
                    updateUi(it)
                }
            }
            launch {
                viewModel.getPlayerServiceIntent()?.let {
                    Log.i("Tests", "playerServiceintentObserver")
                    bindService(it)
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        Log.i("Test", "onStart: ${MusicDataSource.musicList}")
    }

    private fun initMusicPlayerService(uri: Uri) {
        Intent(requireActivity(), MediaPlayerService::class.java).also { intent ->
            intent.putExtra(MUSIC_URI_EXTRA, uri.toString())
            bindService(intent)
            requireActivity().startService(intent)
            viewModel.savePlayerServiceIntent(intent)
            toggleFabPlayPause(PAUSE_ICON)
            setsUpProgressBar()
        }
    }

    private fun bindService(intent: Intent) {
        requireActivity().bindService(intent, connection, Context.BIND_AUTO_CREATE)
    }

    private fun toggleFabPlayPause(icon: Int) {
        activity?.let {
            val fabPlayPause = binding.fabPlayPausePlayerFragment
            fabPlayPause.icon =
                when (icon) {
                    PAUSE_ICON -> {
                        ResourcesCompat.getDrawable(resources, R.drawable.ic_pause, null)
                    }
                    PLAY_ICON -> {
                        ResourcesCompat.getDrawable(resources, R.drawable.ic_play, null)
                    }
                    else -> {
                        ResourcesCompat.getDrawable(resources, R.drawable.ic_pause, null)
                    }
                }
        }
    }

    private fun updateUi(music: Music) {
        Log.i("Tests", "updateUi: $music")
        val imageViewMusic = binding.imageviewMusicImagePlayerFragment
        val textViewTitle = binding.textviewMusicTitlePlayerFragment
        val textViewAuthor = binding.textviewMusicAuthorPlayerFragment
        music.apply {
            image?.let {
                imageViewMusic.tryLoad(it)
            }
            title.let {
                textViewTitle.text = it
            }
            artist.let {
                textViewAuthor.text = it
            }
        }
    }


    private fun setsUpFabPlayPause() {
        val fabPlayPause = binding.fabPlayPausePlayerFragment
        fabPlayPause.setOnClickListener {
            activity?.let {
                if (mBound) {
                    val player = mediaPlayerService.player
                    if (player.isPlaying) {
                        player.pause()
                        toggleFabPlayPause(PLAY_ICON)
                    } else {
                        player.start()
                        setsUpProgressBar()
                        toggleFabPlayPause(PAUSE_ICON)
                    }
                }
            }
        }
    }

    private fun setsUpProgressBar() {
        val sliderMusic = binding.sliderMusicPlayerFragment
        if (mBound) {
            val player = mediaPlayerService.player
            sliderMusic.valueTo = player.duration.toFloat()
            lifecycleScope.launch {
                launch {
                    sliderMusic.addOnChangeListener(Slider.OnChangeListener { _, value, fromUser ->
                        if (fromUser) {
                            mediaPlayerService.player.seekTo(value.toInt())
                        }
                    })
                }
                withContext(Dispatchers.IO) {
                    while (sliderMusic.value < sliderMusic.valueTo) {
                        sliderMusic.value = player.currentPosition.toFloat()
                        Thread.sleep(200)
                    }
                }
            }
        }
    }

    private fun setUpImageViewMusic() {
        val imageViewMusic = binding.imageviewMusicImagePlayerFragment
        imageViewMusic.setOnClickListener {
            openAudioPicker()
        }
    }

    private fun openAudioPicker() {
        context?.let {
            val permission = READ_EXTERNAL_STORAGE
            if (ContextCompat.checkSelfPermission(
                    it,
                    permission
                ) == PackageManager.PERMISSION_DENIED
            ) {
                requestPermission(permission) {
                    pickAudio.launch("audio/*")
                }
            } else {
                pickAudio.launch("audio/*")
            }
        }
    }

    override fun onStop() {
        super.onStop()
        activity?.let {
            if (mBound) {
                it.unbindService(connection)
                mBound = false
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}