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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aliernfrog.ensimanager.domain.APIState
import com.aliernfrog.ensimanager.util.staticutil.CryptoUtil
import com.aliernfrog.toptoast.state.TopToastState
import io.github.aliernfrog.shared.ui.component.VerticalSegmentor
import io.github.aliernfrog.shared.ui.component.expressive.ExpressiveButtonRow
import io.github.aliernfrog.shared.ui.component.expressive.ExpressiveSection
import org.koin.compose.koinInject

@Composable
fun ExperimentalOptions() {
    val apiState = koinInject<APIState>()
    val topToastState = koinInject<TopToastState>()

    val context = LocalContext.current
    val encryptedData = apiState.encryptedData.collectAsStateWithLifecycle().value

    ExpressiveSection(title = "Biometrics") {
        VerticalSegmentor(
            {
                ExpressiveButtonRow(
                    title = "Show biometric prompt",
                    enabled = apiState.biometricDecryptionSupportedByDevice
                ) {
                    apiState.showBiometricPrompt(
                        context = context,
                        forDecryption = false,
                        onSuccess = {
                            topToastState.showToast("Biometric prompt succeeded")
                        },
                        onFail = {
                            topToastState.showToast("Biometric prompt failed")
                        }
                    )
                }
            },
            {
                ExpressiveButtonRow(
                    title = "Biometric decryption supported by device: ${apiState.biometricDecryptionSupportedByDevice}",
                    enabled = false
                ) {}
            },
            {
                ExpressiveButtonRow(
                    title = "Can decrypt with biometrics: ${apiState.canDecryptWithBiometrics}",
                    enabled = false
                ) {}
            },
            {
                ExpressiveButtonRow(
                    title = "Biometric decryption enabled: ${apiState.biometricDecryptionEnabled}",
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
                    topToastState.showToast("Biometric key generated")
                }
            },
            {
                ExpressiveButtonRow(
                    title = "Delete biometric key",
                    description = "Biometric decryption will fail until re-encrypted with the new key!"
                ) {
                    CryptoUtil.deleteBiometricKey()
                    hasBiometricKey = CryptoUtil.hasBiometricKey()
                    topToastState.showToast("Biometric key deleted")
                }
            },
            {
                ExpressiveButtonRow(
                    title = "Password wrapped key",
                    description = encryptedData?.passwordWrappedKey ?: "null",
                    enabled = false
                ) {}
            },
            {
                ExpressiveButtonRow(
                    title = "Biometric wrapped key",
                    description = encryptedData?.biometricWrappedKey ?: "null",
                    enabled = false
                ) {}
            },
            modifier = Modifier.padding(horizontal = 12.dp)
        )
    }
}