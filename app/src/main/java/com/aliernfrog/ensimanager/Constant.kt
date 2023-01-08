package com.aliernfrog.ensimanager

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Chat
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.aliernfrog.ensimanager.data.ApiRouteOption
import com.aliernfrog.ensimanager.data.Screen

val ManagerRoundessSize = 30.dp
val ManagerComposableShape = RoundedCornerShape(ManagerRoundessSize)

object ApiRoutes {
    val options = listOf(
        ApiRouteOption("Authorization", "Will be used in headers.authorization", ConfigKey.KEY_API_AUTHORIZATION),
        ApiRouteOption("Get status", "Should return information about status of API", ConfigKey.KEY_API_STATUS_GET),
        ApiRouteOption("Destroy process", "Should destroy process after responding", ConfigKey.KEY_API_PROCESS_DESTROY),
        ApiRouteOption("Get words", "Should return an array of strings", ConfigKey.KEY_API_WORDS_GET),
        ApiRouteOption("Add words", "Should add the word specificed in body.word", ConfigKey.KEY_API_WORDS_ADD),
        ApiRouteOption("Delete words", "Should delete the word specified in body.word", ConfigKey.KEY_API_WORDS_DELETE),
        ApiRouteOption("Get verbs", "Should return an array of strings", ConfigKey.KEY_API_VERBS_GET),
        ApiRouteOption("Add verbs", "Should add the verb specificed in body.verb", ConfigKey.KEY_API_VERBS_ADD),
        ApiRouteOption("Delete verbs", "Should delete the word specified in body.verb", ConfigKey.KEY_API_VERBS_DELETE),
        ApiRouteOption("Post Ensicord add-on", "Should generate and post Ensicord add-on", ConfigKey.KEY_API_POST_ADDON)
    )
}

object ConfigKey {
    const val PREF_NAME = "APP_CONFIG"
    const val KEY_APP_THEME = "appTheme"
    const val KEY_APP_MATERIAL_YOU = "materialYou"
    const val KEY_API_AUTHORIZATION = "apiAuthorization"
    const val KEY_API_STATUS_GET = "apiStatusGet"
    const val KEY_API_PROCESS_DESTROY = "apiProcessDestroy"
    const val KEY_API_WORDS_GET = "apiWordsGet"
    const val KEY_API_WORDS_ADD = "apiWordsAdd"
    const val KEY_API_WORDS_DELETE = "apiWordsDelete"
    const val KEY_API_VERBS_GET = "apiVerbsGet"
    const val KEY_API_VERBS_ADD = "apiVerbsAdd"
    const val KEY_API_VERBS_DELETE = "apiVerbsDelete"
    const val KEY_API_POST_ADDON = "apiPostAddon"
}

object NavRoutes {
    const val CHAT = "chat"
    const val DASHBOARD = "dashboard"
    const val OPTIONS = "options"
}

@Composable
fun getScreens(): List<Screen> {
    val context = LocalContext.current
    return listOf(
        Screen(NavRoutes.CHAT, context.getString(R.string.screen_chat), rememberVectorPainter(Icons.Default.Chat), rememberVectorPainter(Icons.Outlined.Chat), true),
        Screen(NavRoutes.DASHBOARD, context.getString(R.string.screen_dashboard), rememberVectorPainter(Icons.Default.Dashboard), rememberVectorPainter(Icons.Outlined.Dashboard), true),
        Screen(NavRoutes.OPTIONS, context.getString(R.string.screen_options), rememberVectorPainter(Icons.Default.Settings), rememberVectorPainter(Icons.Outlined.Settings), true)
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