package com.aliernfrog.ensimanager.repository

import android.content.Context
import android.util.Log
import com.aliernfrog.ensimanager.R
import com.aliernfrog.ensimanager.TAG
import com.aliernfrog.ensimanager.impl.api.APIProfile
import com.aliernfrog.ensimanager.util.extension.showReportableErrorToast
import com.aliernfrog.ensimanager.util.manager.PreferenceManager
import com.aliernfrog.ensimanager.util.staticutil.BiometricUtil
import com.aliernfrog.ensimanager.util.staticutil.CryptoUtil
import com.aliernfrog.ensimanager.util.staticutil.EncryptedData
import com.aliernfrog.toptoast.state.TopToastState
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.crypto.Cipher
import javax.crypto.SecretKey

class APIProfileRepository(
    private val prefs: PreferenceManager,
    private val gson: Gson,
    private val topToastState: TopToastState,
    context: Context
) {
    private val _apiProfiles = MutableStateFlow(listOf<APIProfile>())
    val apiProfiles = _apiProfiles.asStateFlow()

    private val _encryptedData = MutableStateFlow<EncryptedData?>(null)
    val encryptedData = _encryptedData.asStateFlow()

    var pendingEncryptionPassword: String? = null
    private var encryptionMasterKey: SecretKey? = null

    val dataEncryptionEnabled: Boolean
        get() = _encryptedData.value != null || pendingEncryptionPassword != null

    val dataDecrypted: Boolean
        get() = !dataEncryptionEnabled || encryptionMasterKey != null

    val biometricDecryptionSupportedByDevice = BiometricUtil.canAuthenticate(context)
    val biometricDecryptionEnabled: Boolean
        get() = prefs.biometricUnlockEnabled.value && biometricDecryptionSupportedByDevice

    fun loadAPIProfiles(context: Context): APIProfilesLoadResult {
        val profilesData = prefs.apiProfiles.value
        if (profilesData.isBlank()) return APIProfilesLoadResult.Success(_apiProfiles.value)
        return try {
            _apiProfiles.value = gson.fromJson(profilesData, Array<APIProfile>::class.java).toList()
            restoreLegacyProfile(context)
            APIProfilesLoadResult.Success(_apiProfiles.value)
        } catch (_: Exception) {
            try {
                // Likely encrypted data
                _encryptedData.value = gson.fromJson(profilesData, EncryptedData::class.java)
                APIProfilesLoadResult.Encrypted(_encryptedData.value!!)
            } catch (e: Exception) {
                // Broken data
                Log.e(TAG, "APIProfileRepository/loadAPIProfiles: failed to load saved profiles", e)
                topToastState.showReportableErrorToast(R.string.api_profiles_restoreError, e)
                APIProfilesLoadResult.Error
            }
        }
    }

    fun decryptDataWithPassword(password: String): APIProfilesLoadResult {
        try {
            _encryptedData.value!!.let {
                val decryptResult = CryptoUtil.decryptWithPassword(it, password)
                val array = gson.fromJson(decryptResult.decryptedData, Array<APIProfile>::class.java)
                encryptionMasterKey = decryptResult.masterKey
                _apiProfiles.value = array.toList()
                return APIProfilesLoadResult.Success(_apiProfiles.value)
            }
        } catch (e: Exception) {
            topToastState.showReportableErrorToast(R.string.api_crypto_decrypt_fail, e)
            Log.e(TAG, "APIProfileRepository/decryptDataWithPassword: failed to decrypt API profiles", e)
            return APIProfilesLoadResult.Error
        }
    }

    fun decryptDataWithBiometrics(cipher: Cipher?): APIProfilesLoadResult {
        try {
            _encryptedData.value!!.let {
                val decryptResult = CryptoUtil.decryptWithBiometrics(cipher!!, it)
                val array = gson.fromJson(decryptResult.decryptedData, Array<APIProfile>::class.java)
                encryptionMasterKey = decryptResult.masterKey
                _apiProfiles.value = array.toList()
                return APIProfilesLoadResult.Success(_apiProfiles.value)
            }
        } catch (e: Exception) {
            // The data is most likely broken, ask for password just in case
            topToastState.showReportableErrorToast(R.string.api_crypto_decrypt_fail_biometrics, e)
            Log.e(TAG, "APIProfileRepository/decryptDataWithBiometrics: failed to decrypt API profiles", e)
            return APIProfilesLoadResult.Error
        }
    }

    fun addProfile(profile: APIProfile) {
        val profiles = _apiProfiles.value.toMutableList()
        profiles.add(profile)
        _apiProfiles.value = profiles
        saveProfiles()
    }

    fun updateProfile(id: String, new: APIProfile) {
        val profiles = _apiProfiles.value.toMutableList()
        val index = profiles.indexOfFirst { it.id == id }
        if (index < 0) return

        profiles[index] = new
        _apiProfiles.value = profiles
        saveProfiles()
    }

    fun deleteProfile(id: String) {
        val profiles = _apiProfiles.value
        _apiProfiles.value = profiles.filter { it.id != id }
        saveProfiles()
    }

    fun saveProfiles() {
        pendingEncryptionPassword.let { newPassword ->
            var json = gson.toJson(_apiProfiles.value)
            if (dataEncryptionEnabled) {
                if (newPassword != null) {
                    val encrypted = CryptoUtil.encryptWithPassword(
                        string = json,
                        password = newPassword,
                        withBiometrics = biometricDecryptionEnabled
                    )
                    json = gson.toJson(encrypted)
                } else if (encryptionMasterKey != null && _encryptedData.value != null) {
                    val encrypted = CryptoUtil.reencryptWithKey(
                        string = json,
                        masterKey = encryptionMasterKey!!,
                        reference = _encryptedData.value!!,
                        withBiometrics = biometricDecryptionEnabled
                    )
                    json = gson.toJson(encrypted)
                }
            }
            prefs.apiProfiles.value = json
        }
    }

    fun removeEncryptionData() {
        _encryptedData.value = null
    }

    @Suppress("DEPRECATION")
    fun restoreLegacyProfile(context: Context) {
        if (prefs.legacyAPIURL.value.isNotBlank()) {
            _apiProfiles.value = _apiProfiles.value.plus(APIProfile(
                name = context.getString(R.string.api_profiles_migratedFromV2),
                endpointsURL = prefs.legacyAPIURL.value,
                authorization = prefs.legacyAPIAuth.value
            ))
            prefs.legacyAPIURL.resetValue()
            saveProfiles()
        }
        if (prefs.legacyAPIAuth.value.isNotBlank()) prefs.legacyAPIAuth.resetValue()
    }

    sealed class APIProfilesLoadResult {
        data class Success(val apiProfiles: List<APIProfile>): APIProfilesLoadResult()
        data class Encrypted(val encryptedData: EncryptedData): APIProfilesLoadResult()
        data object Error: APIProfilesLoadResult()
    }
}