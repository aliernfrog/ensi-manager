package com.aliernfrog.ensimanager

import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.aliernfrog.ensimanager.state.EnsiState
import com.aliernfrog.ensimanager.state.OptionsState
import com.aliernfrog.ensimanager.ui.composable.ManagerBaseScaffold
import com.aliernfrog.ensimanager.ui.screen.EnsiScreen
import com.aliernfrog.ensimanager.ui.screen.OptionsScreen
import com.aliernfrog.ensimanager.ui.sheet.WordSheet
import com.aliernfrog.ensimanager.ui.theme.EnsiManagerTheme
import com.aliernfrog.toptoast.TopToastBase
import com.aliernfrog.toptoast.TopToastManager
import com.google.accompanist.systemuicontroller.rememberSystemUiController

class MainActivity : ComponentActivity() {
    private lateinit var config: SharedPreferences
    private lateinit var topToastManager: TopToastManager
    private lateinit var optionsState: OptionsState
    private lateinit var ensiState: EnsiState

    @OptIn(ExperimentalMaterialApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        config = getSharedPreferences(ConfigKey.PREF_NAME, MODE_PRIVATE)
        topToastManager = TopToastManager()
        optionsState = OptionsState(config, ScrollState(0))
        ensiState = EnsiState(config, topToastManager, LazyListState())
        setContent {
            val darkTheme = getDarkThemePreference()
            EnsiManagerTheme(darkTheme, optionsState.materialYou.value) {
                TopToastBase(backgroundColor = MaterialTheme.colorScheme.background, topToastManager) {
                    BaseScaffold()
                    WordSheet(ensiState, state = ensiState.wordSheetState)
                }
                SystemBars(darkTheme)
            }
        }
    }

    @Composable
    private fun BaseScaffold() {
        val navController = rememberNavController()
        ManagerBaseScaffold(navController) {
            NavHost(
                navController = navController,
                startDestination = NavRoutes.ENSI,
                modifier = Modifier.fillMaxSize().padding(it)
            ) {
                composable(route = NavRoutes.ENSI) {
                    EnsiScreen(ensiState)
                }
                composable(route = NavRoutes.OPTIONS) {
                    OptionsScreen(optionsState)
                }
            }
        }
    }

    @Composable
    private fun SystemBars(darkTheme: Boolean) {
        val controller = rememberSystemUiController()
        controller.statusBarDarkContentEnabled = !darkTheme
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