package com.aliernfrog.ensimanager.ui.sheet

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Api
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.aliernfrog.ensimanager.R
import com.aliernfrog.ensimanager.data.api.cache
import com.aliernfrog.ensimanager.data.api.id
import com.aliernfrog.ensimanager.data.api.isAvailable
import com.aliernfrog.ensimanager.ui.component.AppModalBottomSheet
import com.aliernfrog.ensimanager.ui.component.VerticalSegmentor
import com.aliernfrog.ensimanager.ui.component.expressive.ExpressiveButtonRow
import com.aliernfrog.ensimanager.ui.component.expressive.ExpressiveRowIcon
import com.aliernfrog.ensimanager.ui.component.expressive.ExpressiveSection
import com.aliernfrog.ensimanager.ui.component.expressive.ROW_DEFAULT_ICON_SIZE
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
                ExpressiveButtonRow(
                    title = stringResource(R.string.settings),
                    icon = { ExpressiveRowIcon(rememberVectorPainter(Icons.Rounded.Settings)) },
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
            modifier = Modifier.padding(horizontal = 12.dp)
        )

        val profileButtons: List<@Composable () -> Unit> = apiViewModel.apiProfiles.map { profile -> {
            val isAvailable = profile.isAvailable
            ExpressiveButtonRow(
                title = profile.name,
                description = if (!isAvailable) stringResource(R.string.api_profiles_switcher_unavailable) else null,
                enabled = isAvailable,
                icon = profile.cache?.endpoints?.metadata?.iconURL?.let { iconURL -> {
                    AsyncImage(
                        model = iconURL,
                        contentDescription = null,
                        modifier = Modifier
                            .size(ROW_DEFAULT_ICON_SIZE)
                            .clip(CircleShape)
                    )
                } } ?: {
                    ExpressiveRowIcon(rememberVectorPainter(Icons.Default.Api))
                },
                containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                trailingComponent = if (isAvailable) { {
                    RadioButton(
                        selected = apiViewModel.chosenProfile?.id == profile.id,
                        onClick = { apiViewModel.chosenProfile = profile }
                    )
                } } else null
            ) {
                if (isAvailable) scope.launch {
                    apiViewModel.chosenProfile = profile
                    sheetState.hide()
                }
            }
        } }

        ExpressiveSection(title = null) {
            VerticalSegmentor(
                *profileButtons.toTypedArray(),
                {
                    ExpressiveButtonRow(
                        title = stringResource(R.string.api_profiles_switcher_manageProfiles),
                        icon = { ExpressiveRowIcon(rememberVectorPainter(Icons.Default.Api)) },
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHighest
                    ) {
                        scope.launch {
                            onNavigateApiProfilesRequest()
                            sheetState.hide()
                        }
                    }
                },
                modifier = Modifier.padding(horizontal = 12.dp)
            )
        }
    }
}