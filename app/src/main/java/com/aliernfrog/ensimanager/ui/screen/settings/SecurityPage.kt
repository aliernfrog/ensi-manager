package com.aliernfrog.ensimanager.ui.screen.settings

import android.util.Log
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Password
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.aliernfrog.ensimanager.R
import com.aliernfrog.ensimanager.TAG
import com.aliernfrog.ensimanager.ui.component.FadeVisibility
import com.aliernfrog.ensimanager.ui.component.SEGMENTOR_ROUNDNESS
import com.aliernfrog.ensimanager.ui.component.VerticalSegmentor
import com.aliernfrog.ensimanager.ui.component.api.DecryptionCard
import com.aliernfrog.ensimanager.ui.component.expressive.ExpressiveButtonRow
import com.aliernfrog.ensimanager.ui.component.expressive.ExpressiveSwitchRow
import com.aliernfrog.ensimanager.ui.component.expressive.toRowFriendlyColor
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
                modifier = Modifier.padding(vertical = 16.dp, horizontal = 12.dp),
                description = stringResource(R.string.settings_security_decryptFirst)
            )
        }

        ExpressiveSwitchRow(
            title = stringResource(R.string.settings_security_encryption),
            description = stringResource(R.string.settings_security_encryption_description),
            checked = encryptionEnabled,
            enabled = optionsEnabled || !encryptionEnabled,
            containerColor = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .padding(vertical = 16.dp, horizontal = 12.dp)
                .clip(RoundedCornerShape(SEGMENTOR_ROUNDNESS))
        ) {
            if (encryptionEnabled) {
                apiViewModel.changeEncryptionPasswordAndSave(null)
                apiViewModel.topToastState.showSuccessToast(R.string.settings_security_encryption_disabledToast)
            }
            else apiViewModel.showEncryptionDialog = true
        }

        VerticalSegmentor(
            {
                ExpressiveButtonRow(
                    title = stringResource(R.string.settings_security_changePassword),
                    painter = rememberVectorPainter(Icons.Default.Password),
                    iconContainerColor = Color.Red.toRowFriendlyColor,
                    enabled = optionsEnabled,
                    trailingComponent = {
                        Icon(Icons.AutoMirrored.Rounded.KeyboardArrowRight, null)
                    }
                ) {
                    apiViewModel.showEncryptionDialog = true
                }
            },
            {
                ExpressiveSwitchRow(
                    title = stringResource(R.string.settings_security_biometrics),
                    description = stringResource(
                        if (apiViewModel.biometricDecryptionSupported) R.string.settings_security_biometrics_description
                        else R.string.settings_security_biometrics_unsupported
                    ),
                    painter = rememberVectorPainter(Icons.Default.Fingerprint),
                    iconContainerColor = Color.Green.toRowFriendlyColor,
                    enabled = optionsEnabled && apiViewModel.biometricDecryptionSupported,
                    checked = apiViewModel.biometricDecryptionEnabled,
                ) {
                    if (it) apiViewModel.showBiometricPrompt(
                        context = context,
                        forDecryption = false,
                        onSuccess = {
                            CryptoUtil.generateBiometricKey()
                            apiViewModel.biometricDecryptionEnabled = true
                            apiViewModel.saveProfiles()
                            apiViewModel.topToastState.showSuccessToast(R.string.settings_security_biometrics_enabledToast)
                        },
                        onFail = {
                            Log.d(TAG, "SecurityPage: biometric prompt failed")
                        }
                    ) else {
                        CryptoUtil.deleteBiometricKey()
                        apiViewModel.biometricDecryptionEnabled = false
                        apiViewModel.saveProfiles()
                        apiViewModel.topToastState.showSuccessToast(R.string.settings_security_biometrics_disabledToast)
                    }
                }
            },
            modifier = Modifier.padding(horizontal = 12.dp)
        )
    }
}