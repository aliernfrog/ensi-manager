package com.aliernfrog.ensimanager.util.manager

import android.content.Context
import com.aliernfrog.ensimanager.ConfigKey
import com.aliernfrog.ensimanager.ui.theme.Theme
import com.aliernfrog.ensimanager.util.manager.base.BasePreferenceManager

class PreferenceManager(context: Context) : BasePreferenceManager(
    prefs = context.getSharedPreferences(ConfigKey.PREF_NAME, Context.MODE_PRIVATE)
) {
    // Appearance options
    var theme by intPreference(ConfigKey.KEY_APP_THEME, Theme.SYSTEM.int)
    var materialYou by booleanPreference(ConfigKey.KEY_APP_MATERIAL_YOU, true)

    // API
    var apiEndpointsUrl by stringPreference(ConfigKey.KEY_API_ENDPOINTS_URL)
    var apiAuthorization by stringPreference(ConfigKey.KEY_API_AUTHORIZATION)

    // Updates options
    var autoCheckUpdates by booleanPreference(ConfigKey.KEY_APP_AUTO_UPDATES, true)
    var updatesURL by stringPreference(ConfigKey.KEY_APP_UPDATES_URL, ConfigKey.DEFAULT_UPDATES_URL)
}