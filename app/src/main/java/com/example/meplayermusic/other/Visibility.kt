package com.example.meplayermusic.other

import android.view.View

enum class Visibility {
    VISIBLE {
        override fun state(): Int = View.VISIBLE
    },
    GONE {
        override fun state(): Int = View.GONE
    },
    INVISIBLE {
        override fun state(): Int = View.INVISIBLE
    };

    abstract fun state() : Int
}