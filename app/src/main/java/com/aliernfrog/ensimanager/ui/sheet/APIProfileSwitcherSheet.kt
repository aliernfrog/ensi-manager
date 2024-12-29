package com.aliernfrog.ensimanager.ui.sheet

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Api
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter
import com.aliernfrog.ensimanager.R
import com.aliernfrog.ensimanager.data.api.cache
import com.aliernfrog.ensimanager.data.api.id
import com.aliernfrog.ensimanager.data.api.isAvailable
import com.aliernfrog.ensimanager.ui.component.AppModalBottomSheet
import com.aliernfrog.ensimanager.ui.component.VerticalSegmentor
import com.aliernfrog.ensimanager.ui.component.form.ButtonRow
import com.aliernfrog.ensimanager.ui.component.form.FormSection
import com.aliernfrog.ensimanager.ui.viewmodel.APIViewModel
import com.aliernfrog.ensimanager.util.Destination
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun APIProfileSwitchSheet(
    apiViewModel: APIViewModel = koinViewModel(),
    sheetState: SheetState = apiViewModel.profileSwitcherSheetState,
    onNavigateSettingsRequest: () -> Unit,
    onNavigateApiProfilesRequest: () -> Unit
) {
    val scope = rememberCoroutineScope()

    AppModalBottomSheet(
        sheetState = sheetState
    ) {
        VerticalSegmentor(
            {
                val onSettingsClick: () -> Unit = {
                    scope.launch {
                        onNavigateSettingsRequest()
                        sheetState.hide()
                        Destination.SETTINGS.hasNotification.value = false
                    }
                }
                ButtonRow(
                    title = stringResource(R.string.settings),
                    painter = rememberVectorPainter(Icons.Default.Settings),
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                    trailingComponent = if (Destination.SETTINGS.hasNotification.value) { {
                       Button(
                           onClick = onSettingsClick
                       ) {
                           Text(stringResource(R.string.api_profiles_switcher_update))
                       }
                    } } else null
                ) {
                    onSettingsClick()
                }
            },
            {
                ButtonRow(
                    title = stringResource(R.string.api_profiles),
                    painter = rememberVectorPainter(Icons.Default.Api),
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHighest
                ) {
                    scope.launch {
                        onNavigateApiProfilesRequest()
                        sheetState.hide()
                    }
                }
            },
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        val profileButtons: List<@Composable () -> Unit> = apiViewModel.apiProfiles.map { profile -> {
            ButtonRow(
                title = profile.name,
                description = if (!profile.isAvailable) stringResource(R.string.api_profiles_switcher_unavailable) else null,
                modifier = if (!profile.isAvailable) Modifier.alpha(0.5f) else Modifier,
                painter = profile.cache?.endpoints?.metadata?.iconURL.let { iconURL ->
                    if (iconURL != null) rememberAsyncImagePainter(iconURL)
                    else rememberVectorPainter(Icons.Default.Api)
                },
                containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                iconColorFilter = null,
                trailingComponent = if (profile.isAvailable) { {
                    RadioButton(
                        selected = apiViewModel.chosenProfile?.id == profile.id,
                        onClick = { apiViewModel.chosenProfile = profile }
                    )
                } } else null
            ) {
                if (profile.isAvailable)scope.launch {
                    apiViewModel.chosenProfile = profile
                    sheetState.hide()
                }
            }
        } }

        FormSection(
            title = null,
            topDivider = true,
            bottomDivider = false
        ) {
            VerticalSegmentor(
                *profileButtons.toTypedArray(),
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }
    }
}