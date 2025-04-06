package com.aliernfrog.ensimanager.ui.dialog.api

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
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
fun EncryptionDialog(
    onDismissRequest: () -> Unit,
    onEncryptRequest: (password: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var password by rememberSaveable { mutableStateOf("") }
    var showPassword by rememberSaveable { mutableStateOf(false) }
    var encrypting by rememberSaveable { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            Button(
                onClick = {
                    encrypting = true
                    onEncryptRequest(password)
                    onDismissRequest()
                },
                enabled = !encrypting
            ) {
                Box {
                    Text(
                        text = stringResource(R.string.api_profiles_encrypt_do),
                        modifier = Modifier.alpha(if (encrypting) 0f else 1f)
                    )
                    if (encrypting) CircularProgressIndicator(
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
                enabled = !encrypting
            ) {
                Text(stringResource(R.string.action_cancel))
            }
        },
        icon = {
          Icon(
              imageVector = Icons.Default.Lock,
              contentDescription = null
          )
        },
        title = {
            Text(stringResource(R.string.api_profiles_encrypt_setPassword))
        },
        text = {
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                enabled = !encrypting,
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
        },
        modifier = modifier
    )
}

@Composable
fun togglePasswordVisibilityText(passwordVisible: Boolean): String = stringResource(
    if (passwordVisible) R.string.action_password_hide else R.string.action_password_show
)