package com.aliernfrog.ensimanager.ui.sheet

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Api
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aliernfrog.ensimanager.R
import com.aliernfrog.ensimanager.domain.APIState
import com.aliernfrog.ensimanager.domain.AppState
import com.aliernfrog.ensimanager.ui.component.api.ProfileIcon
import io.github.aliernfrog.shared.ui.component.AppModalBottomSheet
import io.github.aliernfrog.shared.ui.component.IconButtonWithTooltip
import io.github.aliernfrog.shared.ui.component.VerticalSegmentor
import io.github.aliernfrog.shared.ui.component.expressive.ExpressiveButtonRow
import io.github.aliernfrog.shared.ui.component.expressive.ExpressiveRowIcon
import io.github.aliernfrog.shared.ui.component.expressive.ExpressiveSection
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ProfileSwitchSheet(
    sheetState: SheetState,
    onNavigateSettingsRequest: () -> Unit,
    onNavigateApiProfilesRequest: () -> Unit
) {
    val appState = koinInject<AppState>()
    val apiState = koinInject<APIState>()
    val scope = rememberCoroutineScope()

    val apiProfiles = apiState.apiProfiles.collectAsStateWithLifecycle().value

    AppModalBottomSheet(
        sheetState = sheetState
    ) {
        VerticalSegmentor(
            {
                val onSettingsClick: () -> Unit = {
                    scope.launch {
                        onNavigateSettingsRequest()
                        sheetState.hide()
                        appState.showUpdateNotification = false
                    }
                }
                ExpressiveButtonRow(
                    title = stringResource(R.string.settings),
                    icon = { ExpressiveRowIcon(rememberVectorPainter(Icons.Rounded.Settings)) },
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                    trailingComponent = if (appState.showUpdateNotification) { {
                       Button(
                           onClick = onSettingsClick,
                           shapes = ButtonDefaults.shapes()
                       ) {
                           Text(stringResource(R.string.profileSwitcher_update))
                       }
                    } } else null
                ) {
                    onSettingsClick()
                }
            },
            modifier = Modifier.padding(horizontal = 12.dp)
        )

        val profileButtons: List<@Composable () -> Unit> = apiProfiles.map { profile -> {
            val isAvailable = profile.isAvailable
            val profileColor = remember(profile) {
                Color(profile.color)
            }

            ExpressiveButtonRow(
                title = profile.name,
                enabled = isAvailable,
                icon = {
                    ProfileIcon(
                        profileName = profile.name,
                        model = profile.endpoints?.metadata?.iconURL,
                        containerColor = profileColor,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                    )
                },
                containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                trailingComponent = {
                    if (isAvailable) RadioButton(
                        selected = apiState.chosenProfile?.id == profile.id,
                        onClick = { apiState.chosenProfile = profile }
                    )
                    else if (profile.isFetching) CircularProgressIndicator()
                    else IconButtonWithTooltip(
                        icon = rememberVectorPainter(Icons.Default.Refresh),
                        contentDescription = stringResource(R.string.profiles_fetch),
                        onClick = { scope.launch { profile.fetchAPIEndpoints() } }
                    )
                }
            ) {
                if (isAvailable) scope.launch {
                    apiState.chosenProfile = profile
                    sheetState.hide()
                }
            }
        } }

        ExpressiveSection(title = null) {
            VerticalSegmentor(
                *profileButtons.toTypedArray(),
                {
                    ExpressiveButtonRow(
                        title = stringResource(R.string.profileSwitcher_manageProfiles),
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