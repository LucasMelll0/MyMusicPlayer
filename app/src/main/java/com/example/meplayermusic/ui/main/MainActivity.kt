package com.example.meplayermusic.ui.main

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.meplayermusic.R
import com.example.meplayermusic.databinding.ActivityMainBinding
import com.example.meplayermusic.other.Visibility
import com.example.meplayermusic.ui.main.viewmodel.MainViewModel
import com.example.meplayermusic.ui.musiclist.viewModel.MusicListViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private val mainViewModel: MainViewModel by viewModel()
    private val musicListViewModel: MusicListViewModel by viewModel()
    private val requestPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            lifecycleScope.launch {
                progressBarVisibility(Visibility.VISIBLE)
                mainViewModel.fetchData(this@MainActivity)
                progressBarVisibility(Visibility.GONE)
            }
        } else {
            Toast.makeText(
                this@MainActivity,
                getString(R.string.read_storage_permission_denied_message),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        checkExternalStoragePermission {
            lifecycleScope.launch {
                progressBarVisibility(Visibility.VISIBLE)
                mainViewModel.fetchData(this@MainActivity)
                musicListViewModel.getAllFavorites()
                progressBarVisibility(Visibility.GONE)
            }
        }
    }


    private fun progressBarVisibility(visibility: Visibility) {
        binding.imageviewProgressbarMainActivity.visibility = visibility.state()
    }

    private fun checkExternalStoragePermission(onGranted: () -> Unit) {
        val permission = READ_EXTERNAL_STORAGE
        if (ContextCompat.checkSelfPermission(
                this,
                permission
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            onGranted()
        } else {
            requestPermission.launch(permission)
        }
    }

}