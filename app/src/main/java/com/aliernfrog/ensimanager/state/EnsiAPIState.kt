package com.aliernfrog.ensimanager.state

import android.content.SharedPreferences
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
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
import com.aliernfrog.toptoast.state.TopToastState
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL

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
                //TODO? check if status code is 2xx and print response if not
                val content = URL(setupEndpointsUrl).readText()
                val data = gson.fromJson(content, EnsiAPIData::class.java)
                apiData = data
                setupCancellable = true
                saveConfig()
                if (showToastOnSuccess) topToastState.showToast(R.string.setup_saved, Icons.Rounded.Check)
                if (switchScreenOnSuccess) withContext(Dispatchers.Main) {
                    getNavController().navigate(NavigationConstant.POST_SETUP_DESTINATION) { popUpTo(0) }
                }
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