package com.example.meplayermusic.ui.musiclist.all

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.meplayermusic.constantes.MEDIA_ROOT_ID
import com.example.meplayermusic.databinding.FragmentAllMusicsBinding
import com.example.meplayermusic.other.Status
import com.example.meplayermusic.other.Visibility
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

    private fun setsUpSearchBar() {
        val searchView = binding.searchviewSearchbar
        val cardViewSearchBar = binding.cardviewSearchbarAllMusicsFragment
        cardViewSearchBar.setOnClickListener {
            if (searchView.isIconified) {
                searchView.isIconified = false
            }
        }
        setsUpSearchView(searchView)
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
        setsUpSearchBar()
        setsUpSwipeRefresh()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setsUpSwipeRefresh() {
        val swipeRefresh = binding.swipeRefreshAllMusicsFragment
        swipeRefresh.setOnRefreshListener {
            musicListViewModel.getAll()
            Log.d("Tests", "setsUpSwipeRefresh: ${musicListViewModel.musics.value?.data?.size}")
            adapter.notifyDataSetChanged()
            swipeRefresh.isRefreshing = false
        }
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

    private fun emptyListMessageVisibility(visibility: Visibility) {
        binding.emptyListMessageAllMusicsFragment.visibility = visibility.state()
    }

    private fun progressBarVisibility(visibility: Visibility) {
        binding.progressbarAllMusicsFragment.visibility = visibility.state()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setsUpObservers() {
        musicListViewModel.musics.observe(this) { Resource ->
            Resource?.let { result ->
                emptyListMessageVisibility(Visibility.INVISIBLE)
                progressBarVisibility(Visibility.INVISIBLE)
                when (result.status) {
                    Status.SUCCESS -> {
                        result.data?.let { musicList ->
                           adapter.submitList(musicList)
                            adapter.notifyDataSetChanged()
                        }
                    }
                    Status.ERROR -> {
                        emptyListMessageVisibility(Visibility.VISIBLE)
                    }
                    Status.LOADING -> {
                        progressBarVisibility(Visibility.VISIBLE)
                    }
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