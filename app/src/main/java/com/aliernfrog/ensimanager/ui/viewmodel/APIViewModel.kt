package com.aliernfrog.ensimanager.ui.viewmodel

import android.content.Context
import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.PriorityHigh
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.aliernfrog.ensimanager.R
import com.aliernfrog.ensimanager.TAG
import com.aliernfrog.ensimanager.data.EnsiAPIData
import com.aliernfrog.ensimanager.data.EnsiAPIEndpoint
import com.aliernfrog.ensimanager.data.HTTPResponse
import com.aliernfrog.ensimanager.util.extension.doRequest
import com.aliernfrog.ensimanager.util.manager.PreferenceManager
import com.aliernfrog.ensimanager.util.staticutil.WebUtil
import com.aliernfrog.toptoast.enum.TopToastColor
import com.aliernfrog.toptoast.state.TopToastState
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

class APIViewModel(
    private val prefs: PreferenceManager,
    private val topToastState: TopToastState,
    private val gson: Gson,
    context: Context
) : ViewModel() {
    private val userAgent = WebUtil.buildUserAgent(context)

    var apiData by mutableStateOf<EnsiAPIData?>(null)
        private set
    val isReady
        get() = apiData != null

    var fetching by mutableStateOf(false)
        private set

    var error by mutableStateOf<String?>(null)
        private set

    var setupEndpointsURL by mutableStateOf(prefs.apiEndpointsUrl)
    var setupAuthorization by mutableStateOf(prefs.apiAuthorization)
    var migratedTo by mutableStateOf<String?>(null)

    fun doInitialConnection(onFinish: () -> Unit) {
        CoroutineScope(Dispatchers.Main).launch {
            if (setupEndpointsURL.isNotBlank()) fetchApiData(
                showToastOnSuccess = false
            )
            onFinish()
        }
    }

    suspend fun fetchApiData(showToastOnSuccess: Boolean = true) {
        fetching = true
        withContext(Dispatchers.IO) {
            try {
                val response = WebUtil.sendRequest(
                    toUrl = setupEndpointsURL,
                    method = "GET",
                    userAgent = userAgent
                )
                val isSuccess = response.error == null && response.statusCode.toString().startsWith("2")
                if (isSuccess) {
                    val data = gson.fromJson(response.responseBody, EnsiAPIData::class.java)
                    data.migration?.url?.let { newURL ->
                        migratedTo = newURL
                        setupEndpointsURL = newURL
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
        prefs.apiEndpointsUrl = setupEndpointsURL
        prefs.apiAuthorization = setupAuthorization
    }

    suspend fun doRequest(
        endpoint: EnsiAPIEndpoint?,
        json: JSONObject? = null
    ): HTTPResponse? {
        fetching = true
        return withContext(Dispatchers.IO) {
            val response = endpoint?.doRequest(
                json = json,
                authorization = setupAuthorization,
                userAgent = userAgent
            )
            fetching = false
            return@withContext response
        }
    }
}