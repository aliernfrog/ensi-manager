package com.aliernfrog.ensimanager.state

import android.content.SharedPreferences
import androidx.compose.runtime.mutableStateOf
import com.aliernfrog.ensimanager.ConfigKey
import com.aliernfrog.ensimanager.EnsiScreenType
import com.aliernfrog.ensimanager.data.ApiRoute
import com.aliernfrog.ensimanager.util.GeneralUtil
import com.aliernfrog.ensimanager.util.WebUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray

class EnsiState(_config: SharedPreferences) {
    private val config = _config

    val type = mutableStateOf(EnsiScreenType.WORDS)
    val filter = mutableStateOf("")

    private var words = mutableStateOf(listOf<String>())
    private var verbs = mutableStateOf(listOf<String>())

    private var authorization: String? = null
    private var getWordsRoute: ApiRoute? = null
    private var getVerbsRoute: ApiRoute? = null

    fun updateApiProperties() {
        authorization = config.getString(ConfigKey.KEY_API_AUTHORIZATION, null)
        getWordsRoute = GeneralUtil.getApiRouteFromString(config.getString(ConfigKey.KEY_API_WORDS_GET, " ## ")!!)
        getVerbsRoute = GeneralUtil.getApiRouteFromString(config.getString(ConfigKey.KEY_API_VERBS_GET, " ## ")!!)
    }

    fun getCurrentList(): List<String> {
        return when(type.value) {
            EnsiScreenType.VERBS -> verbs.value
            else -> words.value
        }
    }

    suspend fun fetchCurrentList() {
        when(type.value) {
            EnsiScreenType.WORDS -> fetchWords()
            EnsiScreenType.VERBS -> fetchVerbs()
        }
    }

    private suspend fun fetchWords() {
        if (getWordsRoute == null) return
        withContext(Dispatchers.IO) {
            val response = WebUtil.sendRequest(getWordsRoute!!.url, getWordsRoute!!.method, authorization)
            if (response.responseBody != null && GeneralUtil.isJsonArray(response.responseBody)) {
                words.value = GeneralUtil.jsonArrayToList(JSONArray(response.responseBody))
            }
        }
    }

    private suspend fun fetchVerbs() {
        if (getVerbsRoute == null) return
        withContext(Dispatchers.IO) {
            val response = WebUtil.sendRequest(getVerbsRoute!!.url, getVerbsRoute!!.method, authorization)
            if (response.responseBody != null && GeneralUtil.isJsonArray(response.responseBody)) {
                verbs.value = GeneralUtil.jsonArrayToList(JSONArray(response.responseBody))
            }
        }
    }
}