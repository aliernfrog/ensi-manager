package com.aliernfrog.ensimanager.ui.component

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.aliernfrog.ensimanager.R
import com.aliernfrog.ensimanager.domain.APIState
import com.aliernfrog.ensimanager.domain.AppState
import com.aliernfrog.ensimanager.ui.component.api.ProfileIcon
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SettingsButton(
    modifier: Modifier = Modifier,
    profileSwitcher: Boolean = true,
    enabled: Boolean = true,
    onNavigateSettingsRequest: () -> Unit
) {
    val appState = koinInject<AppState>()
    val apiState = koinInject<APIState>()

    val scope = rememberCoroutineScope()
    val chosenProfile = apiState.chosenProfile

    @Composable
    fun BadgedIconButton(content: @Composable () -> Unit) {
        IconButton(
            shapes = IconButtonDefaults.shapes(),
            modifier = modifier,
            enabled = enabled,
            onClick = {
                if (profileSwitcher) scope.launch {
                    apiState.profileSwitcherSheetState.show()
                } else {
                    onNavigateSettingsRequest()
                    appState.showUpdateNotification = false
                }
            }
        ) {
            if (appState.showUpdateNotification) BadgedBox(
                badge = { Badge() }
            ) {
                content()
            }
            else content()
        }
    }

    if (profileSwitcher) BadgedIconButton {
        ProfileIcon(
            profileName = chosenProfile?.name ?: "-",
            model = chosenProfile?.endpoints?.metadata?.iconURL,
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
        )
    } else BadgedIconButton {
        Icon(
            imageVector = Icons.Default.Settings,
            contentDescription = stringResource(R.string.settings)
        )
    }
}