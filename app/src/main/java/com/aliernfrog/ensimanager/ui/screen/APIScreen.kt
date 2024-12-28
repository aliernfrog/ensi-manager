package com.aliernfrog.ensimanager.ui.screen

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.aliernfrog.ensimanager.R
import com.aliernfrog.ensimanager.data.api.id
import com.aliernfrog.ensimanager.ui.component.AppScaffold
import com.aliernfrog.ensimanager.ui.component.AppSmallTopBar
import com.aliernfrog.ensimanager.ui.component.ButtonIcon
import com.aliernfrog.ensimanager.ui.component.FloatingActionButton
import com.aliernfrog.ensimanager.ui.component.SettingsButton
import com.aliernfrog.ensimanager.ui.sheet.AddAPIProfileSheet
import com.aliernfrog.ensimanager.ui.theme.AppComponentShape
import com.aliernfrog.ensimanager.ui.theme.AppFABPadding
import com.aliernfrog.ensimanager.ui.viewmodel.APIViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun APIGate(
    apiViewModel: APIViewModel = koinViewModel(),
    onNavigateSettingsRequest: () -> Unit,
    content: @Composable () -> Unit
) {
    AnimatedContent(targetState = !apiViewModel.isReady) { showAPIConfiguration ->
        if (showAPIConfiguration) APIProfilesScreen(
            onNavigateSettingsRequest = onNavigateSettingsRequest
        )
        else content()
    }

    BackHandler(apiViewModel.fetching) {}
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
                onNavigationClick = if (apiViewModel.fetching) null else onNavigateBackRequest,
                actions = {
                    onNavigateSettingsRequest?.let { onClick ->
                        SettingsButton(
                            enabled = !apiViewModel.fetching,
                            onClick = onClick
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
                    apiViewModel.addProfileSheetState.show()
                } }
            }
        },
        scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    ) {
        LazyColumn(Modifier.fillMaxSize()) {
            if (apiViewModel.apiProfiles.isEmpty()) item {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp)
                ) {
                    Text(
                        text = stringResource(R.string.api_profiles_empty),
                        textAlign = TextAlign.Center
                    )
                    Button(
                        onClick = { scope.launch {
                            apiViewModel.addProfileSheetState.show()
                        } }
                    ) {
                        ButtonIcon(rememberVectorPainter(Icons.Default.Add))
                        Text(stringResource(R.string.api_profiles_add))
                    }
                }
            }

            items(apiViewModel.apiProfiles) { profile ->
                val fetching = apiViewModel.fetchingProfiles.contains(profile.id)
                val error = apiViewModel.profileErrors[profile.id]
                val migratedTo = apiViewModel.profileMigrations[profile.id]

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    shape = AppComponentShape
                ) {
                    Column(
                        Modifier.fillMaxWidth().padding(vertical = 16.dp, horizontal = 24.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (error != null) Icon(
                                imageVector = Icons.Default.Error,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier
                                    .size(50.dp)
                                    .clip(CircleShape)
                                    .padding(end = 8.dp)
                            ) else AsyncImage(
                                model = profile.iconModel,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(50.dp)
                                    .clip(CircleShape)
                                    .padding(end = 8.dp)
                            )
                            Text(profile.name)
                        }

                        AnimatedVisibility(fetching, Modifier.align(Alignment.CenterHorizontally)) {
                            CircularProgressIndicator()
                        }

                        error?.let {
                            Text(it)
                        }

                        migratedTo?.let {
                            Text(stringResource(R.string.api_profiles_migrated).replace("{URL}", it))
                        }
                    }
                }
            }

            item {
                Spacer(Modifier.navigationBarsPadding().height(AppFABPadding))
            }
        }
    }

    AddAPIProfileSheet()
}