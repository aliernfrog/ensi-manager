package com.aliernfrog.ensimanager.ui.screen.settings

import android.content.ClipData
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.rounded.Book
import androidx.compose.material.icons.rounded.CopyAll
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.Face
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.Update
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import coil3.compose.AsyncImage
import com.aliernfrog.ensimanager.R
import com.aliernfrog.ensimanager.SettingsConstant
import com.aliernfrog.ensimanager.ui.component.ButtonIcon
import com.aliernfrog.ensimanager.ui.component.HorizontalSegmentor
import com.aliernfrog.ensimanager.ui.component.VerticalSegmentor
import com.aliernfrog.ensimanager.ui.component.expressive.ExpressiveButtonRow
import com.aliernfrog.ensimanager.ui.component.expressive.ExpressiveRowIcon
import com.aliernfrog.ensimanager.ui.component.expressive.ExpressiveSection
import com.aliernfrog.ensimanager.ui.component.expressive.ExpressiveSwitchRow
import com.aliernfrog.ensimanager.ui.component.expressive.ROW_DEFAULT_ICON_SIZE
import com.aliernfrog.ensimanager.ui.viewmodel.MainViewModel
import com.aliernfrog.ensimanager.ui.viewmodel.SettingsViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutPage(
    mainViewModel: MainViewModel = koinViewModel(),
    settingsViewModel: SettingsViewModel = koinViewModel(),
    onNavigateLibsRequest: () -> Unit,
    onNavigateBackRequest: () -> Unit
) {
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current
    val clipboard = LocalClipboard.current

    val scope = rememberCoroutineScope()
    val appIcon = remember {
        context.packageManager.getApplicationIcon(context.packageName).toBitmap().asImageBitmap()
    }

    SettingsPageContainer(
        title = stringResource(R.string.settings_about),
        onNavigateBackRequest = onNavigateBackRequest
    ) {
        VerticalSegmentor(
            {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                        .padding(vertical = 8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            bitmap = appIcon,
                            contentDescription = stringResource(R.string.app_name),
                            modifier = Modifier
                                .padding(end = 16.dp)
                                .size(72.dp)
                        )
                        Column {
                            Text(
                                text = stringResource(R.string.app_name),
                                style = MaterialTheme.typography.titleLarge
                            )
                            Text(
                                text = mainViewModel.applicationVersionLabel,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.clickable {
                                    settingsViewModel.onAboutClick()
                                }
                            )
                        }
                    }
                    ChangelogButton(
                        updateAvailable = mainViewModel.updateAvailable
                    ) { scope.launch {
                        mainViewModel.updateSheetState.show()
                    } }
                }
            }, {
                val socialButtons: List<@Composable () -> Unit> = SettingsConstant.socials.map { social -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                uriHandler.openUri(social.url)
                            }
                            .padding(vertical = 8.dp)
                    ) {
                        Icon(
                            painter = when (val icon = social.icon) {
                                is Int -> painterResource(icon)
                                is ImageVector -> rememberVectorPainter(icon)
                                else -> throw IllegalArgumentException("unexpected class for social icon")
                            },
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(social.label)
                    }
                } }

                HorizontalSegmentor(
                    *socialButtons.toTypedArray(),
                    roundness = 0.dp
                )
            },
            itemContainerColor = Color.Transparent,
            modifier = Modifier.padding(horizontal = 12.dp)
        )

        ExpressiveSection(
            title = stringResource(R.string.settings_about_updates),
            modifier = Modifier.padding(top = 8.dp)
        ) {
            VerticalSegmentor(
                {
                    ExpressiveSwitchRow(
                        title = stringResource(R.string.settings_about_updates_autoCheckUpdates),
                        description = stringResource(R.string.settings_about_updates_autoCheckUpdates_description),
                        icon = { ExpressiveRowIcon(rememberVectorPainter(Icons.Rounded.Schedule)) },
                        checked = settingsViewModel.prefs.autoCheckUpdates.value
                    ) {
                        settingsViewModel.prefs.autoCheckUpdates.value = it
                    }
                },
                modifier = Modifier.padding(horizontal = 12.dp)
            )
        }

        ExpressiveSection(
            title = stringResource(R.string.settings_about_credits)
        ) {
            LaunchedEffect(Unit) {
                SettingsConstant.credits.forEach {
                    it.fetchAvatar()
                }
            }

            val creditsButtons: List<@Composable () -> Unit> = SettingsConstant.credits.map { credit -> {
                ExpressiveButtonRow(
                    title = credit.name,
                    description = credit.description,
                    icon = credit.avatarURL?.let { {
                        AsyncImage(
                            model = it,
                            contentDescription = null,
                            modifier = Modifier
                                .size(ROW_DEFAULT_ICON_SIZE)
                                .clip(CircleShape)
                        )
                    } } ?: {
                        ExpressiveRowIcon(
                            painter = rememberVectorPainter(Icons.Rounded.Face)
                        )
                    }
                ) {
                    credit.link?.let { uriHandler.openUri(it) }
                }
            } }

            VerticalSegmentor(
                *creditsButtons.toTypedArray(),
                {
                    ExpressiveButtonRow(
                        title = stringResource(R.string.settings_about_libs),
                        description = stringResource(R.string.settings_about_libs_description),
                        icon = { ExpressiveRowIcon(rememberVectorPainter(Icons.Rounded.Book)) },
                        trailingComponent = {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                contentDescription = null
                            )
                        },
                        onClick = onNavigateLibsRequest
                    )
                },
                modifier = Modifier.padding(horizontal = 12.dp)
            )
        }

        ExpressiveSection(
            title = stringResource(R.string.settings_about_other)
        ) {
            VerticalSegmentor(
                {
                    ExpressiveButtonRow(
                        title = stringResource(R.string.settings_about_other_copyDebugInfo),
                        description = stringResource(R.string.settings_about_other_copyDebugInfo_description),
                        icon = { ExpressiveRowIcon(rememberVectorPainter(Icons.Rounded.CopyAll)) }
                    ) {
                        scope.launch {
                            clipboard.setClipEntry(ClipEntry(ClipData.newPlainText(
                                context.getString(R.string.settings_about_other_copyDebugInfo_clipLabel),
                                mainViewModel.debugInfo
                            )))
                            settingsViewModel.topToastState.showToast(
                                text = R.string.settings_about_other_copyDebugInfo_copied,
                                icon = Icons.Rounded.CopyAll
                            )
                        }
                    }
                },
                modifier = Modifier.padding(horizontal = 12.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun ChangelogButton(
    updateAvailable: Boolean,
    onClick: () -> Unit
) {
    AnimatedContent(updateAvailable) {
        if (it) ElevatedButton(
            onClick = { onClick() },
            shapes = ButtonDefaults.shapes()
        ) {
            ButtonIcon(
                rememberVectorPainter(Icons.Rounded.Update)
            )
            Text(stringResource(R.string.settings_about_update))
        }
        else OutlinedButton(
            onClick = { onClick() },
            shapes = ButtonDefaults.shapes()
        ) {
            ButtonIcon(
                rememberVectorPainter(Icons.Rounded.Description)
            )
            Text(stringResource(R.string.settings_about_changelog))
        }
    }
}