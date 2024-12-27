package com.aliernfrog.ensimanager.util.manager

import android.content.Context
import com.aliernfrog.ensimanager.ui.theme.Theme
import com.aliernfrog.ensimanager.util.manager.base.BasePreferenceManager

class PreferenceManager(context: Context) : BasePreferenceManager(
    prefs = context.getSharedPreferences("APP_CONFIG", Context.MODE_PRIVATE)
) {
    // Appearance options
    var theme = intPreference("appTheme", Theme.SYSTEM.ordinal)
    var materialYou = booleanPreference("materialYou", true)
    var pitchBlack = booleanPreference("pitchBlack", false)

    // API
    var apiEndpointsUrl = stringPreference("apiEndpointsUrl", experimental = true, includeInDebugInfo = false)
    var apiAuthorization = stringPreference("apiAuthorization", experimental = true, includeInDebugInfo = false)

    // Experimental (developer) options
    var experimentalOptionsEnabled = booleanPreference("experimentalOptionsEnabled", false)
    var autoCheckUpdates = booleanPreference("autoUpdates", true)
    var updatesURL = stringPreference("updatesUrl", "https://aliernfrog.github.io/ensimanager/latest.json", experimental = true, includeInDebugInfo = false)
}