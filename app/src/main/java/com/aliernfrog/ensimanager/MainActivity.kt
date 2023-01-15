package com.aliernfrog.ensimanager

import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.aliernfrog.ensimanager.state.ChatState
import com.aliernfrog.ensimanager.state.DashboardState
import com.aliernfrog.ensimanager.state.OptionsState
import com.aliernfrog.ensimanager.ui.composable.ManagerBaseScaffold
import com.aliernfrog.ensimanager.ui.screen.ChatScreen
import com.aliernfrog.ensimanager.ui.screen.DashboardScreen
import com.aliernfrog.ensimanager.ui.screen.OptionsScreen
import com.aliernfrog.ensimanager.ui.sheet.AddWordSheet
import com.aliernfrog.ensimanager.ui.sheet.WordSheet
import com.aliernfrog.ensimanager.ui.theme.EnsiManagerTheme
import com.aliernfrog.toptoast.component.TopToastHost
import com.aliernfrog.toptoast.state.TopToastState
import com.google.accompanist.systemuicontroller.rememberSystemUiController

class MainActivity : ComponentActivity() {
    private lateinit var config: SharedPreferences
    private lateinit var topToastState: TopToastState
    private lateinit var optionsState: OptionsState
    private lateinit var chatState: ChatState
    private lateinit var dashboardState: DashboardState

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        config = getSharedPreferences(ConfigKey.PREF_NAME, MODE_PRIVATE)
        topToastState = TopToastState()
        optionsState = OptionsState(config, ScrollState(0))
        chatState = ChatState(config, topToastState, LazyListState())
        dashboardState = DashboardState(config, topToastState)
        setContent {
            val darkTheme = getDarkThemePreference()
            EnsiManagerTheme(darkTheme, optionsState.materialYou.value) {
                BaseScaffold()
                TopToastHost(topToastState)
                SystemBars(darkTheme)
            }
        }
    }

    @OptIn(ExperimentalLayoutApi::class, ExperimentalMaterialApi::class)
    @Composable
    private fun BaseScaffold() {
        val navController = rememberNavController()
        ManagerBaseScaffold(navController) {
            NavHost(
                navController = navController,
                startDestination = NavRoutes.DASHBOARD,
                modifier = Modifier.fillMaxSize().padding(it).consumeWindowInsets(it).systemBarsPadding()
            ) {
                composable(route = NavRoutes.CHAT) {
                    ChatScreen(chatState)
                }
                composable(route = NavRoutes.DASHBOARD) {
                    DashboardScreen(dashboardState)
                }
                composable(route = NavRoutes.OPTIONS) {
                    OptionsScreen(optionsState)
                }
            }
        }
        AddWordSheet(chatState, state = chatState.addWordSheetState)
        WordSheet(chatState, state = chatState.wordSheetState)
    }

    @Composable
    private fun SystemBars(darkTheme: Boolean) {
        val controller = rememberSystemUiController()
        controller.systemBarsDarkContentEnabled = !darkTheme
        controller.isNavigationBarContrastEnforced = false
    }

    @Composable
    private fun getDarkThemePreference(): Boolean {
        return when(optionsState.theme.value) {
            Theme.LIGHT -> false
            Theme.DARK -> true
            else -> isSystemInDarkTheme()
        }
    }
}