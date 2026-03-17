package com.aliernfrog.ensimanager.ui.activity

import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.aliernfrog.ensimanager.R
import com.aliernfrog.ensimanager.TAG
import com.aliernfrog.ensimanager.ui.component.MainDestinationContent
import com.aliernfrog.ensimanager.ui.dialog.api.crypto.DecryptionDialog
import com.aliernfrog.ensimanager.ui.dialog.api.crypto.EncryptionDialog
import com.aliernfrog.ensimanager.ui.screen.APIProfilesScreen
import com.aliernfrog.ensimanager.ui.screen.settings.SettingsScreen
import com.aliernfrog.ensimanager.ui.sheet.APIProfileSwitchSheet
import com.aliernfrog.ensimanager.ui.theme.EnsiManagerTheme
import com.aliernfrog.ensimanager.ui.viewmodel.APIViewModel
import com.aliernfrog.ensimanager.ui.viewmodel.MainViewModel
import com.aliernfrog.ensimanager.util.Destination
import com.aliernfrog.ensimanager.util.MainDestinationGroup
import com.aliernfrog.ensimanager.util.extension.removeLastIfMultiple
import com.aliernfrog.ensimanager.util.extension.showSuccessToast
import com.aliernfrog.ensimanager.util.slideTransitionMetadata
import com.aliernfrog.ensimanager.util.slideVerticalTransitionMetadata
import com.aliernfrog.toptoast.component.TopToastHost
import io.github.aliernfrog.shared.ui.component.util.AppContainer
import io.github.aliernfrog.shared.ui.component.util.InsetsObserver
import io.github.aliernfrog.shared.ui.screen.UpdatesScreen
import io.github.aliernfrog.shared.ui.screen.settings.SettingsDestination
import io.github.aliernfrog.shared.ui.theme.Theme
import io.github.aliernfrog.shared.util.LocalSharedString
import io.github.aliernfrog.shared.util.SharedString
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.compose.viewmodel.koinViewModel

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()

        val vm = getViewModel<MainViewModel>()
        val sharedString by inject<SharedString>()

        setContent {
            val view = LocalView.current
            val scope = rememberCoroutineScope()
            val useDarkTheme = shouldUseDarkTheme(vm.prefs.theme.value)
            var isAppInitialized by rememberSaveable { mutableStateOf(false) }

            @Composable
            fun AppTheme(content: @Composable () -> Unit) {
                EnsiManagerTheme(
                    darkTheme = useDarkTheme,
                    dynamicColors = vm.prefs.materialYou.value,
                    pitchBlack = vm.prefs.pitchBlack.value,
                    content = content
                )
            }

            AppTheme {
                CompositionLocalProvider(
                    LocalSharedString provides sharedString
                ) {
                    App(vm)
                }
            }

            LaunchedEffect(Unit) {
                vm.scope = scope
                vm.topToastState.setComposeView(view)
                if (isAppInitialized) return@LaunchedEffect

                vm.topToastState.setAppTheme { AppTheme(it) }

                if (vm.prefs.autoCheckUpdates.value) vm.checkUpdates()
                isAppInitialized = true
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun App(vm: MainViewModel) {
        val apiViewModel = koinViewModel<APIViewModel>() // TODO remove

        val context = LocalContext.current
        val scope = rememberCoroutineScope()
        val applyImePadding = !vm.isAtMainDestination

        val availableUpdates = vm.availableUpdates.collectAsStateWithLifecycle().value
        val currentVersionInfo = vm.currentVersionInfo.collectAsStateWithLifecycle().value
        val isCompatibleWithLatestVersion = vm.isCompatibleWithLatestVersion.collectAsStateWithLifecycle().value
        val isCheckingForUpdates = vm.isCheckingForUpdates.collectAsStateWithLifecycle().value

        val onNavigateBackRequest: () -> Unit = {
            vm.navigationBackStack.removeLastIfMultiple()
        }

        val onNavigateSettingsRequest: () -> Unit = {
            vm.navigationBackStack.add(SettingsDestination.root)
        }

        InsetsObserver()
        AppContainer {
            NavDisplay(
                backStack = vm.navigationBackStack,
                modifier = Modifier
                    .fillMaxSize()
                    .let {
                        // MainDestinationGroup handles imePadding, so skip it here if we are at MainDestinationGroup
                        if (applyImePadding) it.imePadding() else it
                    },
                entryProvider = entryProvider {
                    entry<MainDestinationGroup> { _ ->
                        MainDestinationContent()
                    }

                    entry<Destination.APIProfiles>(
                        metadata = slideTransitionMetadata
                    ) {
                        APIProfilesScreen(
                            onNavigateSettingsRequest = onNavigateSettingsRequest
                        )
                    }

                    entry<Destination.Updates>(
                        metadata = slideVerticalTransitionMetadata
                    ) {
                        UpdatesScreen(
                            availableUpdates = availableUpdates,
                            currentVersionInfo = currentVersionInfo,
                            isCheckingForUpdates = isCheckingForUpdates,
                            isCompatibleWithLatestVersion = isCompatibleWithLatestVersion,
                            onCheckUpdatesRequest = {
                                vm.checkUpdates(manuallyTriggered = true)
                            },
                            onNavigateBackRequest = onNavigateBackRequest
                        )
                    }

                    entry<SettingsDestination>(
                        metadata = slideTransitionMetadata
                    ) { destination ->
                        SettingsScreen(
                            destination = destination,
                            onNavigateBackRequest = onNavigateBackRequest,
                            onNavigateRequest = { vm.navigationBackStack.add(it) },
                            onCheckUpdatesRequest = { skipVersionCheck ->
                                vm.checkUpdates(skipVersionCheck = skipVersionCheck)
                            },
                            onNavigateUpdatesScreenRequest = {
                                vm.navigationBackStack.add(Destination.Updates)
                            }
                        )
                    }
                }
            )

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
                onDecryptRequest = { password, setDecryptingState ->
                    val profiles = apiViewModel.decryptAPIProfiles(password)
                    if (profiles != null) {
                        apiViewModel.showDecryptionDialog = false
                        apiViewModel.topToastState.showSuccessToast(R.string.api_crypto_decrypt_decrypted)
                        scope.launch {
                            apiViewModel.refetchAllProfiles()
                        }
                    }
                    setDecryptingState(false)
                },
                onBiometricUnlockRequest = if (apiViewModel.biometricDecryptionAvailable) { { setDecryptingState ->
                    apiViewModel.showBiometricPrompt(
                        context = context,
                        forDecryption = true,
                        onSuccess = { scope.launch {
                            setDecryptingState(true)
                            val profiles = apiViewModel.decryptAPIProfilesWithBiometrics(it.cryptoObject?.cipher)
                            if (profiles != null) {
                                apiViewModel.showDecryptionDialog = false
                                apiViewModel.topToastState.showSuccessToast(R.string.api_crypto_decrypt_decrypted)
                                scope.launch {
                                    apiViewModel.refetchAllProfiles()
                                }
                            }
                            setDecryptingState(false)
                        } },
                        onFail = {
                            setDecryptingState(false)
                            Log.d(TAG, "MainScreen: biometric decryption prompt failed")
                        }
                    )
                } } else null
            )

            APIProfileSwitchSheet(
                onNavigateSettingsRequest = onNavigateSettingsRequest,
                onNavigateApiProfilesRequest = {
                    vm.navigationBackStack.add(Destination.APIProfiles)
                }
            )
            TopToastHost(vm.topToastState)
        }
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