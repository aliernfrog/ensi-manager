package com.aliernfrog.ensimanager.ui.screen

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Update
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material.icons.rounded.Public
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.aliernfrog.ensimanager.R
import com.aliernfrog.ensimanager.SettingsConstant
import com.aliernfrog.ensimanager.data.ReleaseInfo
import com.aliernfrog.ensimanager.ui.component.AppScaffold
import com.aliernfrog.ensimanager.ui.component.ButtonIcon
import com.aliernfrog.ensimanager.ui.component.RadioButtons
import com.aliernfrog.ensimanager.ui.component.form.ButtonRow
import com.aliernfrog.ensimanager.ui.component.form.ExpandableRow
import com.aliernfrog.ensimanager.ui.component.form.FormSection
import com.aliernfrog.ensimanager.ui.component.form.SwitchRow
import com.aliernfrog.ensimanager.ui.theme.AppComponentShape
import com.aliernfrog.ensimanager.ui.viewmodel.MainViewModel
import com.aliernfrog.ensimanager.ui.viewmodel.SettingsViewModel
import com.aliernfrog.ensimanager.util.staticutil.GeneralUtil
import kotlinx.coroutines.launch
import org.koin.androidx.compose.getViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    mainViewModel: MainViewModel = getViewModel(),
    settingsViewModel: SettingsViewModel = getViewModel(),
    onNavigateAPIConfigScreenRequest: () -> Unit
) {
    val scope = rememberCoroutineScope()
    AppScaffold(
        title = stringResource(R.string.settings),
        topAppBarState = settingsViewModel.topAppBarState
    ) {
        Column(Modifier.fillMaxSize().verticalScroll(settingsViewModel.scrollState)) {
            UpdateNotification(
                isShown = mainViewModel.updateAvailable,
                versionInfo = mainViewModel.latestVersionInfo
            ) { scope.launch {
                mainViewModel.updateSheetState.show()
            } }

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
    FormSection(title = stringResource(R.string.settings_appearance)) {
        ExpandableRow(
            expanded = settingsViewModel.themeOptionsExpanded,
            title = stringResource(R.string.settings_appearance_theme),
            description = stringResource(R.string.settings_appearance_theme_description),
            onClickHeader = {
                settingsViewModel.themeOptionsExpanded = !settingsViewModel.themeOptionsExpanded
            }
        ) {
            RadioButtons(
                options = themeOptions,
                selectedOptionIndex = settingsViewModel.prefs.theme
            ) {
                settingsViewModel.prefs.theme = it
            }
        }
        if (settingsViewModel.showMaterialYouOption) SwitchRow(
            title = stringResource(R.string.settings_appearance_materialYou),
            description = stringResource(R.string.settings_appearance_materialYou_description),
            checked = settingsViewModel.prefs.materialYou
        ) {
            settingsViewModel.prefs.materialYou = it
        }
    }
}

@Composable
private fun APIOptions(
    onNavigateAPIConfigScreenRequest: () -> Unit
) {
    FormSection(title = stringResource(R.string.settings_api)) {
        ButtonRow(
            title = stringResource(R.string.settings_api_config),
            description = stringResource(R.string.settings_api_config_description),
            expanded = false,
            arrowRotation = if (LocalLayoutDirection.current == LayoutDirection.Rtl) 270f else 90f
        ) {
            onNavigateAPIConfigScreenRequest()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AboutApp(
    mainViewModel: MainViewModel = getViewModel(),
    settingsViewModel: SettingsViewModel = getViewModel()
) {
    val scope = rememberCoroutineScope()
    val version = "${mainViewModel.applicationVersionName} (${mainViewModel.applicationVersionCode})"
    FormSection(title = stringResource(R.string.settings_about), bottomDivider = false) {
        ButtonRow(
            title = stringResource(R.string.settings_about_version),
            description = version,
            trailingComponent = {
                UpdateButton(
                    updateAvailable = mainViewModel.updateAvailable
                ) { updateAvailable -> scope.launch {
                    if (updateAvailable) mainViewModel.updateSheetState.show()
                    else mainViewModel.checkUpdates(manuallyTriggered = true)
                } }
            }
        ) {
            settingsViewModel.onAboutClick()
        }
        SwitchRow(
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
    ExpandableRow(
        expanded = expanded,
        title = stringResource(R.string.settings_about_links),
        description = stringResource(R.string.settings_about_links_description),
        onClickHeader = {
            onExpandedChange(!expanded)
        }
    ) {
        SettingsConstant.socials.forEach {
            val icon = when(it.url.split("/")[2]) {
                "discord.gg" -> painterResource(id = R.drawable.discord)
                "github.com" -> painterResource(id = R.drawable.github)
                else -> rememberVectorPainter(Icons.Rounded.Public)
            }
            ButtonRow(
                title = it.name,
                description = it.url,
                painter = icon,
                contentPadding = PaddingValues(horizontal = 8.dp),
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
            ) {
                uriHandler.openUri(it.url)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExperimentalSettings(
    mainViewModel: MainViewModel = getViewModel(),
    settingsViewModel: SettingsViewModel = getViewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    FormSection(title = stringResource(R.string.settings_experimental), bottomDivider = false, topDivider = true) {
        Text(stringResource(R.string.settings_experimental_description), color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(horizontal = 16.dp))
        SwitchRow(
            title = stringResource(R.string.settings_experimental_showMaterialYouOption),
            checked = settingsViewModel.showMaterialYouOption,
            onCheckedChange = {
                settingsViewModel.showMaterialYouOption = it
            }
        )
        ButtonRow(
            title = stringResource(R.string.settings_experimental_checkUpdates)
        ) {
            scope.launch { mainViewModel.checkUpdates(ignoreVersion = true) }
        }
        ButtonRow(
            title = stringResource(R.string.settings_experimental_showUpdateToast)
        ) {
            mainViewModel.showUpdateToast()
        }
        ButtonRow(
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
        ButtonRow(title = stringResource(R.string.settings_experimental_resetPrefs), contentColor = MaterialTheme.colorScheme.error) {
            SettingsConstant.experimentalPrefOptions.forEach {
                it.setValue(it.default, settingsViewModel.prefs)
            }
            settingsViewModel.topToastState.showAndroidToast(
                text = R.string.settings_experimental_resetPrefsDone,
                icon = Icons.Rounded.Done
            )
            GeneralUtil.restartApp(context)
        }
    }
}

@Composable
fun UpdateNotification(
    isShown: Boolean,
    versionInfo: ReleaseInfo,
    onClick: () -> Unit
) {
    AnimatedVisibility(
        visible = isShown,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically()
    ) {
        Card(
            onClick = onClick,
            shape = AppComponentShape,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Icon(imageVector = Icons.Default.Update, contentDescription = null)
                Column {
                    Text(
                        text = stringResource(R.string.settings_updateNotification_updateAvailable)
                            .replace("{VERSION}", versionInfo.versionName),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = stringResource(R.string.settings_updateNotification_description),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Composable
fun UpdateButton(
    updateAvailable: Boolean,
    onClick: (updateAvailable: Boolean) -> Unit
) {
    AnimatedContent(updateAvailable) {
        if (it) ElevatedButton(
            onClick = { onClick(true) }
        ) {
            ButtonIcon(
                rememberVectorPainter(Icons.Default.Update)
            )
            Text(stringResource(R.string.settings_about_update))
        }
        else OutlinedButton(
            onClick = { onClick(false) }
        ) {
            Text(stringResource(R.string.settings_about_checkUpdates))
        }
    }
}