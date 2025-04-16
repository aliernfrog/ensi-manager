package com.aliernfrog.ensimanager.ui.viewmodel

import android.content.Context
import android.util.Log
import androidx.biometric.BiometricPrompt
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.Density
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aliernfrog.ensimanager.R
import com.aliernfrog.ensimanager.TAG
import com.aliernfrog.ensimanager.data.api.APIEndpoints
import com.aliernfrog.ensimanager.data.api.APIProfile
import com.aliernfrog.ensimanager.data.api.APIProfileCache
import com.aliernfrog.ensimanager.data.api.DEPRECATED_ENDPOINTS
import com.aliernfrog.ensimanager.data.api.cache
import com.aliernfrog.ensimanager.data.api.id
import com.aliernfrog.ensimanager.data.api.isAvailable
import com.aliernfrog.ensimanager.data.isSuccessful
import com.aliernfrog.ensimanager.data.summary
import com.aliernfrog.ensimanager.di.getKoinInstance
import com.aliernfrog.ensimanager.util.Destination
import com.aliernfrog.ensimanager.util.NavigationConstant
import com.aliernfrog.ensimanager.util.extension.set
import com.aliernfrog.ensimanager.util.extension.showErrorToast
import com.aliernfrog.ensimanager.util.manager.ContextUtils
import com.aliernfrog.ensimanager.util.manager.PreferenceManager
import com.aliernfrog.ensimanager.util.staticutil.BiometricUtil
import com.aliernfrog.ensimanager.util.staticutil.CryptoUtil
import com.aliernfrog.ensimanager.util.staticutil.EncryptedData
import com.aliernfrog.ensimanager.util.staticutil.WebUtil
import com.aliernfrog.toptoast.state.TopToastState
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import javax.crypto.Cipher
import javax.crypto.SecretKey

@OptIn(ExperimentalMaterial3Api::class)
class APIViewModel(
    val prefs: PreferenceManager,
    val topToastState: TopToastState,
    private val gson: Gson,
    private val contextUtils: ContextUtils,
    context: Context
) : ViewModel() {
    val profileSwitcherSheetState = SheetState(skipPartiallyExpanded = false, Density(context))
    val profileSheetState = SheetState(skipPartiallyExpanded = true, Density(context))

    val userAgent = WebUtil.buildUserAgent(context)
    val apiProfiles = mutableStateListOf<APIProfile>()
    val fetchingProfiles = mutableStateListOf<String>()
    val profileErrors = mutableStateMapOf<String, String>()
    val profileMigrations = mutableStateMapOf<String, String>()
    private val cache = mutableStateMapOf<String, APIProfileCache>()
    val onProfileSwitchListeners = mutableListOf<(APIProfile?) -> Unit>()

    var profileSheetEditingProfile by mutableStateOf<APIProfile?>(null)
        private set
    var profileSheetName by mutableStateOf("")
    var profileSheetEndpointsURL by mutableStateOf("")
    var profileSheetAuthorization by mutableStateOf("")
    var profileSheetTrustedSha256 by mutableStateOf("")
    var profileSheetShowAuthorization by mutableStateOf(false)

    var showEncryptionDialog by mutableStateOf(false)
    var showDecryptionDialog by mutableStateOf(false)
    var encryptedData by mutableStateOf<EncryptedData?>(null)
    private var newEncryptionPassword by mutableStateOf<String?>(null)
    private var encryptionMasterKey by mutableStateOf<SecretKey?>(null)
    val dataEncryptionEnabled by derivedStateOf {
        encryptedData != null || newEncryptionPassword != null
    }
    val dataDecrypted by derivedStateOf {
        !dataEncryptionEnabled || encryptionMasterKey != null || newEncryptionPassword != null
    }

    val biometricDecryptionSupported = BiometricUtil.canAuthenticate(context)
    var biometricDecryptionAvailable by mutableStateOf(false)
    var biometricDecryptionEnabled: Boolean
        get() = prefs.biometricUnlockEnabled.value && biometricDecryptionSupported
        set(value) { prefs.biometricUnlockEnabled.value = value }

    private var selectedDefaultProfile = false
    private var _chosenProfile by mutableStateOf<APIProfile?>(null)
    var chosenProfile: APIProfile?
        get() = _chosenProfile
        set(value) {
            _chosenProfile = value
            value?.cache?.availableDestinations?.let { availableDestinations ->
                val mainViewModel = getKoinInstance<MainViewModel>()
                val currentRoute = mainViewModel.navController?.currentDestination?.route
                val currentDestination = Destination.entries.find { it.route == currentRoute }
                val isScreenAvailable = availableDestinations.contains(currentDestination)
                if (!isScreenAvailable) availableDestinations.firstOrNull()?.let {
                    mainViewModel.navController?.set(it)
                } ?: mainViewModel.navController?.set(NavigationConstant.INITIAL_DESTINATION)
            }
            if (prefs.rememberLastSelectedAPIProfile.value) {
                prefs.defaultAPIProfileIndex.value = apiProfiles.indexOfFirst { it.id == value?.id }
            }
            onProfileSwitchListeners.forEach {
                it(value)
            }
        }

    var isChosenProfileFetching: Boolean
        get() = chosenProfile?.let { fetchingProfiles.contains(it.id) } ?: false
        set(value) {
            chosenProfile?.id?.let {
                if (value) fetchingProfiles.add(it) else fetchingProfiles.remove(it)
            }
        }
    val isConnected
        get() = chosenProfile?.isAvailable ?: false

    init {
        loadAPIProfiles()

        @Suppress("DEPRECATION")
        if (prefs.legacyAPIURL.value.isNotBlank()) {
            apiProfiles.add(APIProfile(
                name = context.getString(R.string.api_profiles_migratedFromV2),
                endpointsURL = prefs.legacyAPIURL.value,
                authorization = prefs.legacyAPIAuth.value
            ))
            prefs.legacyAPIURL.resetValue()
            saveProfiles()
        }

        @Suppress("DEPRECATION")
        if (prefs.legacyAPIAuth.value.isNotBlank()) prefs.legacyAPIAuth.resetValue()

        viewModelScope.launch {
            refetchAllProfiles()
        }
    }

    private fun loadAPIProfiles() {
        val profilesData = prefs.apiProfiles.value
        if (profilesData.isBlank()) return
        try {
            apiProfiles.addAll(
                gson.fromJson(profilesData, Array<APIProfile>::class.java)
            )
        } catch (_: Exception) {
            try {
                // Might be encrypted, ask user for password if so
                encryptedData = gson.fromJson(profilesData, EncryptedData::class.java)
                if (encryptedData?.biometricWrappedKey != null && biometricDecryptionEnabled) biometricDecryptionAvailable = true
                showDecryptionDialog = true
            } catch (e: Exception) {
                // Broken data
                Log.e(TAG, "Failed to restore saved profiles", e)
                topToastState.showErrorToast(R.string.api_profiles_restoreError)
            }
        }
    }

    fun decryptAPIProfiles(password: String): Array<APIProfile>? {
        try {
            encryptedData?.let {
                val decryptResult = CryptoUtil.decryptWithPassword(it, password)
                val array = gson.fromJson(decryptResult.decryptedData, Array<APIProfile>::class.java)
                encryptionMasterKey = decryptResult.masterKey
                apiProfiles.addAll(array)
                return array
            }
        } catch (e: Exception) {
            topToastState.showErrorToast(R.string.api_crypto_decrypt_fail, androidToast = true)
            showDecryptionDialog = true
            Log.e(TAG, "decryptAPIProfilesAndLoad: failed to decrypt API profiles", e)
        }
        return null
    }

    fun decryptAPIProfilesWithBiometrics(cipher: Cipher?): Array<APIProfile>? {
        try {
            encryptedData?.let {
                val decryptResult = CryptoUtil.decryptWithBiometrics(cipher!!, it)
                val array = gson.fromJson(decryptResult.decryptedData, Array<APIProfile>::class.java)
                encryptionMasterKey = decryptResult.masterKey
                apiProfiles.addAll(array)
                return array
            }
        } catch (e: Exception) {
            // The data is most likely broken, ask for password just in case
            topToastState.showErrorToast(R.string.api_crypto_decrypt_fail_biometrics, androidToast = true)
            showDecryptionDialog = true
            Log.e(TAG, "decryptAPIProfilesWithBiometricsAndLoad: failed to decrypt API profiles", e)
        }
        return null
    }

    private fun selectDefaultAPIProfile() {
        if (apiProfiles.isEmpty() || selectedDefaultProfile) return
        selectedDefaultProfile = true
        val defaultIndex = prefs.defaultAPIProfileIndex.value
        if (defaultIndex < 0) return
        val profile = apiProfiles.elementAtOrNull(defaultIndex)
        if (profile?.isAvailable == true) chosenProfile = profile
    }

    suspend fun refetchAllProfiles() {
        return coroutineScope {
            apiProfiles.map {
                async {
                    fetchAPIEndpoints(it)
                }
            }.awaitAll()
            selectDefaultAPIProfile()
        }
    }

    suspend fun fetchAPIEndpoints(profile: APIProfile): APIEndpoints? {
        fetchingProfiles.add(profile.id)
        val res = withContext(Dispatchers.IO) {
            try {
                val isAlreadySaved = apiProfiles.any { it.id == profile.id }
                val response = WebUtil.sendRequest(
                    toUrl = profile.endpointsURL,
                    method = "GET",
                    pinnedSha256 = profile.trustedSha256,
                    userAgent = userAgent
                )
                if (response.isSuccessful) {
                    val endpoints = response.responseBody.let { body ->
                        gson.fromJson(body, APIEndpoints::class.java)?.copy(
                            sslPublicKey = response.certSha256,
                            deprecatedEndpoints = findDeprecatedEndpoints(body.orEmpty())
                        )
                    }
                    val availableDestinations = Destination.entries.filter { dest ->
                        endpoints?.let {
                            dest.isAvailableInEndpoints?.invoke(it) != false
                        } == true
                    }
                    endpoints?.migration?.url?.let {
                        profileMigrations[profile.id] = it
                    } ?: {
                        profileMigrations.remove(profile.id)
                    }
                    cache[profile.id] = cache[profile.id]?.copy(
                        endpoints = endpoints,
                        availableDestinations = availableDestinations
                    ) ?: APIProfileCache(
                        endpoints = endpoints,
                        availableDestinations = availableDestinations
                    )
                    profileErrors.remove(profile.id)
                    return@withContext endpoints
                } else {
                    profileErrors[profile.id] = if (response.error != WebUtil.SEND_REQUEST_SHA256_UNMATCH_ERROR) response.summary
                    else contextUtils.getString(
                        if (isAlreadySaved) R.string.api_profiles_sha256fail else R.string.api_profiles_sha256fail_unsaved
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "fetchAPIEndpoints: failed to fetch endpoints for ${profile.id}", e)
                profileErrors[profile.id] = e.toString()
            }
            return@withContext null
        }
        fetchingProfiles.remove(profile.id)
        return res
    }

    private fun findDeprecatedEndpoints(jsonString: String): Map<String, String> = try {
        val json = JSONObject(jsonString)
        DEPRECATED_ENDPOINTS.filter { (old, new) ->
            json.has(old) && !json.has(new)
        }
    } catch (_: Exception) {
        emptyMap()
    }

    fun changeEncryptionPasswordAndSave(password: String?) {
        newEncryptionPassword = password
        if (password == null) encryptedData = null
        saveProfiles()
    }

    fun getProfileCache(profile: APIProfile): APIProfileCache? {
        return cache[profile.id]
    }

    fun updateProfile(old: APIProfile, new: APIProfile) {
        val index = apiProfiles.indexOf(old)
        apiProfiles[index] = new
    }

    fun saveProfiles() {
        newEncryptionPassword.let { newPassword ->
            var json = gson.toJson(apiProfiles)
            if (dataEncryptionEnabled) {
                if (newPassword != null) {
                    val encrypted = CryptoUtil.encryptWithPassword(json, newPassword, withBiometrics = biometricDecryptionEnabled)
                    json = gson.toJson(encrypted)
                } else if (encryptionMasterKey != null && encryptedData != null) {
                    val encrypted = CryptoUtil.reencryptWithKey(json, encryptionMasterKey!!, encryptedData!!, withBiometrics = biometricDecryptionEnabled)
                    json = gson.toJson(encrypted)
                }
            }
            prefs.apiProfiles.value = json
        }
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

    suspend fun openProfileSheetToAddNew() {
        clearProfileSheetState()
        profileSheetState.show()
    }

    suspend fun openProfileSheetToEdit(profile: APIProfile) {
        clearProfileSheetState()
        profileSheetEditingProfile = profile
        profileSheetName = profile.name
        profileSheetEndpointsURL = profile.endpointsURL
        profileSheetAuthorization = profile.authorization
        profileSheetTrustedSha256 = profile.trustedSha256.orEmpty()
        profileSheetState.show()
    }

    fun clearProfileSheetState() {
        profileSheetEditingProfile = null
        profileSheetName = ""
        profileSheetEndpointsURL = ""
        profileSheetAuthorization = ""
        profileSheetTrustedSha256 = ""
        profileSheetShowAuthorization = false
    }
}
