package com.aliernfrog.ensimanager.ui.screen

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.aliernfrog.ensimanager.ui.component.BaseScaffold
import com.aliernfrog.ensimanager.ui.sheet.UpdateSheet
import com.aliernfrog.ensimanager.util.Destination
import com.aliernfrog.ensimanager.util.NavigationConstant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    BaseScaffold(navController) {
        NavHost(
            navController = navController,
            startDestination = NavigationConstant.INITIAL_DESTINATION,
            modifier = Modifier.fillMaxSize().padding(it).consumeWindowInsets(it).imePadding(),
            enterTransition = {
                scaleIn(
                    animationSpec = tween(delayMillis = 100),
                    initialScale = 0.95f
                ) + fadeIn(
                    animationSpec = tween(delayMillis = 100)
                )
            },
            exitTransition = { fadeOut(tween(100)) },
            popEnterTransition = {
                scaleIn(
                    animationSpec = tween(delayMillis = 100),
                    initialScale = 1.05f
                ) + fadeIn(
                    animationSpec = tween(delayMillis = 100)
                )
            },
            popExitTransition = {
                scaleOut(
                    animationSpec = tween(100),
                    targetScale = 0.95f
                ) + fadeOut(
                    animationSpec = tween(100)
                )
            }
        ) {
            composable(Destination.DASHBOARD.route) {
                APIScreen {
                    DashboardScreen(
                        onNavigateLogsScreenRequest = {
                            navController.navigate(Destination.LOGS.route)
                        }
                    )
                }
            }
            composable(Destination.LOGS.route) {
                LogsScreen(
                    onBackClick = { navController.popBackStack() }
                )
            }
            composable(Destination.CHAT.route) {
                APIScreen {
                    ChatScreen()
                }
            }
            composable(Destination.SETTINGS.route) {
                SettingsScreen(
                    onNavigateAPIConfigScreenRequest = {
                        navController.navigate(Destination.API_CONFIG.route)
                    }
                )
            }
            composable(Destination.API_CONFIG.route) {
                APIConfigurationScreen(
                    onBackClick = { navController.popBackStack() }
                )
            }
        }
    }

    UpdateSheet()
}