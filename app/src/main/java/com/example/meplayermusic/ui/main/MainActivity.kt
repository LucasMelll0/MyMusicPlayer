package com.example.meplayermusic.ui.main

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.meplayermusic.R
import com.example.meplayermusic.databinding.ActivityMainBinding
import com.example.meplayermusic.datasource.MusicDataSource
import com.example.meplayermusic.other.Visibility

class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private val requestPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            MusicDataSource.fetchMediaData(this@MainActivity)
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
            progressBarVisibility(Visibility.VISIBLE)
            MusicDataSource.fetchMediaData(this@MainActivity)
            progressBarVisibility(Visibility.GONE)
        }
    }

    private fun progressBarVisibility(visibility: Visibility) {
        val progressBar = binding.progressbarMainActivity
        progressBar.visibility = when(visibility) {
            Visibility.VISIBLE -> View.VISIBLE
            Visibility.GONE -> View.GONE
            Visibility.INVISIBLE -> View.INVISIBLE
        }
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