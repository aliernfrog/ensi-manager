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
import com.aliernfrog.ensimanager.SettingsConstant.supportLinks
import com.aliernfrog.ensimanager.TAG
import com.aliernfrog.ensimanager.crashReportURL
import com.aliernfrog.ensimanager.ui.component.MainDestinationContent
import com.aliernfrog.ensimanager.ui.dialog.api.crypto.DecryptionDialog
import com.aliernfrog.ensimanager.ui.dialog.api.crypto.EncryptionDialog
import com.aliernfrog.ensimanager.ui.screen.profiles.ProfileScreen
import com.aliernfrog.ensimanager.ui.screen.profiles.ProfilesScreen
import com.aliernfrog.ensimanager.ui.screen.settings.SettingsScreen
import com.aliernfrog.ensimanager.ui.sheet.ProfileSwitchSheet
import com.aliernfrog.ensimanager.ui.theme.EnsiManagerTheme
import com.aliernfrog.ensimanager.ui.viewmodel.MainViewModel
import com.aliernfrog.ensimanager.util.Destination
import com.aliernfrog.ensimanager.util.MainDestinationGroup
import com.aliernfrog.ensimanager.util.extension.showSuccessToast
import com.aliernfrog.ensimanager.util.slideTransitionMetadata
import com.aliernfrog.ensimanager.util.slideVerticalTransitionMetadata
import com.aliernfrog.toptoast.component.TopToastHost
import io.github.aliernfrog.shared.ui.component.util.AppContainer
import io.github.aliernfrog.shared.ui.component.util.InsetsObserver
import io.github.aliernfrog.shared.ui.screen.UpdatesScreen
import io.github.aliernfrog.shared.ui.screen.settings.SettingsDestination
import io.github.aliernfrog.shared.ui.sheet.CrashDetailsSheet
import io.github.aliernfrog.shared.ui.theme.Theme
import io.github.aliernfrog.shared.util.LocalSharedString
import io.github.aliernfrog.shared.util.SharedString
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.compose.koinViewModel
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()

        val vm = getViewModel<MainViewModel>()
        val sharedString by inject<SharedString>()

        setContent {
            val view = LocalView.current
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
        val context = LocalContext.current
        val scope = rememberCoroutineScope()

        // MainDestinationGroup handles imePadding differently, so do not apply it here
        val applyImePadding = !vm.appState.navController.isAtMainDestination

        val availableUpdates = vm.availableUpdates.collectAsStateWithLifecycle().value
        val currentVersionInfo = vm.currentVersionInfo.collectAsStateWithLifecycle().value
        val isCompatibleWithLatestVersion = vm.isCompatibleWithLatestVersion.collectAsStateWithLifecycle().value
        val isCheckingForUpdates = vm.isCheckingForUpdates.collectAsStateWithLifecycle().value

        val onNavigateBackRequest: () -> Unit = {
            vm.appState.navController.removeLastIfMultiple()
        }

        val onNavigateSettingsRequest: () -> Unit = {
            vm.appState.navController.add(SettingsDestination.root)
        }

        fun getUniqueNavKey() = System.nanoTime().toString()

        InsetsObserver()
        AppContainer {
            NavDisplay(
                backStack = vm.appState.navController.backStack,
                modifier = Modifier
                    .fillMaxSize()
                    .let {
                        if (applyImePadding) it.imePadding() else it
                    },
                entryProvider = entryProvider {
                    entry<MainDestinationGroup> { _ ->
                        MainDestinationContent(vm)
                    }

                    entry<Destination.Profiles>(
                        metadata = slideTransitionMetadata
                    ) { destination ->
                        val isFirst = vm.appState.navController.backStack.firstOrNull() == destination
                        ProfilesScreen(
                            isInitialScreen = isFirst,
                            onNavigateSettingsRequest = onNavigateSettingsRequest,
                            onNavigateBackRequest = if (isFirst) null else onNavigateBackRequest
                        )
                    }

                    entry<Destination.Profile>(
                        metadata = slideTransitionMetadata
                    ) { destination ->
                        ProfileScreen(
                            vm = koinViewModel(
                                key = rememberSaveable { getUniqueNavKey() }
                            ) {
                                parametersOf(destination.data)
                            },
                            onNavigateBackRequest = onNavigateBackRequest
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
                            onNavigateRequest = {
                                vm.appState.navController.add(it)
                            },
                            onCheckUpdatesRequest = { skipVersionCheck ->
                                vm.checkUpdates(skipVersionCheck = skipVersionCheck)
                            },
                            onNavigateUpdatesScreenRequest = {
                                vm.appState.navController.add(Destination.Updates)
                            }
                        )
                    }
                }
            )

            if (vm.apiState.showEncryptionDialog) EncryptionDialog(
                onDismissRequest = { vm.apiState.showEncryptionDialog = false },
                onEncryptRequest = { password, onFinish ->
                    scope.launch {
                        vm.apiState.changeEncryptionPasswordAndSave(password)
                        vm.apiState.showEncryptionDialog = false
                        vm.topToastState.showSuccessToast(R.string.api_crypto_encrypt_encrypted)
                        onFinish()
                    }
                }
            )

            if (vm.apiState.showDecryptionDialog) DecryptionDialog(
                onDismissRequest = { vm.apiState.showDecryptionDialog = false },
                onDecryptRequest = { password, setDecryptingState ->
                    val profiles = vm.apiState.decryptDataWithPassword(password)
                    if (profiles != null) {
                        vm.apiState.showDecryptionDialog = false
                        vm.topToastState.showSuccessToast(R.string.api_crypto_decrypt_decrypted)
                        scope.launch {
                            vm.apiState.refetchAllProfiles()
                        }
                    }
                    setDecryptingState(false)
                },
                onBiometricUnlockRequest = if (vm.apiState.canDecryptWithBiometrics) { { setDecryptingState ->
                    vm.apiState.showBiometricPrompt(
                        context = context,
                        forDecryption = true,
                        onSuccess = { scope.launch {
                            setDecryptingState(true)
                            val profiles = vm.apiState.decryptDataWithBiometrics(it.cryptoObject?.cipher)
                            if (profiles != null) {
                                vm.apiState.showDecryptionDialog = false
                                vm.topToastState.showSuccessToast(R.string.api_crypto_decrypt_decrypted)
                                scope.launch {
                                    vm.apiState.refetchAllProfiles()
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

            ProfileSwitchSheet(
                sheetState = vm.apiState.profileSwitcherSheetState,
                onNavigateSettingsRequest = onNavigateSettingsRequest,
                onNavigateApiProfilesRequest = {
                    vm.appState.navController.add(Destination.Profiles)
                }
            )

            CrashDetailsSheet(
                throwable = vm.lastCaughtException,
                crashReportURL = crashReportURL,
                debugInfo = vm.versionManager.getDebugInfo(),
                supportLinks = supportLinks
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