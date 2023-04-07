package com.aliernfrog.ensimanager.state

import android.content.SharedPreferences
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Done
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.aliernfrog.ensimanager.ConfigKey
import com.aliernfrog.ensimanager.R
import com.aliernfrog.ensimanager.data.EnsiAPIData
import com.aliernfrog.toptoast.state.TopToastState
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL

class EnsiAPIState(
    private val config: SharedPreferences,
    private val topToastState: TopToastState
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
            fetchApiData()
        }
    }

    suspend fun fetchApiData(): EnsiAPIData? {
        setupFetching = true
        return withContext(Dispatchers.IO) {
            return@withContext try {
                //TODO? check if status code is 2xx and print response if not
                val content = URL(setupEndpointsUrl).readText()
                val data = gson.fromJson(content, EnsiAPIData::class.java)
                apiData = data
                setupCancellable = true
                setupFetching = false
                savePrefs()
                data
            } catch (e: Exception) {
                e.printStackTrace()
                setupFetching = false
                setupError = e.toString()
                null
            }
        }
    }

    private fun savePrefs() {
        val configEdit = config.edit()
        configEdit.putString(ConfigKey.KEY_API_ENDPOINTS_URL, setupEndpointsUrl)
        configEdit.putString(ConfigKey.KEY_API_AUTHORIZATION, setupAuth)
        configEdit.apply()
        topToastState.showToast(
            text = R.string.setup_saved,
            icon = Icons.Rounded.Done
        )
    }
}