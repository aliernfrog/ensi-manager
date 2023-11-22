package com.aliernfrog.ensimanager.util.extension

import androidx.navigation.NavController
import com.aliernfrog.ensimanager.util.Destination

/**
 * Navigates to given [destination] and removes previous destinations from back stack.
 */
fun NavController.set(destination: Destination) {
    navigate(destination.route) { popUpTo(0) }
}