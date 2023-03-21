package com.example.meplayermusic.extensions

import android.os.Bundle
import androidx.annotation.IdRes
import androidx.navigation.NavController
import androidx.navigation.NavGraph

fun NavController.safeNavigate(
    @IdRes id: Int,
    args: Bundle? = null
) {
    val destinationId = currentDestination?.getAction(id)?.destinationId.orEmpty()
    currentDestination?.let { node ->
        val currentNode = when (node) {
            is NavGraph -> node
            else -> node.parent
        }
        if (destinationId != 0) {
            currentNode?.findNode(destinationId)?.let { navigate(id, args) }
        }
    }
}