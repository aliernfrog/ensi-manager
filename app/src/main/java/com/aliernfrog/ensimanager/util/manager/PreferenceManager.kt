package com.aliernfrog.ensimanager.util.manager

import android.content.Context
import com.aliernfrog.ensimanager.ui.theme.Theme
import com.aliernfrog.ensimanager.util.manager.base.BasePreferenceManager

class PreferenceManager(context: Context) : BasePreferenceManager(
    prefs = context.getSharedPreferences("APP_CONFIG", Context.MODE_PRIVATE)
) {
    // Appearance options
    val theme = intPreference("appTheme", Theme.SYSTEM.ordinal)
    val materialYou = booleanPreference("materialYou", true)
    val pitchBlack = booleanPreference("pitchBlack", false)

    // API
    val rememberLastAPIProfile = booleanPreference("rememberLastApiProfile", true)
    val apiProfiles = stringPreference("apiProfiles", "[]")
    val lastActiveAPIProfileId = stringPreference("lastActiveApiProfileId")

    // Experimental (developer) options
    val experimentalOptionsEnabled = booleanPreference("experimentalOptionsEnabled", false)
    val autoCheckUpdates = booleanPreference("autoUpdates", true)
    val updatesURL = stringPreference("updatesUrl", "https://aliernfrog.github.io/ensimanager/latest.json", experimental = true, includeInDebugInfo = false)
}