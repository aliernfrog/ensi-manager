package com.aliernfrog.ensimanager.state

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.mutableStateOf
import com.aliernfrog.ensimanager.ConfigKey
import com.aliernfrog.ensimanager.EnsiFetchingState
import com.aliernfrog.ensimanager.EnsiScreenType
import com.aliernfrog.ensimanager.R
import com.aliernfrog.ensimanager.data.ApiRoute
import com.aliernfrog.ensimanager.util.GeneralUtil
import com.aliernfrog.ensimanager.util.WebUtil
import com.aliernfrog.toptoast.TopToastColorType
import com.aliernfrog.toptoast.TopToastManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray

class EnsiState(_config: SharedPreferences, _topToastManager: TopToastManager, _lazyListState: LazyListState) {
    private val config = _config
    private val topToastManager = _topToastManager
    val lazyListState = _lazyListState

    val type = mutableStateOf(EnsiScreenType.WORDS)
    val filter = mutableStateOf("")
    val fetchingState = mutableStateOf(EnsiFetchingState.FETCHING)

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
        }.filter { it.lowercase().contains(filter.value.lowercase()) }
    }

    suspend fun fetchCurrentList(context: Context) {
        fetchingState.value = EnsiFetchingState.FETCHING
        when(type.value) {
            EnsiScreenType.WORDS -> fetchWords(context)
            EnsiScreenType.VERBS -> fetchVerbs(context)
        }
        fetchingState.value = EnsiFetchingState.DONE
    }

    private suspend fun fetchWords(context: Context) {
        if (getWordsRoute == null) return toastInvalidRoute(context)
        if (!getWordsRoute!!.url.contains("://")) return toastInvalidRoute(context)
        withContext(Dispatchers.IO) {
            val response = WebUtil.sendRequest(getWordsRoute!!.url, getWordsRoute!!.method, authorization)
            if (response?.responseBody != null && GeneralUtil.isJsonArray(response.responseBody)) {
                words.value = GeneralUtil.jsonArrayToList(JSONArray(response.responseBody))
            }
        }
    }

    private suspend fun fetchVerbs(context: Context) {
        if (getVerbsRoute == null) return toastInvalidRoute(context)
        if (!getVerbsRoute!!.url.contains("://")) return toastInvalidRoute(context)
        withContext(Dispatchers.IO) {
            val response = WebUtil.sendRequest(getVerbsRoute!!.url, getVerbsRoute!!.method, authorization)
            if (response?.responseBody != null && GeneralUtil.isJsonArray(response.responseBody)) {
                verbs.value = GeneralUtil.jsonArrayToList(JSONArray(response.responseBody))
            }
        }
    }

    private fun toastInvalidRoute(context: Context) {
        topToastManager.showToast(context.getString(R.string.ensi_fetch_invalidRoute), iconDrawableId = R.drawable.exclamation, iconTintColorType = TopToastColorType.ERROR)
    }
}