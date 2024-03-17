package com.aliernfrog.ensimanager.ui.activity

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalView
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.aliernfrog.ensimanager.ui.component.InsetsObserver
import com.aliernfrog.ensimanager.ui.screen.MainScreen
import com.aliernfrog.ensimanager.ui.theme.EnsiManagerTheme
import com.aliernfrog.ensimanager.ui.theme.Theme
import com.aliernfrog.ensimanager.ui.viewmodel.APIViewModel
import com.aliernfrog.ensimanager.ui.viewmodel.MainViewModel
import com.aliernfrog.toptoast.component.TopToastHost
import org.koin.android.ext.android.inject
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {
    @Volatile
    private var isAppReady = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val splashScreen = installSplashScreen()
        initializeApi()
        splashScreen.setKeepOnScreenCondition { !isAppReady }

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

        @Composable
        fun AppTheme(content: @Composable () -> Unit) {
            EnsiManagerTheme(
                darkTheme = isDarkThemeEnabled(mainViewModel.prefs.theme),
                dynamicColors = mainViewModel.prefs.materialYou,
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
            mainViewModel.topToastState.setAppTheme { AppTheme(it) }

            if (mainViewModel.prefs.autoCheckUpdates) mainViewModel.checkUpdates()
        }
    }

    @Composable
    private fun AppContainer(
        content: @Composable BoxScope.() -> Unit
    ) {
        val config = LocalConfiguration.current
        var modifier = Modifier.background(MaterialTheme.colorScheme.surface)
        if (config.orientation == Configuration.ORIENTATION_LANDSCAPE)
            modifier = modifier
                .displayCutoutPadding()
                .navigationBarsPadding()

        Box(
            modifier = modifier,
            content = content
        )
    }

    @Composable
    private fun isDarkThemeEnabled(theme: Int): Boolean {
        return when(theme) {
            Theme.LIGHT.ordinal -> false
            Theme.DARK.ordinal -> true
            else -> isSystemInDarkTheme()
        }
    }

    private fun initializeApi() {
        val apiViewModel by inject<APIViewModel>()

        apiViewModel.doInitialConnection {
            isAppReady = true
        }
    }
}