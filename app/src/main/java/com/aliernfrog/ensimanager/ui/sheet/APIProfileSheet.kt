package com.aliernfrog.ensimanager.ui.sheet

import android.annotation.SuppressLint
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Label
import androidx.compose.material.icons.filled.Api
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.aliernfrog.ensimanager.R
import com.aliernfrog.ensimanager.impl.api.APIProfile
import com.aliernfrog.ensimanager.ui.dialog.api.ssl.TrustNewCertDialog
import com.aliernfrog.ensimanager.ui.dialog.api.crypto.togglePasswordVisibilityText
import com.aliernfrog.ensimanager.util.extension.showErrorToast
import com.aliernfrog.ensimanager.util.extension.showSuccessToast
import com.aliernfrog.toptoast.state.TopToastState
import io.github.aliernfrog.shared.ui.component.AppModalBottomSheet
import io.github.aliernfrog.shared.ui.component.ButtonIcon
import io.github.aliernfrog.shared.ui.component.FadeVisibility
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun APIProfileSheet(
    sheetState: SheetState,
    topToastState: TopToastState,
    editingProfile: APIProfile?,
    existingProfiles: List<APIProfile>,
    onUpdateProfileRequest: (new: APIProfile) -> Unit,
    onAddProfileRequest: (new: APIProfile) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var name by rememberSaveable {
        mutableStateOf(editingProfile?.name ?: "")
    }
    var endpointsURL by rememberSaveable {
        mutableStateOf(editingProfile?.endpointsURL ?: "")
    }
    var authorization by rememberSaveable {
        mutableStateOf(editingProfile?.authorization ?: "")
    }
    var showAuthorization by rememberSaveable {
        mutableStateOf(false)
    }
    var sha256 by rememberSaveable {
        mutableStateOf(editingProfile?.trustedSha256 ?: "")
    }

    var fetching by rememberSaveable { mutableStateOf(false) }
    var trustNewCertDialogProfile by remember { mutableStateOf<APIProfile?>(null) }

    val isNameUnique = !existingProfiles.any {
        it.name == name
    }
    val isURLUnique = !existingProfiles.any {
        it.endpointsURL == endpointsURL
    }
    val isEndpointUnsecure by remember { derivedStateOf {
        endpointsURL.let {
            it.contains("://") && !it.startsWith("https://", ignoreCase = true)
        }
    } }
    val valid by remember { derivedStateOf {
        name.isNotEmpty() && endpointsURL.isNotEmpty() && isNameUnique && isURLUnique
    } }

    trustNewCertDialogProfile?.let { profile ->
        TrustNewCertDialog(
            publicKey = profile.endpoints?.sslPublicKey,
            onTrust = { scope.launch {
                val withKey = profile.copy(trustedSha256 = profile.endpoints?.sslPublicKey)
                if (editingProfile != null) onUpdateProfileRequest(withKey)
                else onAddProfileRequest(withKey)
                trustNewCertDialogProfile = null
                @SuppressLint("LocalContextGetResourceValueCall")
                topToastState.showSuccessToast(context.getString(R.string.api_profiles_add_saved), androidToast = true)
                sheetState.hide()
            } },
            onDismissRequest = { trustNewCertDialogProfile = null }
        )
    }

    AppModalBottomSheet(
        title = editingProfile?.name.let {
            if (it == null) stringResource(R.string.api_profiles_add)
            else stringResource(R.string.api_profiles_edit_title).replace("{NAME}", it)
        },
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text(stringResource(R.string.api_profiles_add_name)) },
                leadingIcon = {
                    Icon(Icons.AutoMirrored.Filled.Label, null)
                },
                supportingText = {
                    if (!isNameUnique) Text(stringResource(R.string.api_profiles_add_name_alreadyExists))
                },
                isError = !isNameUnique,
                readOnly = fetching,
                modifier = Modifier.animateContentSize().fillMaxWidth()
            )
            OutlinedTextField(
                value = endpointsURL,
                onValueChange = { endpointsURL = it },
                label = { Text(stringResource(R.string.api_profiles_add_endpointsURL)) },
                leadingIcon = {
                    Icon(Icons.Default.Api, null)
                },
                supportingText = {
                    Text(stringResource(
                        if (isURLUnique) R.string.api_profiles_add_endpointsURL_info
                        else R.string.api_profiles_add_endpointsURL_alreadyExists
                    ))
                },
                isError = !isURLUnique,
                singleLine = true,
                readOnly = fetching,
                modifier = Modifier.animateContentSize().fillMaxWidth()
            )
            OutlinedTextField(
                value = authorization,
                onValueChange = { authorization = it },
                label = { Text(stringResource(R.string.api_profiles_add_authorization)) },
                leadingIcon = {
                    Icon(Icons.Default.Key, null)
                },
                trailingIcon = {
                    IconButton(
                        onClick = {
                            showAuthorization = !showAuthorization
                        },
                        shapes = IconButtonDefaults.shapes()
                    ) {
                        Icon(
                            imageVector = if (showAuthorization) Icons.Rounded.VisibilityOff
                            else Icons.Rounded.Visibility,
                            contentDescription = togglePasswordVisibilityText(showAuthorization)
                        )
                    }
                },
                supportingText = { Text(stringResource(R.string.api_profiles_add_authorization_info)) },
                readOnly = fetching,
                visualTransformation = if (showAuthorization) VisualTransformation.None
                else PasswordVisualTransformation(),
                modifier = Modifier.animateContentSize().fillMaxWidth()
            )
            OutlinedTextField(
                value = sha256,
                onValueChange = { sha256 = it },
                label = { Text(stringResource(R.string.api_profiles_add_sha256)) },
                leadingIcon = {
                    Icon(Icons.Default.VerifiedUser, null)
                },
                supportingText = {
                    Text(stringResource(
                        if (isEndpointUnsecure) R.string.api_profiles_add_sha256_notHttps else R.string.api_profiles_add_sha256_info
                    ))
                },
                enabled = !isEndpointUnsecure,
                readOnly = fetching,
                modifier = Modifier.animateContentSize().fillMaxWidth()
            )

            FadeVisibility(
                visible = isEndpointUnsecure
            ) {
                Card(Modifier.fillMaxWidth()) {
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

            Crossfade(
                targetState = valid,
                modifier = Modifier
                    .padding(top = 8.dp)
                    .align(Alignment.End)
            ) { buttonEnabled ->
                Button(
                    enabled = buttonEnabled && !fetching,
                    shapes = ButtonDefaults.shapes(),
                    onClick = {
                        if (!valid) return@Button
                        val profile = APIProfile(
                            name = name,
                            endpointsURL = endpointsURL,
                            authorization = authorization,
                            trustedSha256 = sha256.ifBlank { null }
                        )
                        scope.launch {
                            fetching = true
                            profile.fetchAPIEndpoints()
                            if (profile.error != null) topToastState.showErrorToast(profile.error.orEmpty(), androidToast = true)
                            else trustNewCertDialogProfile = profile
                            fetching = false
                        }
                    }
                ) {
                    Box {
                        Row(Modifier.alpha(
                            if (fetching) 0f else 1f
                        )) {
                            ButtonIcon(rememberVectorPainter(Icons.Default.Check))
                            Text(stringResource(
                                if (editingProfile != null) R.string.api_profiles_add_save
                                else R.string.api_profiles_add
                            ))
                        }
                        if (fetching) CircularProgressIndicator(
                            strokeWidth = 2.dp,
                            modifier = Modifier
                                .size(18.dp)
                                .align(Alignment.Center)
                        )
                    }
                }
            }
        }
    }
}