package com.aliernfrog.ensimanager.ui.dialog.api.ssl

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.aliernfrog.ensimanager.R
import com.aliernfrog.ensimanager.impl.api.APIProfile

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CertConfirmationDialog(
    profile: APIProfile,
    onTrust: () -> Unit,
    onDismissRequest: () -> Unit
) {
    val hasKeyAndNoError = profile.trustedSha256 != null && profile.error == null

    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            Button(
                onClick = onTrust,
                shapes = ButtonDefaults.shapes()
            ) {
                Text(stringResource(
                    if (hasKeyAndNoError) R.string.api_ssl_trust else R.string.profiles_add_save
                ))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismissRequest,
                shapes = ButtonDefaults.shapes()
            ) {
                Text(stringResource(R.string.action_cancel))
            }
        },
        icon = {
            Icon(
                imageVector = if (hasKeyAndNoError) Icons.Default.VerifiedUser else Icons.Default.Warning,
                contentDescription = null
            )
        },
        text = {
            Column(Modifier.verticalScroll(rememberScrollState())) {
                if (hasKeyAndNoError) {
                    Text(stringResource(R.string.api_ssl_trustNew_text))

                    Codeblock(profile.trustedSha256)

                    Text(stringResource(R.string.api_ssl_trustNew_q))
                }
                else if (profile.error != null) {
                    Text(stringResource(R.string.api_ssl_trustNew_error))

                    Codeblock(profile.error.orEmpty())

                    Text(stringResource(R.string.api_ssl_trustNew_notSecure_q))
                }
                else Text(
                    stringResource(R.string.api_ssl_trustNew_notSecure)
                            +"\n"+ stringResource(R.string.api_ssl_trustNew_notSecure_q)
                )
            }
        }
    )
}

@Composable
private fun Codeblock(text: String) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
    ) {
        Text(
            text = text,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.padding(8.dp)
        )
    }
}