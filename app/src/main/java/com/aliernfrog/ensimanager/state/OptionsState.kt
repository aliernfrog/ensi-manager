package com.aliernfrog.ensimanager.state

import android.content.SharedPreferences
import androidx.compose.runtime.mutableStateOf
import com.aliernfrog.ensimanager.ConfigKey
import com.aliernfrog.ensimanager.Theme

class OptionsState(_config: SharedPreferences) {
    val config = _config

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