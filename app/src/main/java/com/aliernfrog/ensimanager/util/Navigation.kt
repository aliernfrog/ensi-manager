package com.aliernfrog.ensimanager.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.Settings
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
    val isSubScreen: Boolean = false
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

    SETTINGS(
        route = "settings",
        labelId = R.string.settings,
        vectorFilled = Icons.Default.Settings,
        vectorOutlined = Icons.Outlined.Settings
    ),

    API_CONFIG(
        route = "apiConfig",
        labelId = R.string.setup,
        isSubScreen = true
    )
}