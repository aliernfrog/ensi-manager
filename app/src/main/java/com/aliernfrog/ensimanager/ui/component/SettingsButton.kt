package com.aliernfrog.ensimanager.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Api
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter
import com.aliernfrog.ensimanager.R
import com.aliernfrog.ensimanager.data.api.cache
import com.aliernfrog.ensimanager.ui.viewmodel.APIViewModel
import com.aliernfrog.ensimanager.util.Destination
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsButton(
    modifier: Modifier = Modifier,
    profileSwitcher: Boolean = true,
    enabled: Boolean = true,
    onNavigateSettingsRequest: () -> Unit
) {
    val apiViewModel = koinViewModel<APIViewModel>()
    val scope = rememberCoroutineScope()
    val hasNotification = Destination.SETTINGS.hasNotification.value

    @Composable
    fun BadgedIconButton(content: @Composable () -> Unit) {
        IconButton(
            modifier = modifier,
            enabled = enabled,
            onClick = {
                if (profileSwitcher) scope.launch { apiViewModel.profileSwitcherSheetState.show() }
                else {
                    onNavigateSettingsRequest()
                    Destination.SETTINGS.hasNotification.value = false
                }
            }
        ) {
            if (hasNotification) BadgedBox(
                badge = { Badge() }
            ) {
                content()
            }
            else content()
        }
    }

    if (profileSwitcher) BadgedIconButton {
        Image(
            painter = apiViewModel.chosenProfile?.cache?.endpoints?.metadata?.iconURL.let { iconURL ->
                if (iconURL != null) rememberAsyncImagePainter(iconURL)
                else rememberVectorPainter(Icons.Default.Api)
            },
            contentDescription = stringResource(R.string.api_profiles_switcher),
            modifier = Modifier.size(32.dp)
        )
    } else BadgedIconButton {
        Icon(
            imageVector = Icons.Default.Settings,
            contentDescription = stringResource(R.string.settings)
        )
    }
}