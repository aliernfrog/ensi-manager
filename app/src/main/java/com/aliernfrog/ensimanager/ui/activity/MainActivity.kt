package com.aliernfrog.ensimanager.ui.activity

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalView
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.aliernfrog.ensimanager.ui.component.InsetsObserver
import com.aliernfrog.ensimanager.ui.screen.MainScreen
import com.aliernfrog.ensimanager.ui.theme.EnsiManagerTheme
import com.aliernfrog.ensimanager.ui.theme.Theme
import com.aliernfrog.ensimanager.ui.viewmodel.MainViewModel
import com.aliernfrog.toptoast.component.TopToastHost
import org.koin.androidx.compose.koinViewModel

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()

        setContent {
            AppContent()
        }
    }

    @Composable
    private fun AppContent(
        mainViewModel: MainViewModel = koinViewModel()
    ) {
        val view = LocalView.current
        val scope = rememberCoroutineScope()
        val useDarkTheme = shouldUseDarkTheme(mainViewModel.prefs.theme.value)
        var isAppInitialized by rememberSaveable { mutableStateOf(false) }

        @Composable
        fun AppTheme(content: @Composable () -> Unit) {
            EnsiManagerTheme(
                darkTheme = useDarkTheme,
                dynamicColors = mainViewModel.prefs.materialYou.value,
                pitchBlack = mainViewModel.prefs.pitchBlack.value,
                content = content
            )
        }

        AppTheme {
            InsetsObserver()
            AppContainer {
                MainScreen()
                TopToastHost(mainViewModel.topToastState)
            }
        }

        LaunchedEffect(Unit) {
            mainViewModel.scope = scope
            mainViewModel.topToastState.setComposeView(view)
            if (isAppInitialized) return@LaunchedEffect

            mainViewModel.topToastState.setAppTheme { AppTheme(it) }

            if (mainViewModel.prefs.autoCheckUpdates.value) mainViewModel.checkUpdates()
            isAppInitialized = true
        }
    }

    @Composable
    private fun AppContainer(
        content: @Composable BoxScope.() -> Unit
    ) {
        val config = LocalConfiguration.current
        val density = LocalDensity.current
        val layoutDirection = LocalLayoutDirection.current
        val navbarInsets = WindowInsets.navigationBars
        val navbarOnLeft = navbarInsets.getLeft(density, layoutDirection) > 0
        val navbarOnRight = navbarInsets.getRight(density, layoutDirection) > 0

        Box(
            content = content,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .let {
                    var modifier = it
                    if (config.orientation == Configuration.ORIENTATION_LANDSCAPE) modifier = modifier.displayCutoutPadding()
                    if (navbarOnLeft || navbarOnRight) modifier = modifier.navigationBarsPadding()
                    modifier
                }
        )
    }

    @Composable
    private fun shouldUseDarkTheme(theme: Int): Boolean {
        return when(theme) {
            Theme.LIGHT.ordinal -> false
            Theme.DARK.ordinal -> true
            else -> isSystemInDarkTheme()
        }
    }
}