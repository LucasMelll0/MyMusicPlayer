package com.example.meplayermusic.ui.musiclist.all

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.meplayermusic.databinding.FragmentAllMusicsBinding
import com.example.meplayermusic.other.Status
import com.example.meplayermusic.ui.main.viewmodel.MainViewModel
import com.example.meplayermusic.ui.musiclist.viewModel.MusicListViewModel
import com.example.meplayermusic.ui.musiclist.recyclerview.AllMusicsAdapter
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class AllMusicsFragment : Fragment() {

    private var _binding: FragmentAllMusicsBinding? = null
    private val binding get() = _binding!!
    private val adapter: AllMusicsAdapter by inject()
    private val mainViewModel: MainViewModel by viewModel()
    private val musicListViewModel: MusicListViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAllMusicsBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setsUpRecyclerView()
    }

    override fun onStart() {
        super.onStart()
        musicListViewModel.getAll()
        setsUpObservers()
        setsUpAdapterFunctions()
    }

    private fun setsUpAdapterFunctions() {
        adapter.onClick = {
            mainViewModel.playOrToggleMusic(it, false)
        }
        adapter.onCheckBoxClick = { music, isChecked ->
            if (isChecked) {
                musicListViewModel.addToFavorites(music)
            } else {
                musicListViewModel.removeFromFavorites(music)
            }
        }
    }

    private fun setsUpObservers() {
        musicListViewModel.favorites.observe(this) {
            musicListViewModel.update(it)
        }
        musicListViewModel.musics.observe(this) {
            it?.let { result ->
                when (result.status) {
                    Status.SUCCESS -> {
                        result.data?.let { musicList ->
                            adapter.submitList(musicList)
                        }
                    }
                    Status.ERROR -> Unit
                    Status.LOADING -> Unit
                }
            }
        }
    }

    private fun setsUpRecyclerView() {
        val recyclerViewMusic = binding.recyclerviewMusicListFragment
        recyclerViewMusic.adapter = adapter
        recyclerViewMusic.layoutManager = LinearLayoutManager(requireContext())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}