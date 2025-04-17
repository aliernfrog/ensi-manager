package com.aliernfrog.ensimanager.ui.screen

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.SpeakerNotes
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Api
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter
import com.aliernfrog.ensimanager.R
import com.aliernfrog.ensimanager.data.api.APIProfile
import com.aliernfrog.ensimanager.data.api.cache
import com.aliernfrog.ensimanager.data.api.id
import com.aliernfrog.ensimanager.data.api.isAvailable
import com.aliernfrog.ensimanager.ui.component.AppScaffold
import com.aliernfrog.ensimanager.ui.component.AppSmallTopBar
import com.aliernfrog.ensimanager.ui.component.ButtonIcon
import com.aliernfrog.ensimanager.ui.component.CardWithActions
import com.aliernfrog.ensimanager.ui.component.FloatingActionButton
import com.aliernfrog.ensimanager.ui.component.SettingsButton
import com.aliernfrog.ensimanager.ui.component.TextWithIcon
import com.aliernfrog.ensimanager.ui.component.api.DecryptionCard
import com.aliernfrog.ensimanager.ui.component.form.FormHeader
import com.aliernfrog.ensimanager.ui.dialog.DeleteConfirmationDialog
import com.aliernfrog.ensimanager.ui.sheet.APIProfileSheet
import com.aliernfrog.ensimanager.ui.theme.AppComponentShape
import com.aliernfrog.ensimanager.ui.theme.AppFABPadding
import com.aliernfrog.ensimanager.ui.viewmodel.APIViewModel
import com.aliernfrog.ensimanager.util.extension.horizontalFadingEdge
import com.aliernfrog.ensimanager.util.extension.showSuccessToast
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun APIGate(
    apiViewModel: APIViewModel = koinViewModel(),
    onNavigateSettingsRequest: () -> Unit,
    content: @Composable () -> Unit
) {
    AnimatedContent(targetState = !apiViewModel.isConnected) { showAPIConfiguration ->
        if (showAPIConfiguration) APIProfilesScreen(
            onNavigateSettingsRequest = onNavigateSettingsRequest
        )
        else content()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun APIProfilesScreen(
    apiViewModel: APIViewModel = koinViewModel(),
    onNavigateSettingsRequest: (() -> Unit)?,
    onNavigateBackRequest: (() -> Unit)? = null
) {
    val scope = rememberCoroutineScope()

    AppScaffold(
        topBar = {
            AppSmallTopBar(
                title = stringResource(R.string.api_profiles),
                scrollBehavior = it,
                onNavigationClick = onNavigateBackRequest,
                actions = {
                    IconButton(
                        onClick = { scope.launch {
                            apiViewModel.refetchAllProfiles()
                        } },
                        enabled = apiViewModel.fetchingProfiles.isEmpty()
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
            AnimatedVisibility(apiViewModel.apiProfiles.isNotEmpty()) {
                FloatingActionButton(
                    icon = Icons.Default.Add
                ) { scope.launch {
                    apiViewModel.openProfileSheetToAddNew()
                } }
            }
        },
        scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    ) {
        LazyColumn(Modifier.fillMaxSize()) {
            if (apiViewModel.dataEncryptionEnabled && !apiViewModel.dataDecrypted) item {
                DecryptionCard(
                    onDecryptRequest = {
                        apiViewModel.showDecryptionDialog = true
                    },
                    modifier = Modifier.fillMaxWidth().padding(8.dp)
                )
            } else if (apiViewModel.apiProfiles.isEmpty()) item {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp)
                ) {
                    Text(
                        text = stringResource(R.string.api_profiles_empty),
                        textAlign = TextAlign.Center
                    )
                    Button(
                        onClick = { scope.launch {
                            apiViewModel.openProfileSheetToAddNew()
                        } }
                    ) {
                        ButtonIcon(rememberVectorPainter(Icons.Default.Add))
                        Text(stringResource(R.string.api_profiles_add))
                    }
                }
            } else if (!apiViewModel.dataEncryptionEnabled && !apiViewModel.prefs.encryptionSuggestionDismissed.value) item {
                EncryptionCard(
                    onDismissRequest = {
                        apiViewModel.prefs.encryptionSuggestionDismissed.value = true
                    },
                    onEncryptRequest = {
                        apiViewModel.showEncryptionDialog = true
                    },
                    modifier = Modifier.fillMaxWidth().padding(8.dp)
                )
            }

            items(apiViewModel.apiProfiles) { profile ->
                ProfileCard(profile)
            }

            item {
                Spacer(Modifier.navigationBarsPadding().height(AppFABPadding))
            }
        }
    }

    APIProfileSheet()
}

@Composable
private fun ProfileCard(
    profile: APIProfile,
    modifier: Modifier = Modifier,
    apiViewModel: APIViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val layoutDirection = LocalLayoutDirection.current
    val scope = rememberCoroutineScope()
    
    val profileCache = profile.cache
    val fetching = apiViewModel.fetchingProfiles.contains(profile.id)
    val error = apiViewModel.profileErrors[profile.id]
    val migratedTo = apiViewModel.profileMigrations[profile.id]
    val clickable = profile.isAvailable

    var showDeleteConfirmation by remember { mutableStateOf(false) }

    if (showDeleteConfirmation) DeleteConfirmationDialog(
        toDelete = profile.name,
        onDismissRequest = { showDeleteConfirmation = false },
        onConfirm = {
            apiViewModel.apiProfiles.remove(profile)
            apiViewModel.saveProfiles()
            apiViewModel.topToastState.showSuccessToast(
                context.getString(R.string.api_profiles_delete_deleted).replace("{NAME}", profile.name)
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
                if (clickable) Modifier.clickable { apiViewModel.chosenProfile = profile }
                else Modifier
            ),
        shape = AppComponentShape
    ) {
        Row (
            modifier = Modifier.fillMaxWidth().padding(
                horizontal = 16.dp,
                vertical = 8.dp
            )
        ) {
            FormHeader(
                title = profile.name,
                description = profileCache?.endpoints?.metadata?.name?.let {
                    if (it != profile.name) it else null
                },
                iconColorFilter = null,
                iconSize = 56.dp,
                painter = profileCache?.endpoints?.metadata?.iconURL?.let {
                    rememberAsyncImagePainter(it)
                } ?:  rememberVectorPainter(Icons.Default.Api),
                modifier = Modifier.weight(1f).fillMaxWidth()
            )
            if (fetching) CircularProgressIndicator()
            else if (profile.isAvailable) RadioButton(
                selected = apiViewModel.chosenProfile == profile,
                onClick = { apiViewModel.chosenProfile = profile }
            )
        }

        profileCache?.endpoints?.metadata?.summary?.let {
            TextWithIcon(
                text = it,
                icon = rememberVectorPainter(Icons.AutoMirrored.Filled.SpeakerNotes),
                modifier = Modifier.padding(
                    horizontal = 16.dp,
                    vertical = 8.dp
                )
            )
        }

        profileCache?.endpoints?.deprecatedEndpoints?.let {
            if (it.isNotEmpty()) TextWithIcon(
                text = stringResource(R.string.api_profiles_deprecations)+"\n"+
                        it.map { (old, new) -> "$old -> $new" }.joinToString("\n"),
                icon = rememberVectorPainter(Icons.Default.Warning),
                modifier = Modifier.padding(
                    horizontal = 16.dp,
                    vertical = 8.dp
                )
            )
        }

        migratedTo?.let { migratedURL ->
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                TextWithIcon(
                    text = stringResource(R.string.api_profiles_migrated).replace("{URL}", migratedURL),
                    icon = rememberVectorPainter(Icons.Default.MoveUp),
                    modifier = Modifier.weight(1f).fillMaxWidth().padding(
                        end = 8.dp
                    )
                )
                ElevatedButton(
                    onClick = {
                        val index = apiViewModel.apiProfiles.indexOf(profile)
                        if (index < 0) return@ElevatedButton
                        apiViewModel.apiProfiles[index] = profile.copy(
                            endpointsURL = migratedURL
                        )
                        apiViewModel.saveProfiles()
                    }
                ) {
                    Text(stringResource(R.string.api_profiles_migrated_migrate))
                }
            }
        }

        error?.let {
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
                colors = ButtonDefaults.outlinedButtonColors().copy(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                ButtonIcon(rememberVectorPainter(Icons.Default.Delete))
                Text(stringResource(R.string.api_profiles_delete))
            }
            Button(
                onClick = { scope.launch {
                    apiViewModel.openProfileSheetToEdit(profile)
                } }
            ) {
                ButtonIcon(rememberVectorPainter(Icons.Default.Edit))
                Text(stringResource(R.string.api_profiles_edit))
            }
        }
    }
}

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
                onClick = onDismissRequest
            ) {
                Text(stringResource(R.string.action_dismiss))
            }

            Button(
                onClick = onEncryptRequest
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