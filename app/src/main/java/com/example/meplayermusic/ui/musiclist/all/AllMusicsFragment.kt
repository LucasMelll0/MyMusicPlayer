package com.example.meplayermusic.ui.musiclist.all

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.meplayermusic.constantes.MEDIA_ROOT_ID
import com.example.meplayermusic.databinding.FragmentAllMusicsBinding
import com.example.meplayermusic.other.Status
import com.example.meplayermusic.ui.main.viewmodel.MainViewModel
import com.example.meplayermusic.ui.musiclist.recyclerview.AllMusicsAdapter
import com.example.meplayermusic.ui.musiclist.viewModel.MusicListViewModel
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

    private fun setsUpSearchView(searchView: SearchView?) {
        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    val filteredList = musicListViewModel.searchByName(it, MEDIA_ROOT_ID)
                    adapter.submitList(filteredList)

                } ?: setsUpObservers()
                return false
            }
        })
        searchView?.setOnCloseListener {
            setsUpObservers()
            false
        }
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
        if (musicListViewModel.favorites.hasObservers()) musicListViewModel.favorites.removeObservers(
            this
        )
        if (musicListViewModel.musics.hasObservers()) musicListViewModel.musics.removeObservers(this)

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
        musicListViewModel.favorites.observe(this) {
            musicListViewModel.update(it)
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