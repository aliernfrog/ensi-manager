package com.aliernfrog.ensimanager.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Chat
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.stringResource
import com.aliernfrog.ensimanager.R
import com.aliernfrog.ensimanager.data.Screen

object NavigationConstant {
    val INITIAL_DESTINATION = Destination.DASHBOARD.route
}

enum class Destination(
    val route: String,
    val labelId: Int,
    val vector: ImageVector? = null,
    val vectorSelected: ImageVector? = null,
    val isSubScreen: Boolean = false
) {
    SETUP("setup", R.string.setup, Icons.Default.Settings, Icons.Outlined.Settings, isSubScreen = true),
    CHAT("chat", R.string.screen_chat, Icons.Default.Chat, Icons.Outlined.Chat),
    DASHBOARD("dashboard", R.string.screen_dashboard, Icons.Default.Dashboard, Icons.Outlined.Dashboard),
    SETTINGS("settings", R.string.settings, Icons.Default.Settings, Icons.Outlined.Settings)
}

@Composable
fun getScreens(): List<Screen> {
    return Destination.values().map { destination ->
        Screen(
            route = destination.route,
            name = stringResource(destination.labelId),
            iconFilled = destination.vector?.let { rememberVectorPainter(it) },
            iconOutlined = destination.vectorSelected?.let { rememberVectorPainter(it) },
            isSubScreen = destination.isSubScreen
        )
    }
}