package com.aliernfrog.ensimanager.ui.screen.settings

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.aliernfrog.ensimanager.R
import com.aliernfrog.ensimanager.SettingsConstant
import com.aliernfrog.ensimanager.ui.component.form.ButtonRow
import com.aliernfrog.ensimanager.ui.component.form.FormSection
import com.aliernfrog.ensimanager.ui.component.form.SwitchRow
import com.aliernfrog.ensimanager.ui.theme.AppComponentShape
import com.aliernfrog.ensimanager.ui.viewmodel.MainViewModel
import com.aliernfrog.ensimanager.util.staticutil.GeneralUtil
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExperimentalPage(
    mainViewModel: MainViewModel = koinViewModel(),
    onNavigateBackRequest: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    SettingsPageContainer(
        title = stringResource(R.string.settings_experimental),
        onNavigateBackRequest = onNavigateBackRequest
    ) {
        SwitchRow(
            title = stringResource(R.string.settings_experimental),
            description = stringResource(R.string.settings_experimental_description),
            checked = mainViewModel.prefs.experimentalOptionsEnabled,
            shape = AppComponentShape,
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.padding(16.dp)
        ) {
            mainViewModel.prefs.experimentalOptionsEnabled = it
        }

        FormSection(title = "Updates") {
            ButtonRow(
                title = "Check updates (ignore version)"
            ) {
                scope.launch {
                    mainViewModel.checkUpdates(ignoreVersion = true)
                }
            }
            ButtonRow(
                title = "Show update toast"
            ) {
                mainViewModel.showUpdateToast()
            }
            ButtonRow(
                title = "Show update dialog"
            ) {
                scope.launch {
                    mainViewModel.updateSheetState.show()
                }
            }
        }

        FormSection(title = "Prefs", bottomDivider = false) {
            SettingsConstant.experimentalPrefOptions.forEach { prefEdit ->
                OutlinedTextField(
                    value = prefEdit.getValue(mainViewModel.prefs),
                    onValueChange = {
                        prefEdit.setValue(it, mainViewModel.prefs)
                    },
                    label = {
                        Text(stringResource(prefEdit.labelResourceId))
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            ButtonRow(
                title = "Reset experimental prefs",
                contentColor = MaterialTheme.colorScheme.error
            ) {
                SettingsConstant.experimentalPrefOptions.forEach {
                    it.setValue(it.default, mainViewModel.prefs)
                }
                mainViewModel.topToastState.showAndroidToast(
                    text = "Restored default values for experimental prefs",
                    icon = Icons.Rounded.Done
                )
                GeneralUtil.restartApp(context)
            }
        }
    }
}