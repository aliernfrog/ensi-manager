package com.aliernfrog.ensimanager.ui.screen.settings

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Science
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aliernfrog.ensimanager.R
import com.aliernfrog.ensimanager.SettingsConstant.credits
import com.aliernfrog.ensimanager.SettingsConstant.socials
import com.aliernfrog.ensimanager.SettingsConstant.supportLinks
import com.aliernfrog.ensimanager.ui.component.widget.settings.ExperimentalOptions
import com.aliernfrog.ensimanager.ui.viewmodel.SettingsViewModel
import com.aliernfrog.ensimanager.util.AppSettingsDestination
import com.aliernfrog.ensimanager.util.staticutil.GeneralUtil
import io.github.aliernfrog.shared.ui.screen.settings.AboutPage
import io.github.aliernfrog.shared.ui.screen.settings.AppearancePage
import io.github.aliernfrog.shared.ui.screen.settings.ExperimentalPage
import io.github.aliernfrog.shared.ui.screen.settings.LibsPage
import io.github.aliernfrog.shared.ui.screen.settings.SettingsDestination
import io.github.aliernfrog.shared.ui.screen.settings.SettingsRootPage
import io.github.aliernfrog.shared.util.resolve
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    destination: SettingsDestination,
    vm: SettingsViewModel = koinViewModel(),
    onCheckUpdatesRequest: (skipVersionCheck: Boolean) -> Unit,
    onNavigateUpdatesScreenRequest: () -> Unit,
    onNavigateBackRequest: () -> Unit,
    onNavigateRequest: (SettingsDestination) -> Unit
) {
    val context = LocalContext.current
    val availableUpdates = vm.versionManager.availableUpdates.collectAsStateWithLifecycle().value

    when (destination) {
        SettingsDestination.root -> {
            SettingsRootPage(
                categories = vm.categories,
                availableUpdates = availableUpdates,
                experimentalOptionsEnabled = vm.prefs.experimentalOptionsEnabled.value,
                onShowUpdateSheetRequest = onNavigateUpdatesScreenRequest,
                onNavigateBackRequest = onNavigateBackRequest,
                onNavigateRequest = onNavigateRequest
            )
        }

        AppSettingsDestination.api -> {
            APIPage(
                onNavigateBackRequest = onNavigateBackRequest
            )
        }

        AppSettingsDestination.security -> {
            SecurityPage(
                onNavigateBackRequest = onNavigateBackRequest
            )
        }

        SettingsDestination.appearance -> {
            AppearancePage(
                themePref = vm.prefs.theme,
                materialYouPref = vm.prefs.materialYou,
                pitchBlackPref = vm.prefs.pitchBlack,
                onNavigateBackRequest = onNavigateBackRequest
            )
        }

        SettingsDestination.experimental -> {
            ExperimentalPage(
                experimentalPrefs = vm.prefs.experimentalPrefs,
                experimentalOptionsEnabledPref = vm.prefs.experimentalOptionsEnabled,
                onCheckUpdatesRequest = onCheckUpdatesRequest,
                onNavigateUpdatesScreenRequest = onNavigateUpdatesScreenRequest,
                onRestartAppRequest = { GeneralUtil.restartApp(context, withModules = true) },
                onNavigateBackRequest = onNavigateBackRequest
            ) {
                ExperimentalOptions()
            }
        }

        SettingsDestination.about -> {
            AboutPage(
                socials = socials,
                credits = credits,
                supportLinks = supportLinks,
                debugInfo = vm.debugInfo,
                autoCheckUpdatesPref = vm.prefs.autoCheckUpdates,
                experimentalOptionsEnabled = vm.prefs.experimentalOptionsEnabled.value,
                onExperimentalOptionsEnabled = {
                    vm.prefs.experimentalOptionsEnabled.value = true
                    vm.topToastState.showToast(
                        text = "Experimental options enabled",
                        icon = Icons.Rounded.Science
                    )
                },
                onShowUpdateSheetRequest = onNavigateUpdatesScreenRequest,
                onNavigateLibsRequest = { onNavigateRequest(SettingsDestination.libs) },
                onNavigateBackRequest = onNavigateBackRequest
            )
        }

        SettingsDestination.libs -> {
            LibsPage(
                librariesJSONRes = R.raw.aboutlibraries,
                onNavigateBackRequest = onNavigateBackRequest
            )
        }

        else -> {
            Text("UNKNOWN DESTINATION: ${destination.title.resolve()}")
        }
    }
}