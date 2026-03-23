package com.aliernfrog.ensimanager.domain

import android.content.Context
import androidx.biometric.BiometricPrompt
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.Density
import androidx.fragment.app.FragmentActivity
import com.aliernfrog.ensimanager.R
import com.aliernfrog.ensimanager.impl.api.APIProfile
import com.aliernfrog.ensimanager.repository.APIProfileRepository
import com.aliernfrog.ensimanager.util.MainDestinationGroup
import com.aliernfrog.ensimanager.util.NavController
import com.aliernfrog.ensimanager.util.manager.PreferenceManager
import com.aliernfrog.ensimanager.util.staticutil.BiometricUtil
import com.aliernfrog.ensimanager.util.staticutil.EncryptedData
import com.aliernfrog.ensimanager.util.staticutil.WebUtil
import io.github.aliernfrog.shared.ui.component.createSheetStateWithDensity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.crypto.Cipher

@OptIn(ExperimentalMaterial3Api::class)
class APIState(
    private val apiProfileRepository: APIProfileRepository,
    private val appState: AppState,
    private val prefs: PreferenceManager,
    context: Context
) {
    val profileSwitcherSheetState = createSheetStateWithDensity(skipPartiallyExpanded = false, Density(context))

    val userAgent = WebUtil.buildUserAgent(context)
    val onProfileSwitchListeners = mutableListOf<(APIProfile?) -> Unit>()
    private var selectedDefaultProfile = false

    val apiProfiles: StateFlow<List<APIProfile>>
        get() = apiProfileRepository.apiProfiles

    val dataEncryptionEnabled: Boolean
        get() = apiProfileRepository.dataEncryptionEnabled

    val dataDecrypted: Boolean
        get() = apiProfileRepository.dataDecrypted

    val encryptedData: StateFlow<EncryptedData?>
        get() = apiProfileRepository.encryptedData

    val biometricDecryptionSupportedByDevice = apiProfileRepository.biometricDecryptionSupportedByDevice
    var canDecryptWithBiometrics by mutableStateOf(false)
    var biometricDecryptionEnabled: Boolean
        get() = prefs.biometricUnlockEnabled.value && biometricDecryptionSupportedByDevice
        set(value) { prefs.biometricUnlockEnabled.value = value }

    var showEncryptionDialog by mutableStateOf(false)
    var showDecryptionDialog by mutableStateOf(false)

    private var _chosenProfile by mutableStateOf<APIProfile?>(null)
    var chosenProfile: APIProfile?
        get() = _chosenProfile
        set(value) {
            _chosenProfile = value
            value?.availableDestinations?.let { availableDestinations ->
                val isCurrentMainDestinationAvailable = availableDestinations.contains(appState.navController.currentMainDestination)
                if (!isCurrentMainDestinationAvailable) availableDestinations.firstOrNull()?.let { firstAvailableDestination ->
                    appState.navController.currentMainDestination = firstAvailableDestination
                    appState.navController.navigateToMainDestination(clearBackStack = true)
                } ?: {
                    appState.navController.add(NavController.INITIAL_DESTINATION, clearBackStack = true)
                }
                else if (!appState.navController.backStack.contains(MainDestinationGroup))
                    appState.navController.navigateToMainDestination(clearBackStack = true)
            }
            if (prefs.rememberLastSelectedAPIProfile.value) {
                prefs.defaultAPIProfileIndex.value = apiProfiles.value.indexOfFirst {
                    it.id == value?.id
                }
            }
            onProfileSwitchListeners.forEach {
                it(value)
            }
        }

    fun loadProfiles(context: Context) {
        val res = apiProfileRepository.loadAPIProfiles(context)
        if (res is APIProfileRepository.APIProfilesLoadResult.Encrypted) {
            canDecryptWithBiometrics = res.encryptedData.biometricWrappedKey != null && biometricDecryptionEnabled
            showDecryptionDialog = true
        }
    }

    suspend fun refetchAllProfiles() = withContext(Dispatchers.IO) {
        apiProfiles.value.map {
            async {
                it.fetchAPIEndpoints()
            }
        }.awaitAll()
        launch(Dispatchers.Main) {
            selectDefaultProfileIfNeeded()
        }
    }

    private fun selectDefaultProfileIfNeeded() {
        if (apiProfiles.value.isEmpty() || selectedDefaultProfile) return
        selectedDefaultProfile = true

        val defaultIndex = prefs.defaultAPIProfileIndex.value
        if (defaultIndex < 0) return
        val profile = apiProfiles.value.elementAtOrNull(defaultIndex)
        if (profile?.isAvailable == true) chosenProfile = profile
    }

    fun addProfile(profile: APIProfile) = apiProfileRepository.addProfile(profile)

    fun updateProfile(id: String, new: APIProfile) = apiProfileRepository.updateProfile(
        id = id,
        new = new
    )

    fun deleteProfile(id: String) = apiProfileRepository.deleteProfile(id = id)

    fun saveProfiles() = apiProfileRepository.saveProfiles()

    fun decryptDataWithPassword(password: String): List<APIProfile>? {
        return when (
            val result = apiProfileRepository.decryptDataWithPassword(
                password = password
            )
        ) {
            is APIProfileRepository.APIProfilesLoadResult.Success -> result.apiProfiles
            else -> null
        }
    }

    fun decryptDataWithBiometrics(cipher: Cipher?): List<APIProfile>? {
        return when (
            val result = apiProfileRepository.decryptDataWithBiometrics(
                cipher = cipher
            )
        ) {
            is APIProfileRepository.APIProfilesLoadResult.Success -> result.apiProfiles
            else -> null
        }
    }

    fun changeEncryptionPasswordAndSave(password: String?) {
        apiProfileRepository.pendingEncryptionPassword = password
        if (password == null) apiProfileRepository.removeEncryptionData()
        apiProfileRepository.saveProfiles()
    }

    fun showBiometricPrompt(
        context: Context,
        forDecryption: Boolean,
        onSuccess: (BiometricPrompt.AuthenticationResult) -> Unit,
        onFail: () -> Unit
    ) {
        BiometricUtil.authenticate(
            activity = context as FragmentActivity,
            title = context.getString(
                if (forDecryption) R.string.api_crypto_decrypt_biometrics_prompt
                else R.string.settings_security_biometrics_prompt
            ),
            description = context.getString(R.string.api_crypto_decrypt_biometrics_description),
            onSuccess = onSuccess,
            onError = { _, _ -> onFail() },
            onFail = onFail
        )
    }
}