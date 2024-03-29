package com.example.meplayermusic.ui.musiclist.favorites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.meplayermusic.constantes.MEDIA_FAVORITES_ID
import com.example.meplayermusic.databinding.FragmentFavoritesBinding
import com.example.meplayermusic.other.Visibility
import com.example.meplayermusic.ui.main.viewmodel.MainViewModel
import com.example.meplayermusic.ui.musiclist.recyclerview.FavoritesAdapter
import com.example.meplayermusic.ui.musiclist.viewModel.MusicListViewModel
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class FavoritesFragment : Fragment() {

    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!
    private val adapter: FavoritesAdapter by inject()
    private val musicListViewModel: MusicListViewModel by activityViewModel()
    private val mainViewModel: MainViewModel by activityViewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setsUpRecyclerView()
    }

    override fun onStart() {
        super.onStart()
        setsUpAdapterFunctions()
        setsUpObserver()
        setsUpSearchBar()
    }

    private fun setsUpSearchBar() {
        val searchView = binding.searchviewSearchbar
        val cardViewSearchBar = binding.cardviewSearchbarFavoritesFragment
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
                    val filteredList = musicListViewModel.searchByName(it, MEDIA_FAVORITES_ID)
                    adapter.submitList(filteredList)

                } ?: setsUpObserver()
                return false
            }
        })
        searchView?.setOnCloseListener {
            setsUpObserver()
            false
        }
    }

    private fun setsUpAdapterFunctions() {
        adapter.onClick = {
            mainViewModel.playOrToggleMusic(it, false, MEDIA_FAVORITES_ID)
        }
        adapter.onCheckBoxClick = { music, isChecked ->
            if (isChecked) {
                musicListViewModel.addToFavorites(music)
            } else {
                musicListViewModel.removeFromFavorites(music)
            }
        }
    }

    private fun setsUpObserver() {
        musicListViewModel.favorites.observe(this@FavoritesFragment) {
            progressBarVisibility(Visibility.VISIBLE)
            adapter.submitList(it)
            progressBarVisibility(Visibility.INVISIBLE)
            if (it.isEmpty()) {
                emptyListMessageVisibility(Visibility.VISIBLE)
            }else {
                emptyListMessageVisibility(Visibility.INVISIBLE)
            }

        }
    }

    private fun progressBarVisibility(visibility: Visibility) {
        binding.progressbarFavoritesFragment.visibility = visibility.state()
    }

    private fun emptyListMessageVisibility(visibility: Visibility) {
        binding.emptyListMessageFavoritesFragment.visibility = visibility.state()
    }

    private fun setsUpRecyclerView() {
        val recyclerViewFavorites = binding.recyclerviewFavoritesFragment
        recyclerViewFavorites.layoutManager = LinearLayoutManager(requireContext())
        recyclerViewFavorites.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}