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
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.aliernfrog.ensimanager.R

@Composable
fun TrustNewCertDialog(
    publicKey: String?,
    onTrust: () -> Unit,
    onDismissRequest: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            Button(
                onClick = onTrust
            ) {
                Text(stringResource(
                    if (publicKey != null) R.string.api_ssl_trust else R.string.api_profiles_add
                ))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismissRequest
            ) {
                Text(stringResource(R.string.action_cancel))
            }
        },
        icon = {
            Icon(
                imageVector = if (publicKey != null) Icons.Default.VerifiedUser else Icons.Default.Warning,
                contentDescription = null
            )
        },
        text = {
            Column(Modifier.verticalScroll(rememberScrollState())) {
                if (publicKey != null) {
                    Text(stringResource(R.string.api_ssl_trustNew_text))

                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                    ) {
                        Text(
                            text = publicKey,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.padding(8.dp)
                        )
                    }

                    Text(stringResource(R.string.api_ssl_trustNew_q))
                }
                else Text(
                    stringResource(R.string.api_ssl_trustNew_notSecure)+"\n"+stringResource(R.string.api_ssl_trustNew_notSecure_q)
                )
            }
        }
    )
}