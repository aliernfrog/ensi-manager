package com.aliernfrog.ensimanager.ui.component.api

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.stringResource
import com.aliernfrog.ensimanager.R
import com.aliernfrog.ensimanager.ui.component.ButtonIcon
import com.aliernfrog.ensimanager.ui.component.CardWithActions

@Composable
fun DecryptionCard(
    onDecryptRequest: () -> Unit,
    modifier: Modifier = Modifier,
    description: String = stringResource(R.string.api_crypto_decrypt_description)
) {
    CardWithActions(
        title = stringResource(R.string.api_crypto_decrypt),
        icon = rememberVectorPainter(Icons.Default.LockOpen),
        buttons = {
            Button(
                onClick = onDecryptRequest
            ) {
                ButtonIcon(rememberVectorPainter(Icons.AutoMirrored.Filled.ArrowForward))
                Text(stringResource(R.string.api_crypto_decrypt_do))
            }
        },
        modifier = modifier
    ) {
        Text(description)
    }
}