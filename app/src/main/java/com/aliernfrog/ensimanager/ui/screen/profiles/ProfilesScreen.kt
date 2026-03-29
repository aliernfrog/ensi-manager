package com.aliernfrog.ensimanager.ui.screen.profiles

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.SpeakerNotes
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EnhancedEncryption
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.MoveUp
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.aliernfrog.ensimanager.R
import com.aliernfrog.ensimanager.impl.api.APIProfile
import com.aliernfrog.ensimanager.ui.component.SettingsButton
import com.aliernfrog.ensimanager.ui.component.api.DecryptionCard
import com.aliernfrog.ensimanager.ui.component.api.ProfileIcon
import com.aliernfrog.ensimanager.ui.viewmodel.ProfilesViewModel
import com.aliernfrog.ensimanager.util.extension.showSuccessToast
import io.github.aliernfrog.shared.ui.component.AppScaffold
import io.github.aliernfrog.shared.ui.component.AppSmallTopBar
import io.github.aliernfrog.shared.ui.component.ButtonIcon
import io.github.aliernfrog.shared.ui.component.CardWithActions
import io.github.aliernfrog.shared.ui.component.FloatingActionButton
import io.github.aliernfrog.shared.ui.component.IconButtonWithTooltip
import io.github.aliernfrog.shared.ui.component.TextWithIcon
import io.github.aliernfrog.shared.ui.component.expressive.ExpressiveRowHeader
import io.github.aliernfrog.shared.ui.component.util.AnimatedVisibilityShadowWorkaround
import io.github.aliernfrog.shared.ui.dialog.DeleteConfirmationDialog
import io.github.aliernfrog.shared.ui.theme.AppComponentShape
import io.github.aliernfrog.shared.ui.theme.AppFABPadding
import io.github.aliernfrog.shared.util.extension.horizontalFadingEdge
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ProfilesScreen(
    vm: ProfilesViewModel = koinViewModel(),
    isInitialScreen: Boolean,
    onNavigateSettingsRequest: (() -> Unit)?,
    onNavigateBackRequest: (() -> Unit)? = null
) {
    val scope = rememberCoroutineScope()

    val apiProfiles = vm.apiProfiles.collectAsStateWithLifecycle().value
    val isAnyProfileFetching = apiProfiles.any { it.isFetching }

    LaunchedEffect(apiProfiles.size) {
        if (apiProfiles.any { it.endpoints == null })
            vm.apiState.refetchAllProfiles()
    }

    AppScaffold(
        topBar = {
            AppSmallTopBar(
                title = stringResource(
                    if (isInitialScreen) R.string.app_name
                    else R.string.profiles
                ),
                scrollBehavior = it,
                onNavigationClick = onNavigateBackRequest,
                actions = {
                    IconButton(
                        onClick = { scope.launch {
                            vm.apiState.refetchAllProfiles()
                        } },
                        shapes = IconButtonDefaults.shapes(),
                        enabled = !isAnyProfileFetching
                    ) {
                        Icon(Icons.Default.Refresh, null)
                    }

                    onNavigateSettingsRequest?.let { onClick ->
                        SettingsButton(
                            profileSwitcher = false,
                            onNavigateSettingsRequest = onClick
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            AnimatedVisibilityShadowWorkaround(apiProfiles.isNotEmpty()) {
                FloatingActionButton(
                    icon = Icons.Default.Add,
                    modifier = Modifier.navigationBarsPadding()
                ) {
                    vm.navigateToProfileScreen(null)
                }
            }
        },
        scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    ) {
        LazyColumn(Modifier.fillMaxSize()) {
            if (vm.apiState.dataEncryptionEnabled && !vm.apiState.dataDecrypted) item {
                DecryptionCard(
                    onDecryptRequest = {
                        vm.apiState.showDecryptionDialog = true
                    },
                    modifier = Modifier.fillMaxWidth().padding(8.dp)
                )
            } else if (apiProfiles.isEmpty()) item {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp)
                ) {
                    Text(
                        text = stringResource(R.string.profiles_empty),
                        textAlign = TextAlign.Center
                    )
                    Button(
                        onClick = { vm.navigateToProfileScreen(null) },
                        shapes = ButtonDefaults.shapes()
                    ) {
                        ButtonIcon(rememberVectorPainter(Icons.Default.Add))
                        Text(stringResource(R.string.profiles_add_save))
                    }
                }
            } else if (!vm.apiState.dataEncryptionEnabled && !vm.prefs.encryptionSuggestionDismissed.value) item {
                EncryptionCard(
                    onDismissRequest = {
                        vm.prefs.encryptionSuggestionDismissed.value = true
                    },
                    onEncryptRequest = {
                        vm.apiState.showEncryptionDialog = true
                    },
                    modifier = Modifier.fillMaxWidth().padding(8.dp)
                )
            }

            items(apiProfiles) { profile ->
                ProfileCard(vm, profile)
            }

            item {
                Spacer(Modifier.navigationBarsPadding().height(AppFABPadding))
            }
        }
    }
}


@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun ProfileCard(
    vm: ProfilesViewModel,
    profile: APIProfile,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val layoutDirection = LocalLayoutDirection.current
    val clickable = profile.isAvailable

    val profileColor = remember(profile) {
        Color(profile.color)
    }

    var showDeleteConfirmation by remember { mutableStateOf(false) }

    if (showDeleteConfirmation) DeleteConfirmationDialog(
        name = profile.name,
        onDismissRequest = { showDeleteConfirmation = false },
        onConfirmDelete = {
            vm.apiState.deleteProfile(profile.id)
            vm.topToastState.showSuccessToast(
                @SuppressLint("LocalContextGetResourceValueCall")
                context.getString(R.string.profiles_delete_deleted).replace("{NAME}", profile.name)
            )
            showDeleteConfirmation = false
        }
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clip(AppComponentShape)
            .then(
                if (clickable) Modifier.clickable {
                    vm.apiState.chosenProfile = profile
                } else Modifier
            ),
        shape = AppComponentShape
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(
                horizontal = 16.dp,
                vertical = 8.dp
            )
        ) {
            ExpressiveRowHeader(
                title = profile.name,
                description = profile.endpoints?.metadata?.name?.let {
                    if (it != profile.name) it else null
                },
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
                iconSize = 56.dp,
                modifier = Modifier.weight(1f).fillMaxWidth()
            )
            if (profile.isFetching) CircularProgressIndicator()
            else if (profile.isAvailable) RadioButton(
                selected = vm.apiState.chosenProfile == profile,
                onClick = { vm.apiState.chosenProfile = profile }
            ) else IconButtonWithTooltip(
                icon = rememberVectorPainter(Icons.Default.Refresh),
                contentDescription = stringResource(R.string.profiles_fetch),
                onClick = { vm.viewModelScope.launch {
                    profile.fetchAPIEndpoints()
                } }
            )
        }

        profile.endpoints?.metadata?.summary?.let {
            TextWithIcon(
                text = it,
                icon = rememberVectorPainter(Icons.AutoMirrored.Filled.SpeakerNotes),
                modifier = Modifier.padding(
                    horizontal = 16.dp,
                    vertical = 8.dp
                )
            )
        }

        profile.endpoints?.deprecatedEndpoints?.let {
            if (it.isNotEmpty()) TextWithIcon(
                text = stringResource(R.string.profiles_deprecations)+"\n"+
                        it.map { (old, new) -> "$old -> $new" }.joinToString("\n"),
                icon = rememberVectorPainter(Icons.Default.Warning),
                modifier = Modifier.padding(
                    horizontal = 16.dp,
                    vertical = 8.dp
                )
            )
        }

        profile.migratedTo?.let { migratedURL ->
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                TextWithIcon(
                    text = stringResource(R.string.profiles_migrated).replace("{URL}", migratedURL),
                    icon = rememberVectorPainter(Icons.Default.MoveUp),
                    modifier = Modifier.weight(1f).fillMaxWidth().padding(
                        end = 8.dp
                    )
                )
                ElevatedButton(
                    onClick = {
                        vm.apiState.updateProfile(
                            id = profile.id,
                            new = profile.copy(
                                endpointsURL = migratedURL
                            )
                        )
                    },
                    shapes = ButtonDefaults.shapes()
                ) {
                    Text(stringResource(R.string.profiles_migrated_migrate))
                }
            }
        }

        profile.error?.let {
            TextWithIcon(
                text = it,
                icon = rememberVectorPainter(Icons.Default.Error),
                modifier = Modifier.padding(
                    horizontal = 16.dp,
                    vertical = 8.dp
                )
            )
        }

        val rowScrollState = rememberScrollState()
        Row(
            modifier = Modifier
                .align(Alignment.End)
                .horizontalFadingEdge(
                    scrollState = rowScrollState,
                    edgeColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                    isRTL = layoutDirection == LayoutDirection.Rtl
                )
                .horizontalScroll(rowScrollState, reverseScrolling = true)
                .padding(
                    horizontal = 16.dp,
                    vertical = 8.dp
                ),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
        ) {
            OutlinedButton(
                onClick = { showDeleteConfirmation = true },
                shapes = ButtonDefaults.shapes(),
                colors = ButtonDefaults.outlinedButtonColors().copy(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                ButtonIcon(rememberVectorPainter(Icons.Default.Delete))
                Text(stringResource(R.string.profiles_delete))
            }
            Button(
                onClick = { vm.navigateToProfileScreen(profile) },
                shapes = ButtonDefaults.shapes()
            ) {
                ButtonIcon(rememberVectorPainter(Icons.Default.Edit))
                Text(stringResource(R.string.profiles_edit))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun EncryptionCard(
    onDismissRequest: () -> Unit,
    onEncryptRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    CardWithActions(
        title = stringResource(R.string.api_crypto_encrypt),
        icon = rememberVectorPainter(Icons.Default.EnhancedEncryption),
        buttons = {
            TextButton(
                onClick = onDismissRequest,
                shapes = ButtonDefaults.shapes()
            ) {
                Text(stringResource(R.string.action_dismiss))
            }

            Button(
                onClick = onEncryptRequest,
                shapes = ButtonDefaults.shapes()
            ) {
                ButtonIcon(rememberVectorPainter(Icons.AutoMirrored.Filled.ArrowForward))
                Text(stringResource(R.string.api_crypto_encrypt_do))
            }
        },
        modifier = modifier
    ) {
        Text(stringResource(R.string.api_crypto_encrypt_description))
    }
}