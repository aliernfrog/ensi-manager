package com.aliernfrog.ensimanager.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.automirrored.outlined.Notes
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.vector.ImageVector
import com.aliernfrog.ensimanager.R

object NavigationConstant {
    val INITIAL_DESTINATION = Destination.DASHBOARD.route
}

enum class Destination(
    val route: String,
    val labelId: Int,
    val vectorFilled: ImageVector? = null,
    val vectorOutlined: ImageVector? = null,
    val showInNavigationBar: Boolean = true,
    val showNavigationBar: Boolean = showInNavigationBar,
    val hasNotification: MutableState<Boolean> = mutableStateOf(false)
) {
    DASHBOARD(
        route = "dashboard",
        labelId = R.string.dashboard,
        vectorFilled = Icons.Default.Dashboard,
        vectorOutlined = Icons.Outlined.Dashboard
    ),

    CHAT(
        route = "chat",
        labelId = R.string.chat,
        vectorFilled = Icons.AutoMirrored.Filled.Chat,
        vectorOutlined = Icons.AutoMirrored.Outlined.Chat
    ),

    LOGS(
        route = "logs",
        labelId = R.string.logs,
        vectorFilled = Icons.AutoMirrored.Filled.Notes,
        vectorOutlined = Icons.AutoMirrored.Outlined.Notes
    ),

    API_PROFILES(
        route = "apiProfiles",
        labelId = R.string.api_profiles,
        showInNavigationBar = false
    ),

    SETTINGS(
        route = "settings",
        labelId = R.string.settings,
        showInNavigationBar = false
    )
}