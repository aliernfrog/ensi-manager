package com.aliernfrog.ensimanager.domain

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.aliernfrog.ensimanager.util.NavController

class AppState {
    val navController = NavController()

    var showUpdateNotification by mutableStateOf(false)
}