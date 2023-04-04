package com.aliernfrog.ensimanager.state

import android.content.SharedPreferences
import androidx.compose.foundation.ScrollState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarState
import androidx.compose.runtime.mutableStateOf
import com.aliernfrog.ensimanager.ConfigKey
import com.aliernfrog.ensimanager.Theme

@OptIn(ExperimentalMaterial3Api::class)
class OptionsState(
    val config: SharedPreferences,
    val scrollState: ScrollState
) {
    val topAppBarState = TopAppBarState(0F, 0F, 0F)

    val theme = mutableStateOf(config.getInt(ConfigKey.KEY_APP_THEME, Theme.SYSTEM))
    val materialYou = mutableStateOf(config.getBoolean(ConfigKey.KEY_APP_MATERIAL_YOU, true))

    fun setTheme(newTheme: Int) {
        config.edit().putInt(ConfigKey.KEY_APP_THEME, newTheme).apply()
        theme.value = newTheme
    }

    fun setMaterialYou(newPreference: Boolean) {
        config.edit().putBoolean(ConfigKey.KEY_APP_MATERIAL_YOU, newPreference).apply()
        materialYou.value = newPreference
    }
}