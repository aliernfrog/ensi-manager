package com.aliernfrog.ensimanager.state

import android.content.SharedPreferences
import androidx.compose.foundation.ScrollState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.aliernfrog.ensimanager.ConfigKey
import com.aliernfrog.ensimanager.Theme

@OptIn(ExperimentalMaterial3Api::class)
class SettingsState(
    val config: SharedPreferences,
    val scrollState: ScrollState
) {
    val topAppBarState = TopAppBarState(0F, 0F, 0F)

    var theme by mutableStateOf(config.getInt(ConfigKey.KEY_APP_THEME, Theme.SYSTEM))
    var materialYou by mutableStateOf(config.getBoolean(ConfigKey.KEY_APP_MATERIAL_YOU, true))

    var themeOptionsExpanded by mutableStateOf(false)

    fun updateTheme(newTheme: Int) {
        config.edit().putInt(ConfigKey.KEY_APP_THEME, newTheme).apply()
        theme = newTheme
    }

    fun updateMaterialYou(newPreference: Boolean) {
        config.edit().putBoolean(ConfigKey.KEY_APP_MATERIAL_YOU, newPreference).apply()
        materialYou = newPreference
    }
}