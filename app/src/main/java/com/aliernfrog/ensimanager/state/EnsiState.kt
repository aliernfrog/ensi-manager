package com.aliernfrog.ensimanager.state

import android.content.SharedPreferences
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
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

    var words = mutableStateOf(listOf<String>())
    var verbs = mutableStateOf(listOf<String>())

    private var authorization: String? = null
    private var getWordsRoute: ApiRoute? = null
    private var getVerbsRoute: ApiRoute? = null

    fun updateApiProperties() {
        authorization = config.getString(ConfigKey.KEY_API_AUTHORIZATION, null)
        getWordsRoute = GeneralUtil.getApiRouteFromString(config.getString(ConfigKey.KEY_API_WORDS_GET, " ## ")!!)
        getVerbsRoute = GeneralUtil.getApiRouteFromString(config.getString(ConfigKey.KEY_API_VERBS_GET, " ## ")!!)
    }

    suspend fun getWords() {
        if (getWordsRoute == null) return
        withContext(Dispatchers.IO) {
            val response = WebUtil.sendRequest(getWordsRoute!!.url, getWordsRoute!!.method, authorization)
            if (response.responseBody != null && GeneralUtil.isJsonArray(response.responseBody)) {
                words.value = GeneralUtil.jsonArrayToList(JSONArray(response.responseBody))
            }
        }
    }
}