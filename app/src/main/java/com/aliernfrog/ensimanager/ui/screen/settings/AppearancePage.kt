package com.aliernfrog.ensimanager.ui.screen.settings

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Brush
import androidx.compose.material.icons.rounded.Contrast
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.aliernfrog.ensimanager.R
import com.aliernfrog.ensimanager.ui.component.SegmentedButtons
import com.aliernfrog.ensimanager.ui.component.VerticalSegmentor
import com.aliernfrog.ensimanager.ui.component.expressive.ExpressiveSection
import com.aliernfrog.ensimanager.ui.component.expressive.ExpressiveSwitchRow
import com.aliernfrog.ensimanager.ui.component.expressive.toRowFriendlyColor
import com.aliernfrog.ensimanager.ui.theme.Theme
import com.aliernfrog.ensimanager.ui.theme.supportsMaterialYou
import com.aliernfrog.ensimanager.ui.viewmodel.SettingsViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun AppearancePage(
    settingsViewModel: SettingsViewModel = koinViewModel(),
    onNavigateBackRequest: () -> Unit
) {
    SettingsPageContainer(
        title = stringResource(R.string.settings_appearance),
        onNavigateBackRequest = onNavigateBackRequest
    ) {
        ExpressiveSection(
            title = stringResource(R.string.settings_appearance_theme)
        ) {
            SegmentedButtons(
                options = Theme.entries.map { stringResource(it.label) },
                selectedIndex = settingsViewModel.prefs.theme.value,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
            ) {
                settingsViewModel.prefs.theme.value = it
            }
        }

        ExpressiveSection(
            title = stringResource(R.string.settings_appearance_colors)
        ) {
            VerticalSegmentor(
                {
                    ExpressiveSwitchRow(
                        title = stringResource(R.string.settings_appearance_materialYou),
                        description = stringResource(
                            if (supportsMaterialYou) R.string.settings_appearance_materialYou_description
                            else R.string.settings_appearance_materialYou_unavailable
                        ),
                        painter = rememberVectorPainter(Icons.Rounded.Brush),
                        checked = settingsViewModel.prefs.materialYou.value,
                        enabled = supportsMaterialYou,
                        containerColor = MaterialTheme.colorScheme.surfaceContainer,
                        iconContainerColor = Color.Yellow.toRowFriendlyColor
                    ) {
                        settingsViewModel.prefs.materialYou.value = it
                    }
                },
                {
                    ExpressiveSwitchRow(
                        title = stringResource(R.string.settings_appearance_pitchBlack),
                        description = stringResource(R.string.settings_appearance_pitchBlack_description),
                        painter = rememberVectorPainter(Icons.Rounded.Contrast),
                        checked = settingsViewModel.prefs.pitchBlack.value,
                        containerColor = MaterialTheme.colorScheme.surfaceContainer,
                        iconContainerColor = Color.Black.toRowFriendlyColor
                    ) {
                        settingsViewModel.prefs.pitchBlack.value = it
                    }
                },
                modifier = Modifier.padding(horizontal = 12.dp)
            )
        }
    }
}