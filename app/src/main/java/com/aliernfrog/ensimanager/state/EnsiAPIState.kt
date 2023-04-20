package com.aliernfrog.ensimanager.state

import android.content.SharedPreferences
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.PriorityHigh
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import com.aliernfrog.ensimanager.ConfigKey
import com.aliernfrog.ensimanager.R
import com.aliernfrog.ensimanager.data.EnsiAPIData
import com.aliernfrog.ensimanager.data.EnsiAPIEndpoint
import com.aliernfrog.ensimanager.data.HTTPResponse
import com.aliernfrog.ensimanager.util.NavigationConstant
import com.aliernfrog.ensimanager.util.extension.doRequest
import com.aliernfrog.ensimanager.util.staticutil.WebUtil
import com.aliernfrog.toptoast.enum.TopToastColor
import com.aliernfrog.toptoast.state.TopToastState
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

class EnsiAPIState(
    private val config: SharedPreferences,
    private val topToastState: TopToastState,
    private val getNavController: () -> NavHostController
) {
    private val gson = Gson()

    var apiData by mutableStateOf<EnsiAPIData?>(null)
        private set

    var setupCancellable by mutableStateOf(false)
    var setupFetching by mutableStateOf(false)
    var setupEndpointsUrl by mutableStateOf(config.getString(ConfigKey.KEY_API_ENDPOINTS_URL, "") ?: "")
    var setupAuth by mutableStateOf(config.getString(ConfigKey.KEY_API_AUTHORIZATION, "") ?: "")
    var setupError by mutableStateOf<String?>(null)

    init {
        if (setupEndpointsUrl.isBlank()) setupCancellable = false
        else CoroutineScope(Dispatchers.Main).launch {
            fetchApiData(
                switchScreenOnSuccess = true,
                showToastOnSuccess = false
            )
        }
    }

    suspend fun fetchApiData(switchScreenOnSuccess: Boolean = false, showToastOnSuccess: Boolean = true) {
        setupFetching = true
        withContext(Dispatchers.IO) {
            try {
                val response = WebUtil.sendRequest(setupEndpointsUrl, "GET")
                val isSuccess = response.error == null && response.statusCode.toString().startsWith("2")
                if (isSuccess) {
                    val data = gson.fromJson(response.responseBody, EnsiAPIData::class.java)
                    apiData = data
                    setupCancellable = true
                    saveConfig()
                    if (showToastOnSuccess) topToastState.showToast(R.string.setup_saved, Icons.Rounded.Check)
                    if (switchScreenOnSuccess) withContext(Dispatchers.Main) {
                        getNavController().navigate(NavigationConstant.POST_SETUP_DESTINATION) { popUpTo(0) }
                    }
                } else topToastState.showToast(
                    text = response.error ?: "[${response.statusCode}] ${response.responseBody}",
                    icon = Icons.Rounded.PriorityHigh,
                    iconTintColor = TopToastColor.ERROR
                )
            } catch (e: Exception) {
                e.printStackTrace()
                setupError = e.toString()
            }
            setupFetching = false
        }
    }

    private fun saveConfig() {
        val configEdit = config.edit()
        configEdit.putString(ConfigKey.KEY_API_ENDPOINTS_URL, setupEndpointsUrl)
        configEdit.putString(ConfigKey.KEY_API_AUTHORIZATION, setupAuth)
        configEdit.apply()
    }

    fun doRequest(
        endpoint: EnsiAPIEndpoint?,
        json: JSONObject? = null
    ): HTTPResponse? {
        return endpoint?.doRequest(
            json = json,
            authorization = setupAuth
        )
    }
}