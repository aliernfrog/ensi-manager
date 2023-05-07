package com.aliernfrog.ensimanager.state

import android.content.SharedPreferences
import androidx.compose.foundation.ScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Build
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.aliernfrog.ensimanager.ConfigKey
import com.aliernfrog.ensimanager.R
import com.aliernfrog.ensimanager.Theme
import com.aliernfrog.ensimanager.experimentalSettingsRequiredClicks
import com.aliernfrog.toptoast.enum.TopToastColor
import com.aliernfrog.toptoast.state.TopToastState

@OptIn(ExperimentalMaterial3Api::class)
class SettingsState(
    val topToastState: TopToastState,
    val config: SharedPreferences
) {
    val topAppBarState = TopAppBarState(0F, 0F, 0F)
    val scrollState = ScrollState(0)
    private var aboutClickCount = 0

    var theme by mutableStateOf(config.getInt(ConfigKey.KEY_APP_THEME, Theme.SYSTEM))
    var materialYou by mutableStateOf(config.getBoolean(ConfigKey.KEY_APP_MATERIAL_YOU, true))
    var autoCheckUpdates by mutableStateOf(config.getBoolean(ConfigKey.KEY_APP_AUTO_UPDATES, true))

    var themeOptionsExpanded by mutableStateOf(false)
    var linksExpanded by mutableStateOf(false)
    var forceShowMaterialYouOption by mutableStateOf(false)
    var experimentalSettingsShown by mutableStateOf(false)

    fun updateTheme(newTheme: Int) {
        config.edit().putInt(ConfigKey.KEY_APP_THEME, newTheme).apply()
        theme = newTheme
    }

    fun updateMaterialYou(newPreference: Boolean) {
        config.edit().putBoolean(ConfigKey.KEY_APP_MATERIAL_YOU, newPreference).apply()
        materialYou = newPreference
    }

    fun updateAutoCheckUpdates(newPreference: Boolean) {
        config.edit().putBoolean(ConfigKey.KEY_APP_AUTO_UPDATES, newPreference).apply()
        autoCheckUpdates = newPreference
    }

    fun onAboutClick() {
        if (aboutClickCount > experimentalSettingsRequiredClicks) return
        aboutClickCount++
        if (aboutClickCount == experimentalSettingsRequiredClicks) {
            experimentalSettingsShown = true
            topToastState.showToast(
                text = R.string.settings_experimental_enabled,
                icon = Icons.Rounded.Build,
                iconTintColor = TopToastColor.ON_SURFACE
            )
        }
    }
}