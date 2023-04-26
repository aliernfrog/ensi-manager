package com.aliernfrog.ensimanager

import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.aliernfrog.ensimanager.state.*
import com.aliernfrog.ensimanager.ui.component.BaseScaffold
import com.aliernfrog.ensimanager.ui.screen.APISetupScreen
import com.aliernfrog.ensimanager.ui.screen.ChatScreen
import com.aliernfrog.ensimanager.ui.screen.DashboardScreen
import com.aliernfrog.ensimanager.ui.screen.SettingsScreen
import com.aliernfrog.ensimanager.ui.sheet.AddWordSheet
import com.aliernfrog.ensimanager.ui.sheet.UpdateSheet
import com.aliernfrog.ensimanager.ui.sheet.WordSheet
import com.aliernfrog.ensimanager.ui.theme.EnsiManagerTheme
import com.aliernfrog.ensimanager.util.Destination
import com.aliernfrog.ensimanager.util.NavigationConstant
import com.aliernfrog.ensimanager.util.getScreens
import com.aliernfrog.toptoast.component.TopToastHost
import com.aliernfrog.toptoast.state.TopToastState
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController

class MainActivity : ComponentActivity() {
    private lateinit var config: SharedPreferences
    private lateinit var navController: NavHostController
    private lateinit var topToastState: TopToastState
    private lateinit var settingsState: SettingsState
    private lateinit var updateState: UpdateState
    private lateinit var apiState: EnsiAPIState
    private lateinit var chatState: ChatState
    private lateinit var dashboardState: DashboardState

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        config = getSharedPreferences(ConfigKey.PREF_NAME, MODE_PRIVATE)
        navController = NavHostController(applicationContext)
        topToastState = TopToastState(window.decorView)
        settingsState = SettingsState(topToastState, config)
        updateState = UpdateState(topToastState, config, applicationContext)
        apiState = EnsiAPIState(config, topToastState) { navController }
        chatState = ChatState(topToastState, apiState, LazyListState())
        dashboardState = DashboardState(topToastState, apiState)
        setContent {
            val darkTheme = getDarkThemePreference()
            val scope = rememberCoroutineScope()
            EnsiManagerTheme(darkTheme, settingsState.materialYou) {
                BaseScaffold()
                TopToastHost(topToastState)
            }
            LaunchedEffect(Unit) {
                updateState.setScope(scope)
            }
        }
    }

    @OptIn(ExperimentalMaterialApi::class, ExperimentalAnimationApi::class, ExperimentalLayoutApi::class)
    @Composable
    private fun BaseScaffold() {
        val screens = getScreens()
        navController = rememberAnimatedNavController()
        BaseScaffold(screens, navController) {
            AnimatedNavHost(
                navController = navController,
                startDestination = NavigationConstant.INITIAL_DESTINATION,
                modifier = Modifier.fillMaxSize().padding(it).consumeWindowInsets(it).imePadding(),
                enterTransition = { scaleIn(
                    animationSpec = tween(delayMillis = 100),
                    initialScale = 0.95f
                ) + fadeIn(
                    animationSpec = tween(delayMillis = 100)
                ) },
                exitTransition = { fadeOut(tween(100)) },
                popEnterTransition = { scaleIn(
                    animationSpec = tween(delayMillis = 100),
                    initialScale = 1.05f
                ) + fadeIn(
                    animationSpec = tween(delayMillis = 100)
                ) },
                popExitTransition = { scaleOut(
                    animationSpec = tween(100),
                    targetScale = 0.95f
                ) + fadeOut(
                    animationSpec = tween(100)
                ) }
            ) {
                composable(Destination.SETUP.route) { APISetupScreen(apiState, navController) }
                composable(Destination.CHAT.route) { ChatScreen(chatState) }
                composable(Destination.DASHBOARD.route) { DashboardScreen(dashboardState) }
                composable(Destination.SETTINGS.route) { SettingsScreen(settingsState, updateState, navController) }
                composable(Destination.SETTINGS_SUBSCREEN.route) {
                    SettingsScreen(settingsState, updateState, navController, showApiOptions = false) {
                        navController.popBackStack()
                    }
                }
            }
        }
        AddWordSheet(chatState, state = chatState.addWordSheetState)
        WordSheet(chatState, state = chatState.wordSheetState)
        UpdateSheet(updateState)
    }

    @Composable
    private fun getDarkThemePreference(): Boolean {
        return when(settingsState.theme) {
            Theme.LIGHT -> false
            Theme.DARK -> true
            else -> isSystemInDarkTheme()
        }
    }
}