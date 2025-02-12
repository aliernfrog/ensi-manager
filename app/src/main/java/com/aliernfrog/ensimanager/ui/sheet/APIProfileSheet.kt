package com.aliernfrog.ensimanager.ui.sheet

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
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
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.aliernfrog.ensimanager.data.api.APIProfile
import com.aliernfrog.ensimanager.data.api.cache
import com.aliernfrog.ensimanager.data.api.id
import com.aliernfrog.ensimanager.ui.component.AppModalBottomSheet
import com.aliernfrog.ensimanager.ui.component.ButtonIcon
import com.aliernfrog.ensimanager.ui.dialog.api.ssl.TrustNewCertDialog
import com.aliernfrog.ensimanager.ui.viewmodel.APIViewModel
import com.aliernfrog.ensimanager.util.extension.showErrorToast
import com.aliernfrog.ensimanager.util.extension.showSuccessToast
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun APIProfileSheet(
    apiViewModel: APIViewModel = koinViewModel(),
    sheetState: SheetState = apiViewModel.profileSheetState
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var fetching by rememberSaveable { mutableStateOf(false) }
    var trustNewCertDialogProfile by remember { mutableStateOf<APIProfile?>(null) }

    val editingProfile = apiViewModel.profileSheetEditingProfile
    val isNameUnique = !apiViewModel.apiProfiles.any {
        it.name == apiViewModel.profileSheetName && it.id != editingProfile?.id
    }
    val isURLUnique = !apiViewModel.apiProfiles.any {
        it.endpointsURL == apiViewModel.profileSheetEndpointsURL && it.id != editingProfile?.id
    }
    val valid by remember { derivedStateOf {
        apiViewModel.profileSheetName.isNotEmpty() && apiViewModel.profileSheetEndpointsURL.isNotEmpty() && isNameUnique && isURLUnique
    } }

    trustNewCertDialogProfile?.let { profile ->
        val cache = profile.cache ?: return@let
        TrustNewCertDialog(
            publicKey = cache.endpoints?.sslPublicKey,
            onTrust = { scope.launch {
                val withKey = profile.copy(trustedSha256 = cache.endpoints?.sslPublicKey)
                if (editingProfile != null) apiViewModel.updateProfile(editingProfile, withKey)
                else apiViewModel.apiProfiles.add(withKey)
                trustNewCertDialogProfile = null
                apiViewModel.saveProfiles()
                apiViewModel.topToastState.showSuccessToast(context.getString(R.string.api_profiles_add_saved), androidToast = true)
                sheetState.hide()
                apiViewModel.clearProfileSheetState()
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
        Column(Modifier.padding(horizontal = 8.dp)) {
            OutlinedTextField(
                value = apiViewModel.profileSheetName,
                onValueChange = { apiViewModel.profileSheetName = it },
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
                value = apiViewModel.profileSheetEndpointsURL,
                onValueChange = { apiViewModel.profileSheetEndpointsURL = it },
                label = { Text(stringResource(R.string.api_profiles_add_endpointsURL)) },
                leadingIcon = {
                    Icon(Icons.Default.Api, null)
                },
                supportingText = {
                    Text(stringResource(
                        if (isNameUnique) R.string.api_profiles_add_endpointsURL_info
                        else R.string.api_profiles_add_endpointsURL_alreadyExists
                    ))
                },
                isError = !isURLUnique,
                singleLine = true,
                readOnly = fetching,
                modifier = Modifier.animateContentSize().fillMaxWidth()
            )
            OutlinedTextField(
                value = apiViewModel.profileSheetAuthorization,
                onValueChange = { apiViewModel.profileSheetAuthorization = it },
                label = { Text(stringResource(R.string.api_profiles_add_authorization)) },
                leadingIcon = {
                    Icon(Icons.Default.Key, null)
                },
                trailingIcon = {
                    IconButton(onClick = {
                        apiViewModel.profileSheetShowAuthorization = !apiViewModel.profileSheetShowAuthorization
                    }) {
                        Icon(
                            imageVector = if (apiViewModel.profileSheetShowAuthorization) Icons.Rounded.VisibilityOff
                            else Icons.Rounded.Visibility,
                            contentDescription = null
                        )
                    }
                },
                supportingText = { Text(stringResource(R.string.api_profiles_add_authorization_info)) },
                readOnly = fetching,
                visualTransformation = if (apiViewModel.profileSheetShowAuthorization) VisualTransformation.None
                else PasswordVisualTransformation(),
                modifier = Modifier.animateContentSize().fillMaxWidth()
            )
            OutlinedTextField(
                value = apiViewModel.profileSheetTrustedSha256,
                onValueChange = { apiViewModel.profileSheetTrustedSha256 = it },
                label = { Text(stringResource(R.string.api_profiles_add_sha256)) },
                leadingIcon = {
                    Icon(Icons.Default.VerifiedUser, null)
                },
                supportingText = {
                    Text(
                        stringResource(R.string.api_profiles_add_sha256_info) +
                                if (editingProfile != null) "" else stringResource(R.string.api_profiles_add_sha256_leaveEmpty)
                    )
                },
                readOnly = fetching,
                modifier = Modifier.animateContentSize().fillMaxWidth()
            )

            Crossfade(
                targetState = valid,
                modifier = Modifier
                    .padding(top = 8.dp)
                    .align(Alignment.End)
            ) { buttonEnabled ->
                Button(
                    enabled = buttonEnabled && !fetching,
                    onClick = {
                        if (!valid) return@Button
                        val profile = APIProfile(
                            name = apiViewModel.profileSheetName,
                            endpointsURL = apiViewModel.profileSheetEndpointsURL,
                            authorization = apiViewModel.profileSheetAuthorization
                        )
                        scope.launch {
                            fetching = true
                            apiViewModel.fetchAPIEndpoints(profile)
                            val error = apiViewModel.profileErrors[profile.id]
                            if (error != null) apiViewModel.topToastState.showErrorToast(error, androidToast = true)
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