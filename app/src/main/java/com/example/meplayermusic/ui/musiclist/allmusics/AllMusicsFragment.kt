package com.example.meplayermusic.ui.musiclist.allmusics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.meplayermusic.databinding.FragmentAllMusicsBinding
import com.example.meplayermusic.ui.main.viewmodel.MainViewModel
import com.example.meplayermusic.ui.musiclist.recyclerview.MusicAdapter
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class AllMusicsFragment : Fragment() {

    private var _binding: FragmentAllMusicsBinding? = null
    private val binding get() = _binding!!
    private val adapter: MusicAdapter by inject()
    private val viewModel: MainViewModel by viewModel()

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
        setsUpObservers()
    }

    private fun setsUpObservers() {

    }

    private fun setsUpRecyclerView() {
        val recyclerViewMusic = binding.recyclerviewMusicListFragment
        recyclerViewMusic.adapter = adapter
        recyclerViewMusic.layoutManager = LinearLayoutManager(requireContext())
    }
}