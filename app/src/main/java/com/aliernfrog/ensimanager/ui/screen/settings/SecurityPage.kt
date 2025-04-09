package com.aliernfrog.ensimanager.ui.screen.settings

import android.util.Log
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Password
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.aliernfrog.ensimanager.R
import com.aliernfrog.ensimanager.TAG
import com.aliernfrog.ensimanager.ui.component.FadeVisibility
import com.aliernfrog.ensimanager.ui.component.api.DecryptionCard
import com.aliernfrog.ensimanager.ui.component.form.ButtonRow
import com.aliernfrog.ensimanager.ui.component.form.SwitchRow
import com.aliernfrog.ensimanager.ui.theme.AppComponentShape
import com.aliernfrog.ensimanager.ui.viewmodel.APIViewModel
import com.aliernfrog.ensimanager.util.extension.showSuccessToast
import com.aliernfrog.ensimanager.util.staticutil.CryptoUtil
import org.koin.androidx.compose.koinViewModel

@Composable
fun SecurityPage(
    apiViewModel: APIViewModel = koinViewModel(),
    onNavigateBackRequest: () -> Unit
) {
    val context = LocalContext.current

    val encryptionEnabled = apiViewModel.dataEncryptionEnabled

    val optionsEnabled = encryptionEnabled && apiViewModel.dataDecrypted

    SettingsPageContainer(
        title = stringResource(R.string.settings_security),
        onNavigateBackRequest = onNavigateBackRequest
    ) {
        FadeVisibility(!apiViewModel.dataDecrypted) {
            DecryptionCard(
                onDecryptRequest = { apiViewModel.showDecryptionDialog = true },
                modifier = Modifier.padding(16.dp),
                description = stringResource(R.string.settings_security_decryptFirst)
            )
        }

        SwitchRow(
            title = stringResource(R.string.settings_security_encryption),
            description = stringResource(R.string.settings_security_encryption_description),
            checked = encryptionEnabled,
            enabled = optionsEnabled || !encryptionEnabled,
            shape = AppComponentShape,
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.padding(16.dp)
        ) {
            if (encryptionEnabled) apiViewModel.changeEncryptionPasswordAndSave(null)
            else apiViewModel.showEncryptionDialog = true
        }

        ButtonRow(
            title = stringResource(R.string.settings_security_changePassword),
            painter = rememberVectorPainter(Icons.Default.Password),
            enabled = optionsEnabled
        ) {
            apiViewModel.showEncryptionDialog = true
        }

        SwitchRow(
            title = stringResource(R.string.settings_security_biometrics),
            description = stringResource(
                if (apiViewModel.biometricUnlockSupported) R.string.settings_security_biometrics_description
                else R.string.settings_security_biometrics_unsupported
            ),
            painter = rememberVectorPainter(Icons.Default.Fingerprint),
            enabled = optionsEnabled && apiViewModel.biometricUnlockSupported,
            checked = apiViewModel.biometricUnlockEnabled,
        ) {
            if (it) apiViewModel.showBiometricPrompt(
                context = context,
                forDecryption = false,
                onSuccess = {
                    if (!CryptoUtil.hasBiometricKey()) CryptoUtil.generateBiometricKey()
                    apiViewModel.biometricUnlockEnabled = true
                    apiViewModel.saveProfiles()
                    apiViewModel.topToastState.showSuccessToast(R.string.settings_security_biometrics_enabled)
                },
                onFail = {
                    Log.d(TAG, "SecurityPage: biometric unlock failed")
                }
            ) else {
                apiViewModel.biometricUnlockEnabled = false
                apiViewModel.saveProfiles()
            }
        }
    }
}