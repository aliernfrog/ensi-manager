package com.aliernfrog.ensimanager

object ConfigKey {
    const val PREF_NAME = "APP_CONFIG"
    const val KEY_APP_THEME = "appTheme"
    const val KEY_APP_MATERIAL_YOU = "materialYou"
    const val KEY_API_ENDPOINTS_URL = "apiEndpointsUrl"
    const val KEY_API_AUTHORIZATION = "apiAuthorization"
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