package com.aliernfrog.ensimanager.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.aliernfrog.ensimanager.ApiRoutes
import com.aliernfrog.ensimanager.R
import com.aliernfrog.ensimanager.state.OptionsState
import com.aliernfrog.ensimanager.ui.composable.ManagerRadioButtons
import com.aliernfrog.ensimanager.ui.composable.ManagerRouteOption
import com.aliernfrog.ensimanager.ui.composable.ManagerSwitch
import com.aliernfrog.ensimanager.ui.theme.supportsMaterialYou

@Composable
fun OptionsScreen(optionsState: OptionsState) {
    Column(Modifier.fillMaxSize().verticalScroll(optionsState.scrollState)) {
        ThemeOptions(optionsState)
        ApiOptions(optionsState)
    }
}

@Composable
private fun ThemeOptions(optionsState: OptionsState) {
    val themeOptions = listOf(
        stringResource(R.string.options_theme_system),
        stringResource(R.string.options_theme_light),
        stringResource(R.string.options_theme_dark)
    )
    OptionsColumn(title = stringResource(R.string.options_theme)) {
        ManagerRadioButtons(options = themeOptions, initialIndex = optionsState.theme.value) {
            optionsState.setTheme(it)
        }
        if (supportsMaterialYou) {
            ManagerSwitch(
                title = stringResource(R.string.options_theme_materialYou),
                description = stringResource(R.string.options_theme_materialYou_description),
                checked = optionsState.materialYou.value
            ) {
                optionsState.setMaterialYou(it)
            }
        }
    }
}

@Composable
private fun ApiOptions(optionsState: OptionsState) {
    OptionsColumn(title = stringResource(R.string.options_api), bottomDivider = false) {
        ApiRoutes.options.forEach { route ->
            val urlEdit = remember { mutableStateOf(optionsState.config.getString(route.prefKey, "")!!) }
            ManagerRouteOption(route, urlEdit.value, Modifier.padding(horizontal = 8.dp)) {
                urlEdit.value = it
                optionsState.config.edit().putString(route.prefKey, it).apply()
            }
        }
    }
}

@Composable
private fun OptionsColumn(title: String, modifier: Modifier = Modifier, bottomDivider: Boolean = true, topDivider: Boolean = false, content: @Composable ColumnScope.() -> Unit) {
    if (topDivider) Divider(modifier = Modifier.padding(16.dp).alpha(0.7f), thickness = 1.dp, color = MaterialTheme.colorScheme.surfaceVariant)
    Text(text = title, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
    Column(modifier, content = content)
    if (bottomDivider) Divider(modifier = Modifier.padding(16.dp).alpha(0.7f), thickness = 1.dp, color = MaterialTheme.colorScheme.surfaceVariant)
}