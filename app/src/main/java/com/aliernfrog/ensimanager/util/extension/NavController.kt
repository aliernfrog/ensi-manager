package com.aliernfrog.ensimanager.util.extension

import androidx.navigation.NavController
import com.aliernfrog.ensimanager.util.Destination


/**
 * Pops back stack only if it exists.
 */
fun NavController.popBackStackSafe(onNoBackStack: () -> Unit = {}) {
    if (previousBackStackEntry != null) popBackStack()
    else onNoBackStack()
}

/**
 * Navigates to given [destination] and removes previous destinations from back stack.
 */
fun NavController.set(destination: Destination) {
    set(destination.route)
}

/**
 * Navigates to given [route] and removes previous destinations from back stack.
 */
fun NavController.set(route: String) {
    navigate(route) { popUpTo(0) }
}