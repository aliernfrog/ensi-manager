package com.aliernfrog.ensimanager.ui.screen.profiles

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Label
import androidx.compose.material.icons.filled.Api
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallExtendedFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aliernfrog.ensimanager.R
import com.aliernfrog.ensimanager.ui.component.api.ProfileIcon
import com.aliernfrog.ensimanager.ui.dialog.api.crypto.togglePasswordVisibilityText
import com.aliernfrog.ensimanager.ui.dialog.api.ssl.TrustNewCertDialog
import com.aliernfrog.ensimanager.ui.viewmodel.profiles.ProfileViewModel
import io.github.aliernfrog.shared.ui.component.AppScaffold
import io.github.aliernfrog.shared.ui.component.AppSmallTopBar
import io.github.aliernfrog.shared.ui.component.FadeVisibility
import io.github.aliernfrog.shared.ui.component.IconButtonWithTooltip
import io.github.aliernfrog.shared.ui.component.VerticalSegmentor
import io.github.aliernfrog.shared.ui.component.util.AnimatedVisibilityShadowWorkaround

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ProfileScreen(
    vm: ProfileViewModel,
    onNavigateBackRequest: () -> Unit
) {
    val existingProfiles = vm.apiState.apiProfiles.collectAsStateWithLifecycle().value

    val isNameUnique = !existingProfiles.any {
        it.name == vm.name && it.id != vm.editingProfile?.id
    }
    val isURLUnique = !existingProfiles.any {
        it.endpointsURL == vm.endpointsURL && it.id != vm.editingProfile?.id
    }
    val isEndpointUnsecure by remember { derivedStateOf {
        vm.endpointsURL.let {
            it.contains("://") && !it.startsWith("https://", ignoreCase = true)
        }
    } }
    val valid by remember { derivedStateOf {
        vm.name.isNotEmpty() && vm.endpointsURL.isNotEmpty() && isNameUnique && isURLUnique
    } }

    vm.certDialogProfile?.let { profile ->
        TrustNewCertDialog(
            publicKey = profile.endpoints?.sslPublicKey,
            onTrust = {
                vm.saveProfile(profile)
            },
            onDismissRequest = { vm.certDialogProfile = null }
        )
    }

    AppScaffold(
        topBar = { scrollBehavior ->
            AppSmallTopBar(
                title = vm.editingProfile?.let {
                    stringResource(R.string.profiles_edit_title).format(it.name)
                } ?: stringResource(R.string.profiles_add),
                scrollBehavior = scrollBehavior,
                onNavigationClick = onNavigateBackRequest
            )
        },
        floatingActionButton = {
            AnimatedVisibilityShadowWorkaround(
                visible = valid,
                modifier = Modifier.navigationBarsPadding()
            ) {
                SmallExtendedFloatingActionButton(
                    onClick = {
                        vm.saveProfile(null)
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Save,
                            contentDescription = null
                        )
                    },
                    text = {
                        Text(stringResource(R.string.profiles_add_save))
                    }
                )
            }
        },
        scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .navigationBarsPadding()
        ) {
            ProfileIcon(
                profileName = vm.name,
                model = vm.editingProfile?.endpoints?.metadata?.iconURL,
                containerColor = MaterialTheme.colorScheme.surfaceContainer,
                size = 150.dp,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(16.dp)
                    .clip(CircleShape)
            )

            FadeVisibility(
                visible = isEndpointUnsecure
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            modifier = Modifier.padding(end = 16.dp).size(32.dp)
                        )
                        Text(
                            text = stringResource(R.string.api_ssl_trustNew_notSecure),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            VerticalSegmentor(
                {
                    TextField(
                        value = vm.name,
                        onValueChange = { vm.name = it },
                        label = { Text(stringResource(R.string.profiles_add_name)) },
                        leadingIcon = {
                            Icon(Icons.AutoMirrored.Filled.Label, null)
                        },
                        supportingText = if (!isNameUnique) { {
                            Text(stringResource(R.string.profiles_add_name_alreadyExists))
                        } } else null,
                        isError = !isNameUnique,
                        readOnly = vm.fetching,
                        modifier = Modifier.animateContentSize().fillMaxWidth()
                    )
                },
                {
                    TextField(
                        value = vm.endpointsURL,
                        onValueChange = { vm.endpointsURL = it },
                        label = { Text(stringResource(R.string.profiles_add_endpointsURL)) },
                        leadingIcon = {
                            Icon(Icons.Default.Api, null)
                        },
                        supportingText = {
                            Text(stringResource(
                                if (isURLUnique) R.string.profiles_add_endpointsURL_info
                                else R.string.profiles_add_endpointsURL_alreadyExists
                            ))
                        },
                        isError = !isURLUnique,
                        singleLine = true,
                        readOnly = vm.fetching,
                        modifier = Modifier.animateContentSize().fillMaxWidth()
                    )
                },
                {
                    TextField(
                        value = vm.authorization,
                        onValueChange = { vm.authorization = it },
                        label = { Text(stringResource(R.string.profiles_add_authorization)) },
                        supportingText = { Text(stringResource(R.string.profiles_add_authorization_info)) },
                        trailingIcon = {
                            IconButtonWithTooltip(
                                icon = rememberVectorPainter(
                                    if (vm.showAuthorization) Icons.Rounded.VisibilityOff
                                    else Icons.Rounded.Visibility
                                ),
                                contentDescription = togglePasswordVisibilityText(vm.showAuthorization),
                                onClick = {
                                    vm.showAuthorization = !vm.showAuthorization
                                }
                            )
                        },
                        modifier = Modifier.animateContentSize().fillMaxWidth()
                    )
                },
                {
                    TextField(
                        value = vm.sha256,
                        onValueChange = { vm.sha256 = it },
                        label = { Text(stringResource(R.string.profiles_add_sha256)) },
                        leadingIcon = {
                            Icon(Icons.Default.VerifiedUser, null)
                        },
                        supportingText = {
                            Text(stringResource(
                                if (isEndpointUnsecure) R.string.profiles_add_sha256_notHttps else R.string.profiles_add_sha256_info
                            ))
                        },
                        enabled = !isEndpointUnsecure,
                        readOnly = vm.fetching,
                        modifier = Modifier.animateContentSize().fillMaxWidth()
                    )
                },
                modifier = Modifier.padding(horizontal = 12.dp)
            )
        }
    }
}