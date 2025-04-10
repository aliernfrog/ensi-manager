package com.aliernfrog.ensimanager.ui.screen

import android.util.Log
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.aliernfrog.ensimanager.R
import com.aliernfrog.ensimanager.TAG
import com.aliernfrog.ensimanager.ui.component.BaseScaffold
import com.aliernfrog.ensimanager.ui.dialog.api.crypto.DecryptionDialog
import com.aliernfrog.ensimanager.ui.dialog.api.crypto.EncryptionDialog
import com.aliernfrog.ensimanager.ui.screen.settings.SettingsScreen
import com.aliernfrog.ensimanager.ui.sheet.APIProfileSwitchSheet
import com.aliernfrog.ensimanager.ui.sheet.UpdateSheet
import com.aliernfrog.ensimanager.ui.viewmodel.APIViewModel
import com.aliernfrog.ensimanager.ui.viewmodel.MainViewModel
import com.aliernfrog.ensimanager.util.Destination
import com.aliernfrog.ensimanager.util.NavigationConstant
import com.aliernfrog.ensimanager.util.extension.popBackStackSafe
import com.aliernfrog.ensimanager.util.extension.showSuccessToast
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    mainViewModel: MainViewModel = koinViewModel(),
    apiViewModel: APIViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val navController = rememberNavController()
    val onNavigateSettingsRequest = {
        navController.navigate(Destination.SETTINGS.route)
    }
    val onNavigateBackRequest = {
        navController.popBackStackSafe()
    }

    LaunchedEffect(navController) {
        mainViewModel.navController = navController
    }

    BaseScaffold(navController) {
        NavHost(
            navController = navController,
            startDestination = NavigationConstant.INITIAL_DESTINATION,
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .consumeWindowInsets(it)
                .imePadding(),
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
                APIGate(
                    onNavigateSettingsRequest = onNavigateSettingsRequest
                ) {
                    DashboardScreen(
                        onNavigateRequest = { destination ->
                            navController.navigate(destination.route)
                        }
                    )
                }
            }

            composable(Destination.STRINGS.route) {
                APIGate(
                    onNavigateSettingsRequest = onNavigateSettingsRequest
                ) {
                    StringsScreen(
                        onNavigateSettingsRequest = onNavigateSettingsRequest
                    )
                }
            }

            composable(Destination.LOGS.route) {
                APIGate(
                    onNavigateSettingsRequest = onNavigateSettingsRequest
                ) {
                    LogsScreen(
                        onNavigateSettingsRequest = onNavigateSettingsRequest
                    )
                }
            }

            composable(Destination.API_PROFILES.route) {
                APIProfilesScreen(
                    onNavigateSettingsRequest = onNavigateSettingsRequest,
                    onNavigateBackRequest = onNavigateBackRequest
                )
            }

            composable(Destination.SETTINGS.route) {
                SettingsScreen(
                    onNavigateBackRequest = onNavigateBackRequest
                )
            }
        }
    }

    if (apiViewModel.showEncryptionDialog) EncryptionDialog(
        onDismissRequest = { apiViewModel.showEncryptionDialog = false },
        onEncryptRequest = { password, onFinish ->
            scope.launch {
                apiViewModel.changeEncryptionPasswordAndSave(password)
                apiViewModel.showEncryptionDialog = false
                apiViewModel.topToastState.showSuccessToast(R.string.api_crypto_encrypt_encrypted)
                onFinish()
            }
        }
    )

    if (apiViewModel.showDecryptionDialog) DecryptionDialog(
        onDismissRequest = { apiViewModel.showDecryptionDialog = false },
        onDecryptRequest = { password, onFinish ->
            val profiles = apiViewModel.decryptAPIProfiles(password)
            if (profiles != null) {
                apiViewModel.showDecryptionDialog = false
                apiViewModel.topToastState.showSuccessToast(R.string.api_crypto_decrypt_decrypted)
                scope.launch {
                    apiViewModel.refetchAllProfiles()
                }
            }
            onFinish()
        },
        onBiometricUnlockRequest = if (apiViewModel.biometricDecryptionAvailable) { { onFinish ->
            apiViewModel.showBiometricPrompt(
                context = context,
                forDecryption = true,
                onSuccess = { scope.launch {
                    val profiles = apiViewModel.decryptAPIProfilesWithBiometrics(it.cryptoObject?.cipher)
                    if (profiles != null) {
                        apiViewModel.topToastState.showSuccessToast(R.string.api_crypto_decrypt_decrypted)
                        scope.launch {
                            apiViewModel.refetchAllProfiles()
                        }
                    }
                    onFinish()
                } },
                onFail = {
                    onFinish()
                    Log.d(TAG, "MainScreen: biometric decryption prompt failed")
                }
            )
        } } else null
    )

    APIProfileSwitchSheet(
        onNavigateSettingsRequest = onNavigateSettingsRequest,
        onNavigateApiProfilesRequest = {
            navController.navigate(Destination.API_PROFILES.route)
        }
    )

    UpdateSheet(
        sheetState = mainViewModel.updateSheetState,
        latestVersionInfo = mainViewModel.latestVersionInfo,
        updateAvailable = mainViewModel.updateAvailable,
        onCheckUpdatesRequest = { scope.launch {
            mainViewModel.checkUpdates(manuallyTriggered = true)
        } }
    )
}