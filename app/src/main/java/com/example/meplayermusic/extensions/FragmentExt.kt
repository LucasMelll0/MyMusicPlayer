package com.example.meplayermusic.extensions

import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

fun Fragment.goBack() {
    context?.let {
        findNavController().popBackStack()
    }
}
fun Fragment.goTo(destination: Int) {
    context?.let {
        findNavController().safeNavigate(destination)
    }
}



