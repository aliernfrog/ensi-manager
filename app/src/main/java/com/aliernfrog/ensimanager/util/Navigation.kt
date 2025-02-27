package com.aliernfrog.ensimanager.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.automirrored.outlined.Article
import androidx.compose.material.icons.automirrored.outlined.Notes
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.vector.ImageVector
import com.aliernfrog.ensimanager.R
import com.aliernfrog.ensimanager.data.api.APIEndpoints

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
    val hasNotification: MutableState<Boolean> = mutableStateOf(false),
    val isAvailableInEndpoints: ((APIEndpoints) -> Boolean)? = null
) {
    DASHBOARD(
        route = "dashboard",
        labelId = R.string.dashboard,
        vectorFilled = Icons.Default.Dashboard,
        vectorOutlined = Icons.Outlined.Dashboard,
        isAvailableInEndpoints = {
            it.getDashboard != null
        }
    ),

    STRINGS(
        route = "strings",
        labelId = R.string.strings,
        vectorFilled = Icons.AutoMirrored.Filled.Article,
        vectorOutlined = Icons.AutoMirrored.Outlined.Article,
        isAvailableInEndpoints = {
            it.getStrings != null
        }
    ),

    LOGS(
        route = "logs",
        labelId = R.string.logs,
        vectorFilled = Icons.AutoMirrored.Filled.Notes,
        vectorOutlined = Icons.AutoMirrored.Outlined.Notes,
        isAvailableInEndpoints = {
            it.getLogs != null
        }
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