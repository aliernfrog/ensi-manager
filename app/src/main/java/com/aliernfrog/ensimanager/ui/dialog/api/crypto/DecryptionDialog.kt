package com.aliernfrog.ensimanager.ui.dialog.api.crypto

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.aliernfrog.ensimanager.R

@Composable
fun DecryptionDialog(
    onDismissRequest: () -> Unit,
    onDecryptRequest: (password: String, onFinish: () -> Unit) -> Unit,
    onBiometricUnlockRequest: (() -> Unit)?,
    modifier: Modifier = Modifier
) {
    var password by rememberSaveable { mutableStateOf("") }
    var showPassword by rememberSaveable { mutableStateOf(false) }
    var decrypting by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        onBiometricUnlockRequest?.invoke()
    }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            Button(
                onClick = {
                    decrypting = true
                    onDecryptRequest(password) {
                        decrypting = false
                    }
                },
                enabled = !decrypting
            ) {
                Box {
                    Text(
                        text = stringResource(R.string.api_crypto_decrypt_do),
                        modifier = Modifier.alpha(if (decrypting) 0f else 1f)
                    )
                    if (decrypting) CircularProgressIndicator(
                        strokeWidth = 2.dp,
                        modifier = Modifier
                            .size(18.dp)
                            .align(Alignment.Center),
                    )
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismissRequest,
                enabled = !decrypting
            ) {
                Text(stringResource(R.string.action_cancel))
            }
        },
        icon = {
            Icon(
                imageVector = Icons.Default.LockOpen,
                contentDescription = null
            )
        },
        title = {
            Text(stringResource(R.string.api_crypto_decrypt))
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    enabled = !decrypting,
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        IconButton(onClick = {
                            showPassword = !showPassword
                        }) {
                            Icon(
                                imageVector = if (showPassword) Icons.Rounded.VisibilityOff else Icons.Rounded.Visibility,
                                contentDescription = togglePasswordVisibilityText(passwordVisible = showPassword)
                            )
                        }
                    }
                )

                onBiometricUnlockRequest?.let { onClick ->
                    Text(
                        text = stringResource(R.string.settings_security_biometrics),
                        modifier = Modifier
                            .alpha(if (decrypting) 0.7f else 1f)
                            .let {
                                if (decrypting) it
                                else it.clickable(onClick = onClick)
                            }
                    )
                }
            }
        },
        modifier = modifier
    )
}