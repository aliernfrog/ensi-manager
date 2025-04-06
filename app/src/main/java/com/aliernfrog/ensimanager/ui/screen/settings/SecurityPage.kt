package com.aliernfrog.ensimanager.ui.screen.settings

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Password
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.aliernfrog.ensimanager.R
import com.aliernfrog.ensimanager.ui.component.form.ButtonRow
import com.aliernfrog.ensimanager.ui.component.form.SwitchRow
import com.aliernfrog.ensimanager.ui.theme.AppComponentShape
import com.aliernfrog.ensimanager.ui.viewmodel.APIViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun SecurityPage(
    apiViewModel: APIViewModel = koinViewModel(),
    onNavigateBackRequest: () -> Unit
) {
    val dataEncrypted = apiViewModel.dataEncryptionEnabled

    SettingsPageContainer(
        title = stringResource(R.string.settings_security),
        onNavigateBackRequest = onNavigateBackRequest
    ) {
        SwitchRow(
            title = stringResource(R.string.settings_security_encryption),
            description = stringResource(R.string.settings_security_encryption_description),
            checked = dataEncrypted,
            shape = AppComponentShape,
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.padding(16.dp)
        ) {
            apiViewModel.showEncryptionDialog = true
        }

        ButtonRow(
            title = stringResource(R.string.settings_security_changePassword),
            painter = rememberVectorPainter(Icons.Default.Password),
            enabled = dataEncrypted
        ) {
            apiViewModel.showEncryptionDialog = true
        }

        SwitchRow(
            title = stringResource(R.string.settings_security_biometrics),
            painter = rememberVectorPainter(Icons.Default.Fingerprint),
            enabled = dataEncrypted,
            checked = false //TODO
        ) {
            /* TODO */
        }
    }
}