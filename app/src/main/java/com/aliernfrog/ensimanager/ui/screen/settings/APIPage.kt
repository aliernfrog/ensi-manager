package com.aliernfrog.ensimanager.ui.screen.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import com.aliernfrog.ensimanager.R
import com.aliernfrog.ensimanager.data.api.isAvailable
import com.aliernfrog.ensimanager.ui.component.form.RadioButtons
import com.aliernfrog.ensimanager.ui.component.form.ExpandableRow
import com.aliernfrog.ensimanager.ui.component.form.RadioButtonChoice
import com.aliernfrog.ensimanager.ui.component.form.SwitchRow
import com.aliernfrog.ensimanager.ui.viewmodel.APIViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun APIPage(
    apiViewModel: APIViewModel = koinViewModel(),
    onNavigateBackRequest: () -> Unit
) {
    var defaultProfileChoicesExpanded by rememberSaveable { mutableStateOf(false) }

    SettingsPageContainer(
        title = stringResource(R.string.settings_api),
        onNavigateBackRequest = onNavigateBackRequest
    ) {
        SwitchRow(
            title = stringResource(R.string.settings_api_rememberLast),
            checked = apiViewModel.prefs.rememberLastSelectedAPIProfile.value
        ) {
            apiViewModel.prefs.rememberLastSelectedAPIProfile.value = it
        }
        ExpandableRow(
            title = stringResource(R.string.settings_api_defaultProfile),
            description = stringResource(R.string.settings_api_defaultProfile_description),
            expanded = defaultProfileChoicesExpanded,
            onClickHeader = {
                defaultProfileChoicesExpanded = !defaultProfileChoicesExpanded
            }
        ) {
            RadioButtons(
                choices = listOf(
                    *apiViewModel.apiProfiles.map {
                        val available = it.isAvailable
                        RadioButtonChoice(
                            title = it.name,
                            description = if (available) null else stringResource(R.string.api_profiles_switcher_unavailable),
                            enabled = available
                        )
                    }.toTypedArray(),
                    RadioButtonChoice(
                        title = stringResource(R.string.settings_api_defaultProfile_none),
                        indexOverride = -1
                    ),
                ),
                selectedOptionIndex = 0,
                onSelect = { index ->
                    apiViewModel.prefs.defaultAPIProfileIndex.value = index
                }
            )
        }
    }
}
