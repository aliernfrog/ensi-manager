package com.aliernfrog.ensimanager.ui.screen.settings

import android.util.Log
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.Fingerprint
import androidx.compose.material.icons.rounded.Password
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
import com.aliernfrog.ensimanager.ui.component.api.DecryptionCard
import com.aliernfrog.ensimanager.ui.viewmodel.settings.APISettingsViewModel
import com.aliernfrog.ensimanager.util.extension.showSuccessToast
import com.aliernfrog.ensimanager.util.staticutil.CryptoUtil
import io.github.aliernfrog.shared.ui.component.FadeVisibility
import io.github.aliernfrog.shared.ui.component.VerticalSegmentor
import io.github.aliernfrog.shared.ui.component.expressive.ExpressiveButtonRow
import io.github.aliernfrog.shared.ui.component.expressive.ExpressiveRowIcon
import io.github.aliernfrog.shared.ui.component.expressive.ExpressiveSwitchRow
import io.github.aliernfrog.shared.ui.component.expressive.toRowFriendlyColor
import io.github.aliernfrog.shared.ui.screen.settings.SettingsPageContainer
import io.github.aliernfrog.shared.ui.theme.AppRoundnessSize
import org.koin.androidx.compose.koinViewModel

@Composable
fun SecurityPage(
    vm: APISettingsViewModel = koinViewModel(),
    onNavigateBackRequest: () -> Unit
) {
    val context = LocalContext.current
    val encryptionEnabled = vm.apiState.dataEncryptionEnabled
    val optionsEnabled = encryptionEnabled && vm.apiState.dataDecrypted

    SettingsPageContainer(
        title = stringResource(R.string.settings_security),
        onNavigateBackRequest = onNavigateBackRequest
    ) {
        FadeVisibility(!vm.apiState.dataDecrypted) {
            DecryptionCard(
                onDecryptRequest = { vm.apiState.showDecryptionDialog = true },
                modifier = Modifier.padding(vertical = 16.dp, horizontal = 12.dp),
                description = stringResource(R.string.settings_security_decryptFirst)
            )
        }

        ExpressiveSwitchRow(
            title = stringResource(R.string.settings_security_encryption),
            description = stringResource(R.string.settings_security_encryption_description),
            checked = encryptionEnabled,
            enabled = optionsEnabled || !encryptionEnabled,
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier
                .padding(vertical = 16.dp, horizontal = 12.dp)
                .clip(RoundedCornerShape(AppRoundnessSize))
        ) {
            if (encryptionEnabled) {
                vm.apiState.changeEncryptionPasswordAndSave(null)
                vm.topToastState.showSuccessToast(R.string.settings_security_encryption_disabledToast)
            }
            else vm.apiState.showEncryptionDialog = true
        }

        VerticalSegmentor(
            {
                ExpressiveButtonRow(
                    title = stringResource(R.string.settings_security_changePassword),
                    icon = {
                        ExpressiveRowIcon(
                            painter = rememberVectorPainter(Icons.Rounded.Password),
                            containerColor = Color.Red.toRowFriendlyColor
                        )
                    },
                    enabled = optionsEnabled,
                    trailingComponent = {
                        Icon(Icons.AutoMirrored.Rounded.KeyboardArrowRight, null)
                    }
                ) {
                    vm.apiState.showEncryptionDialog = true
                }
            },
            {
                ExpressiveSwitchRow(
                    title = stringResource(R.string.settings_security_biometrics),
                    description = stringResource(
                        if (vm.apiState.biometricDecryptionSupportedByDevice) R.string.settings_security_biometrics_description
                        else R.string.settings_security_biometrics_unsupported
                    ),
                    icon = {
                      ExpressiveRowIcon(
                          painter = rememberVectorPainter(Icons.Rounded.Fingerprint),
                          containerColor = Color.Green.toRowFriendlyColor
                      )
                    },
                    enabled = optionsEnabled && vm.apiState.biometricDecryptionSupportedByDevice,
                    checked = vm.apiState.biometricDecryptionEnabled,
                ) {
                    if (it) vm.apiState.showBiometricPrompt(
                        context = context,
                        forDecryption = false,
                        onSuccess = {
                            CryptoUtil.generateBiometricKey()
                            vm.apiState.biometricDecryptionEnabled = true
                            vm.apiState.saveProfiles()
                            vm.topToastState.showSuccessToast(R.string.settings_security_biometrics_enabledToast)
                        },
                        onFail = {
                            Log.d(TAG, "SecurityPage: biometric prompt failed")
                        }
                    ) else {
                        CryptoUtil.deleteBiometricKey()
                        vm.apiState.biometricDecryptionEnabled = false
                        vm.apiState.saveProfiles()
                        vm.topToastState.showSuccessToast(R.string.settings_security_biometrics_disabledToast)
                    }
                }
            },
            modifier = Modifier.padding(horizontal = 12.dp)
        )
    }
}