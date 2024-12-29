package com.aliernfrog.ensimanager.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Api
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter
import com.aliernfrog.ensimanager.R
import com.aliernfrog.ensimanager.data.api.cache
import com.aliernfrog.ensimanager.ui.viewmodel.APIViewModel
import com.aliernfrog.ensimanager.util.Destination
import org.koin.androidx.compose.koinViewModel

@Composable
fun SettingsButton(
    modifier: Modifier = Modifier,
    profileSwitcher: Boolean = true,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    val apiViewModel = koinViewModel<APIViewModel>()
    val hasNotification = Destination.SETTINGS.hasNotification.value

    @Composable
    fun BadgedIconButton(content: @Composable () -> Unit) {
        IconButton(
            modifier = modifier,
            enabled = enabled,
            onClick = {
                onClick()
                Destination.SETTINGS.hasNotification.value = false
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

    if (profileSwitcher) VerticalPager(
        state = apiViewModel.profilePagerState,
        beyondViewportPageCount = 1,
        modifier = Modifier.size(40.dp)
    ) { page ->
        val profile = apiViewModel.availableProfiles.getOrNull(page)

        BadgedIconButton {
            Image(
                painter = profile?.cache?.endpoints?.metadata?.iconURL.let { iconURL ->
                    if (iconURL != null) rememberAsyncImagePainter(iconURL)
                    else rememberVectorPainter(Icons.Default.Api)
                },
                contentDescription = stringResource(R.string.api_profiles_switcher),
                modifier = Modifier.size(32.dp)
            )
        }
    } else BadgedIconButton {
        Icon(
            imageVector = Icons.Default.Settings,
            contentDescription = stringResource(R.string.settings)
        )
    }
}