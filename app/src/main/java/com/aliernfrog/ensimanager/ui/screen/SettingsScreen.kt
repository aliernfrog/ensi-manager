package com.aliernfrog.ensimanager.ui.screen

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material.icons.rounded.Public
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.aliernfrog.ensimanager.MainActivity
import com.aliernfrog.ensimanager.R
import com.aliernfrog.ensimanager.SettingsConstant
import com.aliernfrog.ensimanager.state.SettingsState
import com.aliernfrog.ensimanager.state.UpdateState
import com.aliernfrog.ensimanager.ui.component.*
import com.aliernfrog.ensimanager.ui.theme.AppComponentShape
import com.aliernfrog.ensimanager.ui.theme.supportsMaterialYou
import com.aliernfrog.ensimanager.util.Destination
import com.aliernfrog.ensimanager.util.staticutil.GeneralUtil
import com.aliernfrog.ensimanager.ui.component.ColumnDivider
import com.aliernfrog.toptoast.enum.TopToastType
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    settingsState: SettingsState,
    updateState: UpdateState,
    navController: NavController,
    showApiOptions: Boolean = true,
    onBackClick: (() -> Unit)? = null
) {
    AppScaffold(
        title = stringResource(R.string.settings),
        topAppBarState = settingsState.topAppBarState,
        onBackClick = onBackClick
    ) {
        Column(Modifier.fillMaxSize().verticalScroll(settingsState.scrollState)) {
            AppearanceOptions(settingsState)
            if (showApiOptions) ApiOptions(navController)
            AboutApp(updateState, settingsState)
            if (settingsState.experimentalSettingsShown) ExperimentalSettings(updateState, settingsState)
            Spacer(Modifier.navigationBarsPadding())
        }
    }
}

@Composable
private fun AppearanceOptions(settingsState: SettingsState) {
    val themeOptions = listOf(
        stringResource(R.string.settings_appearance_theme_system),
        stringResource(R.string.settings_appearance_theme_light),
        stringResource(R.string.settings_appearance_theme_dark)
    )
    ColumnDivider(title = stringResource(R.string.settings_appearance)) {
        ButtonShapeless(
            title = stringResource(R.string.settings_appearance_theme),
            description = stringResource(R.string.settings_appearance_theme_description),
            expanded = settingsState.themeOptionsExpanded
        ) {
            settingsState.themeOptionsExpanded= !settingsState.themeOptionsExpanded
        }
        AnimatedVisibility(
            visible = settingsState.themeOptionsExpanded,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            ColumnRounded(Modifier.padding(horizontal = 8.dp)) {
                RadioButtons(
                    options = themeOptions,
                    initialIndex = settingsState.theme,
                    optionsRounded = true
                ) {
                    settingsState.updateTheme(it)
                }
            }
        }
        if (supportsMaterialYou || settingsState.forceShowMaterialYouOption) Switch(
            title = stringResource(R.string.settings_appearance_materialYou),
            description = stringResource(R.string.settings_appearance_materialYou_description),
            checked = settingsState.materialYou
        ) {
            settingsState.updateMaterialYou(it)
        }
    }
}

@Composable
private fun ApiOptions(navController: NavController) {
    ColumnDivider(title = stringResource(R.string.settings_api)) {
        ButtonShapeless(
            title = stringResource(R.string.settings_api_config),
            description = stringResource(R.string.settings_api_config_description),
            expanded = false,
            arrowRotation = 90f
        ) {
            navController.navigate(Destination.SETUP.route)
        }
    }
}

@Composable
private fun AboutApp(updateState: UpdateState, settingsState: SettingsState) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val version = "v${GeneralUtil.getAppVersionName(context)} (${GeneralUtil.getAppVersionCode(context)})"
    ColumnDivider(title = stringResource(R.string.settings_about), bottomDivider = false) {
        ButtonWithComponent(
            title = stringResource(R.string.settings_about_version),
            description = version,
            component = {
                OutlinedButton(
                    onClick = { scope.launch { updateState.checkUpdates(manuallyTriggered = true) } }
                ) {
                    Text(stringResource(R.string.settings_about_checkUpdates))
                }
            }
        ) {
            settingsState.onAboutClick()
        }
        Switch(
            title = stringResource(R.string.settings_about_autoCheckUpdates),
            description = stringResource(R.string.settings_about_autoCheckUpdates_description),
            checked = settingsState.autoCheckUpdates
        ) {
            settingsState.updateAutoCheckUpdates(it)
        }
        Links(settingsState)
    }
}

@Composable
private fun Links(settingsState: SettingsState) {
    val uriHandler = LocalUriHandler.current
    ButtonShapeless(
        title = stringResource(R.string.settings_about_links),
        description = stringResource(R.string.settings_about_links_description),
        expanded = settingsState.linksExpanded
    ) {
        settingsState.linksExpanded = !settingsState.linksExpanded
    }
    AnimatedVisibility(
        visible = settingsState.linksExpanded,
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
private fun ExperimentalSettings(updateState: UpdateState, settingsState: SettingsState) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val config = settingsState.config
    val configEditor = config.edit()
    ColumnDivider(title = stringResource(R.string.settings_experimental), bottomDivider = false, topDivider = true) {
        Text(stringResource(R.string.settings_experimental_description), color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(horizontal = 16.dp))
        Switch(
            title = stringResource(R.string.settings_experimental_forceShowMaterialYouOption),
            checked = settingsState.forceShowMaterialYouOption,
            onCheckedChange = {
                settingsState.forceShowMaterialYouOption = it
            }
        )
        ButtonShapeless(
            title = stringResource(R.string.settings_experimental_checkUpdates)
        ) {
            scope.launch { updateState.checkUpdates(ignoreVersion = true) }
        }
        ButtonShapeless(
            title = stringResource(R.string.settings_experimental_showUpdateToast)
        ) {
            updateState.showUpdateToast()
        }
        ButtonShapeless(
            title = stringResource(R.string.settings_experimental_showUpdateDialog)
        ) {
            scope.launch { updateState.updateSheetState.show() }
        }
        SettingsConstant.experimentalPrefOptions.forEach { prefEdit ->
            var value by remember { mutableStateOf(config.getString(prefEdit.key, prefEdit.default)!!) }
            OutlinedTextField(
                label = { Text("Prefs: ${prefEdit.key}") },
                value = value,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                shape = AppComponentShape,
                onValueChange = {
                    value = it
                    configEditor.putString(prefEdit.key, it)
                    configEditor.apply()
                }
            )
        }
        ButtonShapeless(title = stringResource(R.string.settings_experimental_resetPrefs), contentColor = MaterialTheme.colorScheme.error) {
            SettingsConstant.experimentalPrefOptions.forEach {
                configEditor.remove(it.key)
            }
            configEditor.apply()
            settingsState.topToastState.showToast(
                text = R.string.settings_experimental_resetPrefsDone,
                icon = Icons.Rounded.Done,
                type = TopToastType.ANDROID
            )
            restartApp(context)
        }
    }
}

private fun restartApp(context: Context) {
    val intent = Intent(context, MainActivity::class.java)
    (context as Activity).finish()
    context.startActivity(intent)
}