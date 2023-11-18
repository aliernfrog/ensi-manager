package com.aliernfrog.ensimanager.ui.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalView
import com.aliernfrog.ensimanager.ui.component.InsetsObserver
import com.aliernfrog.ensimanager.ui.screen.MainScreen
import com.aliernfrog.ensimanager.ui.theme.EnsiManagerTheme
import com.aliernfrog.ensimanager.ui.theme.Theme
import com.aliernfrog.ensimanager.ui.viewmodel.MainViewModel
import com.aliernfrog.toptoast.component.TopToastHost
import org.koin.androidx.viewmodel.ext.android.getViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AppContent()
        }
    }

    @Composable
    private fun AppContent(
        mainViewModel: MainViewModel = getViewModel()
    ) {
        val view = LocalView.current
        val scope = rememberCoroutineScope()
        val darkTheme = isDarkThemeEnabled(mainViewModel.prefs.theme)
        EnsiManagerTheme(
            darkTheme = darkTheme,
            dynamicColors = mainViewModel.prefs.materialYou
        ) {
            InsetsObserver()
            MainScreen()
            TopToastHost(mainViewModel.topToastState)
        }
        LaunchedEffect(Unit) {
            mainViewModel.scope = scope
            mainViewModel.topToastState.setComposeView(view)

            if (mainViewModel.prefs.autoCheckUpdates) mainViewModel.checkUpdates()
        }
    }

    @Composable
    private fun isDarkThemeEnabled(theme: Int): Boolean {
        return when(theme) {
            Theme.LIGHT.int -> false
            Theme.DARK.int -> true
            else -> isSystemInDarkTheme()
        }
    }
}