package com.aliernfrog.ensimanager

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.aliernfrog.ensimanager.data.ApiRouteOption
import com.aliernfrog.ensimanager.data.Screen

val ManagerRoundessSize = 30.dp
val ManagerComposableShape = RoundedCornerShape(ManagerRoundessSize)

object ApiRoutes {
    val routes = listOf(
        ApiRouteOption("Authorization", "Will be used in headers.authorization", ConfigKey.KEY_API_AUTHORIZATION),
        ApiRouteOption("Get words", "Should return an array of strings", ConfigKey.KEY_API_WORDS_GET),
        ApiRouteOption("Get verbs", "Should return an array of strings", ConfigKey.KEY_API_VERBS_GET)
    )
}

object ConfigKey {
    const val PREF_NAME = "APP_CONFIG"
    const val KEY_APP_THEME = "appTheme"
    const val KEY_APP_MATERIAL_YOU = "materialYou"
    const val KEY_API_AUTHORIZATION = "apiAuthorization"
    const val KEY_API_WORDS_GET = "apiWordsGet"
    const val KEY_API_VERBS_GET = "apiVerbsGet"
}

object NavRoutes {
    const val ENSI = "ensi"
    const val OPTIONS = "options"
}

@Composable
fun getScreens(): List<Screen> {
    val context = LocalContext.current
    return listOf(
        Screen(NavRoutes.ENSI, context.getString(R.string.screen_ensi), painterResource(R.drawable.speech), true),
        Screen(NavRoutes.OPTIONS, context.getString(R.string.screen_options), painterResource(R.drawable.options), true)
    )
}

object Theme {
    const val SYSTEM = 0
    const val LIGHT = 1
    const val DARK = 2
}

object EnsiScreenType {
    const val WORDS = 0
    const val VERBS = 1
}

object EnsiFetchingState {
    const val FETCHING = 0
    const val DONE = 1
}