package com.aliernfrog.ensimanager.ui.screen.settings

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.aliernfrog.ensimanager.R
import com.aliernfrog.ensimanager.ui.component.VerticalSegmentor
import com.aliernfrog.ensimanager.ui.component.expressive.ExpressiveSwitchRow
import com.aliernfrog.ensimanager.ui.component.form.RadioButtons
import com.aliernfrog.ensimanager.ui.component.form.ExpandableRow
import com.aliernfrog.ensimanager.ui.component.form.RadioButtonChoice
import com.aliernfrog.ensimanager.ui.viewmodel.APIViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun APIPage(
    apiViewModel: APIViewModel = koinViewModel(),
    onNavigateBackRequest: () -> Unit
) {
    var defaultProfileChoicesExpanded by rememberSaveable { mutableStateOf(false) }
    val defaultProfileChoices = listOf(
        *apiViewModel.apiProfiles.map {
            RadioButtonChoice(title = it.name)
        }.toTypedArray(),
        RadioButtonChoice(
            title = stringResource(R.string.settings_api_defaultProfile_none),
            indexOverride = -1
        )
    )
    val chosenDefaultProfileName = apiViewModel.prefs.defaultAPIProfileIndex.value.let {
        defaultProfileChoices.elementAtOrNull(it)?.title ?: stringResource(R.string.settings_api_defaultProfile_none)
    }

    SettingsPageContainer(
        title = stringResource(R.string.settings_api),
        onNavigateBackRequest = onNavigateBackRequest
    ) {
        VerticalSegmentor(
            {
                ExpressiveSwitchRow(
                    title = stringResource(R.string.settings_api_rememberLast),
                    checked = apiViewModel.prefs.rememberLastSelectedAPIProfile.value,
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                ) {
                    apiViewModel.prefs.rememberLastSelectedAPIProfile.value = it
                }
            },
            {
                ExpandableRow(
                    title = stringResource(R.string.settings_api_defaultProfile),
                    description = stringResource(R.string.settings_api_defaultProfile_description),
                    trailingButtonText = chosenDefaultProfileName,
                    expanded = defaultProfileChoicesExpanded,
                    minimizedHeaderColor = MaterialTheme.colorScheme.surfaceContainer,
                    onClickHeader = {
                        defaultProfileChoicesExpanded = !defaultProfileChoicesExpanded
                    }
                ) {
                    RadioButtons(
                        choices = defaultProfileChoices,
                        selectedIndex = apiViewModel.prefs.defaultAPIProfileIndex.value,
                        onSelect = { index ->
                            apiViewModel.prefs.defaultAPIProfileIndex.value = index
                        }
                    )
                }
            },
            modifier = Modifier.padding(horizontal = 12.dp)
        )
    }
}
