package com.aliernfrog.ensimanager.ui.screen

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.aliernfrog.ensimanager.R
import com.aliernfrog.ensimanager.state.SettingsState
import com.aliernfrog.ensimanager.ui.component.*
import com.aliernfrog.ensimanager.ui.theme.supportsMaterialYou
import com.aliernfrog.ensimanager.util.Destination
import com.aliernfrog.lactool.ui.component.ColumnDivider

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    settingsState: SettingsState,
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
        if (supportsMaterialYou) Switch(
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
    ColumnDivider(title = stringResource(R.string.settings_api), bottomDivider = false) {
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