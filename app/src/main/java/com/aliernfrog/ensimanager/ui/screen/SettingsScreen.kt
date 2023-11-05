package com.aliernfrog.ensimanager.ui.screen

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material.icons.rounded.Public
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.aliernfrog.ensimanager.R
import com.aliernfrog.ensimanager.SettingsConstant
import com.aliernfrog.ensimanager.ui.component.*
import com.aliernfrog.ensimanager.ui.viewmodel.MainViewModel
import com.aliernfrog.ensimanager.ui.viewmodel.SettingsViewModel
import com.aliernfrog.ensimanager.util.staticutil.GeneralUtil
import com.aliernfrog.toptoast.enum.TopToastType
import kotlinx.coroutines.launch
import org.koin.androidx.compose.getViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    settingsViewModel: SettingsViewModel = getViewModel(),
    onNavigateAPIConfigScreenRequest: () -> Unit
) {
    AppScaffold(
        title = stringResource(R.string.settings),
        topAppBarState = settingsViewModel.topAppBarState
    ) {
        Column(Modifier.fillMaxSize().verticalScroll(settingsViewModel.scrollState)) {
            AppearanceOptions()
            APIOptions(onNavigateAPIConfigScreenRequest = onNavigateAPIConfigScreenRequest)
            AboutApp()
            if (settingsViewModel.experimentalSettingsShown) ExperimentalSettings()
            Spacer(Modifier.navigationBarsPadding())
        }
    }
}

@Composable
private fun AppearanceOptions(
    settingsViewModel: SettingsViewModel = getViewModel()
) {
    val themeOptions = listOf(
        stringResource(R.string.settings_appearance_theme_system),
        stringResource(R.string.settings_appearance_theme_light),
        stringResource(R.string.settings_appearance_theme_dark)
    )
    ColumnDivider(title = stringResource(R.string.settings_appearance)) {
        ButtonShapeless(
            title = stringResource(R.string.settings_appearance_theme),
            description = stringResource(R.string.settings_appearance_theme_description),
            expanded = settingsViewModel.themeOptionsExpanded
        ) {
            settingsViewModel.themeOptionsExpanded = !settingsViewModel.themeOptionsExpanded
        }
        AnimatedVisibility(
            visible = settingsViewModel.themeOptionsExpanded,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            ColumnRounded(Modifier.padding(horizontal = 8.dp)) {
                RadioButtons(
                    options = themeOptions,
                    initialIndex = settingsViewModel.prefs.theme,
                    optionsRounded = true
                ) {
                    settingsViewModel.prefs.theme = it
                }
            }
        }
        if (settingsViewModel.showMaterialYouOption) Switch(
            title = stringResource(R.string.settings_appearance_materialYou),
            description = stringResource(R.string.settings_appearance_materialYou_description),
            checked = settingsViewModel.prefs.materialYou
        ) {
            settingsViewModel.prefs.materialYou
        }
    }
}

@Composable
private fun APIOptions(
    onNavigateAPIConfigScreenRequest: () -> Unit
) {
    ColumnDivider(title = stringResource(R.string.settings_api)) {
        ButtonShapeless(
            title = stringResource(R.string.settings_api_config),
            description = stringResource(R.string.settings_api_config_description),
            expanded = false,
            arrowRotation = 90f
        ) {
            onNavigateAPIConfigScreenRequest()
        }
    }
}

@Composable
private fun AboutApp(
    mainViewModel: MainViewModel = getViewModel(),
    settingsViewModel: SettingsViewModel = getViewModel()
) {
    val scope = rememberCoroutineScope()
    val version = "${mainViewModel.applicationVersionName} (${mainViewModel.applicationVersionCode})"
    ColumnDivider(title = stringResource(R.string.settings_about), bottomDivider = false) {
        ButtonWithComponent(
            title = stringResource(R.string.settings_about_version),
            description = version,
            component = {
                OutlinedButton(
                    onClick = { scope.launch {
                        mainViewModel.checkUpdates(manuallyTriggered = true)
                    } }
                ) {
                    Text(stringResource(R.string.settings_about_checkUpdates))
                }
            }
        ) {
            settingsViewModel.onAboutClick()
        }
        Switch(
            title = stringResource(R.string.settings_about_autoCheckUpdates),
            description = stringResource(R.string.settings_about_autoCheckUpdates_description),
            checked = settingsViewModel.prefs.autoCheckUpdates
        ) {
            settingsViewModel.prefs.autoCheckUpdates = it
        }
        Links(
            expanded = settingsViewModel.linksExpanded,
            onExpandedChange = { settingsViewModel.linksExpanded = it }
        )
    }
}

@Composable
private fun Links(
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit
) {
    val uriHandler = LocalUriHandler.current
    ButtonShapeless(
        title = stringResource(R.string.settings_about_links),
        description = stringResource(R.string.settings_about_links_description),
        expanded = expanded
    ) {
        onExpandedChange(!expanded)
    }
    AnimatedVisibility(
        visible = expanded,
        enter = expandVertically() + fadeIn(),
        exit = shrinkVertically() + fadeOut()
    ) {
        ColumnRounded(Modifier.padding(horizontal = 8.dp)) {
            SettingsConstant.socials.forEach {
                val icon = when(it.url.split("/")[2]) {
                    "discord.gg" -> painterResource(id = R.drawable.discord)
                    "github.com" -> painterResource(id = R.drawable.github)
                    else -> rememberVectorPainter(Icons.Rounded.Public)
                }
                ButtonShapeless(
                    title = it.name,
                    painter = icon,
                    rounded = true,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                ) {
                    uriHandler.openUri(it.url)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun ExperimentalSettings(
    mainViewModel: MainViewModel = getViewModel(),
    settingsViewModel: SettingsViewModel = getViewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    ColumnDivider(title = stringResource(R.string.settings_experimental), bottomDivider = false, topDivider = true) {
        Text(stringResource(R.string.settings_experimental_description), color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(horizontal = 16.dp))
        Switch(
            title = stringResource(R.string.settings_experimental_showMaterialYouOption),
            checked = settingsViewModel.showMaterialYouOption,
            onCheckedChange = {
                settingsViewModel.showMaterialYouOption = it
            }
        )
        ButtonShapeless(
            title = stringResource(R.string.settings_experimental_checkUpdates)
        ) {
            scope.launch { mainViewModel.checkUpdates(ignoreVersion = true) }
        }
        ButtonShapeless(
            title = stringResource(R.string.settings_experimental_showUpdateToast)
        ) {
            mainViewModel.showUpdateToast()
        }
        ButtonShapeless(
            title = stringResource(R.string.settings_experimental_showUpdateDialog)
        ) {
            scope.launch { mainViewModel.updateSheetState.show() }
        }
        SettingsConstant.experimentalPrefOptions.forEach { prefEdit ->
            OutlinedTextField(
                value = prefEdit.getValue(settingsViewModel.prefs),
                onValueChange = {
                    prefEdit.setValue(it, settingsViewModel.prefs)
                },
                label = {
                    Text(stringResource(prefEdit.labelResourceId))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
        ButtonShapeless(title = stringResource(R.string.settings_experimental_resetPrefs), contentColor = MaterialTheme.colorScheme.error) {
            SettingsConstant.experimentalPrefOptions.forEach {
                it.setValue(it.default, settingsViewModel.prefs)
            }
            settingsViewModel.topToastState.showToast(
                text = R.string.settings_experimental_resetPrefsDone,
                icon = Icons.Rounded.Done,
                type = TopToastType.ANDROID
            )
            GeneralUtil.restartApp(context)
        }
    }
}