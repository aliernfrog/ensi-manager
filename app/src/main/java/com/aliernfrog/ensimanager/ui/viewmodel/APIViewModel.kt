package com.aliernfrog.ensimanager.ui.viewmodel

import android.content.Context
import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.PriorityHigh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.Density
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aliernfrog.ensimanager.R
import com.aliernfrog.ensimanager.TAG
import com.aliernfrog.ensimanager.data.api.APIEndpoints
import com.aliernfrog.ensimanager.data.api.APIProfile
import com.aliernfrog.ensimanager.data.api.id
import com.aliernfrog.ensimanager.data.isSuccessful
import com.aliernfrog.ensimanager.data.summary
import com.aliernfrog.ensimanager.util.extension.showErrorToast
import com.aliernfrog.ensimanager.util.manager.PreferenceManager
import com.aliernfrog.ensimanager.util.staticutil.WebUtil
import com.aliernfrog.toptoast.enum.TopToastColor
import com.aliernfrog.toptoast.state.TopToastState
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
class APIViewModel(
    val prefs: PreferenceManager,
    val topToastState: TopToastState,
    private val gson: Gson,
    context: Context
) : ViewModel() {
    val addProfileSheetState = SheetState(skipPartiallyExpanded = true, Density(context))

    val userAgent = WebUtil.buildUserAgent(context)
    val apiProfiles = mutableStateListOf<APIProfile>()
    val fetchingProfiles = mutableStateListOf<String>()
    val profileErrors = mutableStateMapOf<String, String>()
    val profileMigrations = mutableStateMapOf<String, String>()
    private val cache = mutableStateMapOf<String, Pair<String, String>>()

    var apiData by mutableStateOf<APIEndpoints?>(null)
        private set
    val isReady
        get() = apiData != null

    var fetching by mutableStateOf(false)

    var error by mutableStateOf<String?>(null)
        private set

    var legacySetupEndpointsURL by mutableStateOf(prefs.apiEndpointsUrl.value)
    var legacySetupAuthorization by mutableStateOf(prefs.apiAuthorization.value)
    var migratedTo by mutableStateOf<String?>(null)

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
            apiProfiles.forEach {
                fetchAPIEndpoints(it)
            }
        }
    }

    fun doInitialConnection(onFinish: () -> Unit) {
        CoroutineScope(Dispatchers.Main).launch {
            if (legacySetupEndpointsURL.isNotBlank()) fetchApiData(
                showToastOnSuccess = false
            )
            onFinish()
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
                        return@withContext endpoints
                    }
                    cache[profile.id] = "endpoints" to response.responseBody.toString()
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

    fun saveProfiles() {
        val json = gson.toJson(apiProfiles)
        prefs.apiProfiles.value = json
    }

    suspend fun fetchApiData(showToastOnSuccess: Boolean = true) {
        fetching = true
        withContext(Dispatchers.IO) {
            try {
                val response = WebUtil.sendRequest(
                    toUrl = legacySetupEndpointsURL,
                    method = "GET",
                    userAgent = userAgent
                )
                val isSuccess = response.error == null && response.statusCode.toString().startsWith("2")
                if (isSuccess) {
                    val data = gson.fromJson(response.responseBody, APIEndpoints::class.java)
                    data.migration?.url?.let { newURL ->
                        migratedTo = newURL
                        legacySetupEndpointsURL = newURL
                        return@withContext fetchApiData(showToastOnSuccess = showToastOnSuccess)
                    }
                    apiData = data
                    saveConfig()
                    if (showToastOnSuccess) topToastState.showToast(R.string.setup_saved, Icons.Rounded.Check)
                } else topToastState.showToast(
                    text = response.error ?: "[${response.statusCode}] ${response.responseBody}",
                    icon = Icons.Rounded.PriorityHigh,
                    iconTintColor = TopToastColor.ERROR
                )
            } catch (e: Exception) {
                Log.e(TAG, "fetchApiData: ", e)
                error = e.toString()
            }
            fetching = false
        }
    }

    private fun saveConfig() {
        prefs.apiEndpointsUrl.value = legacySetupEndpointsURL
        prefs.apiAuthorization.value = legacySetupAuthorization
    }
}