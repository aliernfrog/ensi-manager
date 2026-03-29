package com.aliernfrog.ensimanager.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Api
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter
import com.aliernfrog.ensimanager.R
import com.aliernfrog.ensimanager.domain.APIState
import com.aliernfrog.ensimanager.domain.AppState
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
        Image(
            painter = apiState.chosenProfile?.endpoints?.metadata?.iconURL.let { iconURL ->
                if (iconURL != null) rememberAsyncImagePainter(iconURL)
                else rememberVectorPainter(Icons.Default.Api)
            },
            contentDescription = stringResource(R.string.profileSwitcher),
            modifier = Modifier.size(32.dp)
        )
    } else BadgedIconButton {
        Icon(
            imageVector = Icons.Default.Settings,
            contentDescription = stringResource(R.string.settings)
        )
    }
}