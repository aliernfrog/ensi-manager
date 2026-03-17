package com.aliernfrog.ensimanager.ui.component.widget.settings

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.aliernfrog.ensimanager.ui.viewmodel.APIViewModel
import com.aliernfrog.ensimanager.util.staticutil.CryptoUtil
import io.github.aliernfrog.shared.ui.component.VerticalSegmentor
import io.github.aliernfrog.shared.ui.component.expressive.ExpressiveButtonRow
import io.github.aliernfrog.shared.ui.component.expressive.ExpressiveSection
import org.koin.androidx.compose.koinViewModel

@Composable
fun ExperimentalOptions(
    apiViewModel: APIViewModel = koinViewModel() // ?TODO better way to handle
) {
    val context = LocalContext.current

    ExpressiveSection(title = "Biometrics") {
        VerticalSegmentor(
            {
                ExpressiveButtonRow(
                    title = "Show biometric prompt",
                    enabled = apiViewModel.biometricDecryptionSupported
                ) {
                    apiViewModel.showBiometricPrompt(
                        context = context,
                        forDecryption = false,
                        onSuccess = {
                            apiViewModel.topToastState.showToast("Biometric prompt succeeded")
                        },
                        onFail = {
                            apiViewModel.topToastState.showToast("Biometric prompt failed")
                        }
                    )
                }
            },
            {
                ExpressiveButtonRow(
                    title = "Biometric decryption supported: ${apiViewModel.biometricDecryptionSupported}",
                    enabled = false
                ) {}
            },
            {
                ExpressiveButtonRow(
                    title = "Biometric decryption available: ${apiViewModel.biometricDecryptionAvailable}",
                    enabled = false
                ) {}
            },
            {
                ExpressiveButtonRow(
                    title = "Biometric decryption enabled: ${apiViewModel.biometricDecryptionEnabled}",
                    enabled = false
                ) { }
            },
            modifier = Modifier.padding(horizontal = 12.dp)
        )
    }

    ExpressiveSection(title = "Encryption") {
        var hasBiometricKey by remember { mutableStateOf(CryptoUtil.hasBiometricKey()) }

        VerticalSegmentor(
            {
                ExpressiveButtonRow(
                    title = "Has biometric key: $hasBiometricKey",
                    description = "Tap to update"
                ) {
                    hasBiometricKey = CryptoUtil.hasBiometricKey()
                }
            },
            {
                ExpressiveButtonRow(
                    title = "Generate biometric key",
                    description = "Biometric decryption will fail until re-encrypted with the new key!"
                ) {
                    CryptoUtil.generateBiometricKey()
                    hasBiometricKey = CryptoUtil.hasBiometricKey()
                    apiViewModel.topToastState.showToast("Biometric key generated")
                }
            },
            {
                ExpressiveButtonRow(
                    title = "Delete biometric key",
                    description = "Biometric decryption will fail until re-encrypted with the new key!"
                ) {
                    CryptoUtil.deleteBiometricKey()
                    hasBiometricKey = CryptoUtil.hasBiometricKey()
                    apiViewModel.topToastState.showToast("Biometric key deleted")
                }
            },
            {
                ExpressiveButtonRow(
                    title = "Password wrapped key",
                    description = apiViewModel.encryptedData?.passwordWrappedKey ?: "null",
                    enabled = false
                ) {}
            },
            {
                ExpressiveButtonRow(
                    title = "Biometric wrapped key",
                    description = apiViewModel.encryptedData?.biometricWrappedKey ?: "null",
                    enabled = false
                ) {}
            },
            modifier = Modifier.padding(horizontal = 12.dp)
        )
    }
}