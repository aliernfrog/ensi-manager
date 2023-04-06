package com.aliernfrog.ensimanager.state

import android.content.SharedPreferences
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.aliernfrog.ensimanager.ConfigKey
import com.aliernfrog.ensimanager.data.EnsiAPIData
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL

class EnsiAPIState(
    private val config: SharedPreferences,
    onSetupRequest: () -> Unit
) {
    private val gson = Gson()

    var apiData by mutableStateOf<EnsiAPIData?>(null)
        private set

    init {
        val endpointsUrl = getApiEndpointsUrl()
        if (endpointsUrl.isNullOrBlank()) onSetupRequest()
        else CoroutineScope(Dispatchers.Main).launch {
            fetchApiData(endpointsUrl)
        }
    }

    private suspend fun fetchApiData(endpointsUrl: String): EnsiAPIData? {
        return withContext(Dispatchers.IO) {
            return@withContext try {
                val content = URL(endpointsUrl).readText()
                val data = gson.fromJson(content, EnsiAPIData::class.java)
                apiData = data
                data
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    private fun getApiEndpointsUrl(): String? {
        return config.getString(ConfigKey.KEY_API_ENDPOINTS_URL, null)
    }
}