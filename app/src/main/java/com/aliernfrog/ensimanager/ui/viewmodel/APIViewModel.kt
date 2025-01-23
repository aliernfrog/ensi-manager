package com.aliernfrog.ensimanager.ui.viewmodel

import android.content.Context
import android.util.Log
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.unit.Density
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aliernfrog.ensimanager.R
import com.aliernfrog.ensimanager.TAG
import com.aliernfrog.ensimanager.data.api.APIEndpoints
import com.aliernfrog.ensimanager.data.api.APIProfile
import com.aliernfrog.ensimanager.data.api.APIProfileCache
import com.aliernfrog.ensimanager.data.api.id
import com.aliernfrog.ensimanager.data.api.isAvailable
import com.aliernfrog.ensimanager.data.isSuccessful
import com.aliernfrog.ensimanager.data.summary
import com.aliernfrog.ensimanager.util.extension.showErrorToast
import com.aliernfrog.ensimanager.util.manager.PreferenceManager
import com.aliernfrog.ensimanager.util.staticutil.WebUtil
import com.aliernfrog.toptoast.state.TopToastState
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
class APIViewModel(
    val prefs: PreferenceManager,
    val topToastState: TopToastState,
    private val gson: Gson,
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

    var profileSheetEditingProfile by mutableStateOf<APIProfile?>(null)
        private set
    var profileSheetName by mutableStateOf("")
    var profileSheetEndpointsURL by mutableStateOf("")
    var profileSheetAuthorization by mutableStateOf("")
    var profileSheetShowAuthorization by mutableStateOf(false)

    var chosenProfile by mutableStateOf<APIProfile?>(null)
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
        try {
            apiProfiles.addAll(
                gson.fromJson(prefs.apiProfiles.value, Array<APIProfile>::class.java)
            )
        } catch (e: Exception) {
            Log.e(TAG, "Failed to load API profiles", e)
            topToastState.showErrorToast(R.string.api_profiles_restoreError)
        }

        viewModelScope.launch {
            refetchAllProfiles()

            if (prefs.rememberLastAPIProfile.value && prefs.lastActiveAPIProfileId.value.isNotEmpty()) {
                val selected = apiProfiles.find { it.id == prefs.lastActiveAPIProfileId.value } ?: return@launch
                if (selected.isAvailable) chosenProfile = selected
            }

            snapshotFlow { chosenProfile }
                .collect {
                    prefs.lastActiveAPIProfileId.value = it?.id.toString()
                }
        }
    }

    suspend fun refetchAllProfiles() {
        coroutineScope {
            apiProfiles.map {
                async {
                    fetchAPIEndpoints(it)
                }
            }.awaitAll()
        }
    }

    suspend fun fetchAPIEndpoints(profile: APIProfile): APIEndpoints? {
        fetchingProfiles.add(profile.id)
        val res = withContext(Dispatchers.IO) {
            try {
                val response = WebUtil.sendRequest(
                    toUrl = profile.endpointsURL,
                    method = "GET",
                    userAgent = userAgent
                )
                if (response.isSuccessful) {
                    val endpoints = gson.fromJson(response.responseBody, APIEndpoints::class.java)
                    endpoints?.migration?.url?.let {
                        profileMigrations[profile.id] = it
                    } ?: {
                        profileMigrations.remove(profile.id)
                    }
                    cache[profile.id] = cache[profile.id]?.copy(
                        endpoints = endpoints
                    ) ?: APIProfileCache(
                        endpoints = endpoints
                    )
                    profileErrors.remove(profile.id)
                    return@withContext endpoints
                } else {
                    profileErrors[profile.id] = response.summary
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

    fun getProfileCache(profile: APIProfile): APIProfileCache? {
        return cache[profile.id]
    }

    fun updateProfile(old: APIProfile, new: APIProfile) {
        val index = apiProfiles.indexOf(old)
        apiProfiles[index] = new
    }

    fun saveProfiles() {
        val json = gson.toJson(apiProfiles)
        prefs.apiProfiles.value = json
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
        profileSheetState.show()
    }

    fun clearProfileSheetState() {
        profileSheetEditingProfile = null
        profileSheetName = ""
        profileSheetEndpointsURL = ""
        profileSheetAuthorization = ""
        profileSheetShowAuthorization = false
    }
}