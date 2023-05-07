package com.aliernfrog.ensimanager

import com.aliernfrog.ensimanager.data.PrefEditItem
import com.aliernfrog.ensimanager.data.Social

const val githubRepoURL = "https://github.com/aliernfrog/ensi-manager"
const val experimentalSettingsRequiredClicks = 10

object ConfigKey {
    const val PREF_NAME = "APP_CONFIG"
    const val KEY_APP_THEME = "appTheme"
    const val KEY_APP_MATERIAL_YOU = "materialYou"
    const val KEY_APP_AUTO_UPDATES = "autoUpdates"
    const val KEY_APP_UPDATES_URL = "updatesUrl"
    const val KEY_API_ENDPOINTS_URL = "apiEndpointsUrl"
    const val KEY_API_AUTHORIZATION = "apiAuthorization"
    const val DEFAULT_UPDATES_URL = "https://aliernfrog.github.io/ensimanager/latest.json"
}

object SettingsConstant {
    val socials = listOf(
        Social("Ensi Manager GitHub", githubRepoURL),
        Social("aliernfrog Discord", "https://discord.gg/SQXqBMs"),
        Social("aliernfrog website", "https://aliernfrog.github.io")
    )
    val experimentalPrefOptions = listOf(
        PrefEditItem(
            key = ConfigKey.KEY_APP_UPDATES_URL,
            default = ConfigKey.DEFAULT_UPDATES_URL
        )
    )
}

object Theme {
    const val SYSTEM = 0
    const val LIGHT = 1
    const val DARK = 2
}

object ChatScreenType {
    const val WORDS = 0
    const val VERBS = 1
}

object FetchingState {
    const val FETCHING = 0
    const val DONE = 1
}