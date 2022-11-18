package com.aliernfrog.ensimanager.state

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.runtime.mutableStateOf
import com.aliernfrog.ensimanager.ConfigKey
import com.aliernfrog.ensimanager.EnsiFetchingState
import com.aliernfrog.ensimanager.EnsiScreenType
import com.aliernfrog.ensimanager.R
import com.aliernfrog.ensimanager.data.ApiResponse
import com.aliernfrog.ensimanager.data.ApiRoute
import com.aliernfrog.ensimanager.util.GeneralUtil
import com.aliernfrog.ensimanager.util.WebUtil
import com.aliernfrog.toptoast.TopToastColorType
import com.aliernfrog.toptoast.TopToastManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

@OptIn(ExperimentalMaterialApi::class)
class EnsiState(_config: SharedPreferences, _topToastManager: TopToastManager, _lazyListState: LazyListState) {
    private val config = _config
    private val topToastManager = _topToastManager
    val wordSheetState = ModalBottomSheetState(ModalBottomSheetValue.Hidden)
    val lazyListState = _lazyListState

    val type = mutableStateOf(EnsiScreenType.WORDS)
    val filter = mutableStateOf("")
    val fetchingState = mutableStateOf(EnsiFetchingState.FETCHING)

    val chosenWord = mutableStateOf("")
    val chosenWordType = mutableStateOf(0)

    private var words = mutableStateOf(listOf<String>())
    private var verbs = mutableStateOf(listOf<String>())

    private var authorization: String? = null
    private var getWordsRoute: ApiRoute? = null
    private var deleteWordsRoute: ApiRoute? = null
    private var getVerbsRoute: ApiRoute? = null
    private var deleteVerbsRoute: ApiRoute? = null

    fun updateApiProperties() {
        authorization = config.getString(ConfigKey.KEY_API_AUTHORIZATION, null)
        getWordsRoute = GeneralUtil.getApiRouteFromString(config.getString(ConfigKey.KEY_API_WORDS_GET, " ## ")!!)
        deleteWordsRoute = GeneralUtil.getApiRouteFromString(config.getString(ConfigKey.KEY_API_WORDS_DELETE, " ## ")!!)
        getVerbsRoute = GeneralUtil.getApiRouteFromString(config.getString(ConfigKey.KEY_API_VERBS_GET, " ## ")!!)
        deleteVerbsRoute = GeneralUtil.getApiRouteFromString(config.getString(ConfigKey.KEY_API_VERBS_DELETE, " ## ")!!)
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

    suspend fun deleteChosenWord(context: Context) {
        when(chosenWordType.value) {
            EnsiScreenType.WORDS -> deleteWord(chosenWord.value, context)
            EnsiScreenType.VERBS -> deleteVerb(chosenWord.value, context)
        }
        fetchCurrentList(context)
    }

    suspend fun showWordSheet(word: String) {
        chosenWord.value = word
        chosenWordType.value = type.value
        wordSheetState.show()
    }

    private suspend fun fetchWords(context: Context) {
        if (routeInvalid(getWordsRoute)) return toastInvalidRoute(context)
        withContext(Dispatchers.IO) {
            handleJsonResponse(WebUtil.sendRequest(getWordsRoute!!.url, getWordsRoute!!.method, authorization), context) {
                words.value = it
            }
        }
    }

    private suspend fun deleteWord(word: String, context: Context) {
        if (routeInvalid(deleteWordsRoute)) return toastInvalidRoute(context)
        val json = JSONObject().put("word", word)
        withContext(Dispatchers.IO) {
            handleSuccessResponse(WebUtil.sendRequest(deleteWordsRoute!!.url, deleteWordsRoute!!.method, authorization, json), context)
        }
    }

    private suspend fun fetchVerbs(context: Context) {
        if (routeInvalid(getVerbsRoute)) return toastInvalidRoute(context)
        withContext(Dispatchers.IO) {
            handleJsonResponse(WebUtil.sendRequest(getVerbsRoute!!.url, getVerbsRoute!!.method, authorization), context) {
                verbs.value = it
            }
        }
    }

    private suspend fun deleteVerb(verb: String, context: Context) {
        if (routeInvalid(deleteVerbsRoute)) return toastInvalidRoute(context)
        val json = JSONObject().put("verb", verb)
        withContext(Dispatchers.IO) {
            handleSuccessResponse(WebUtil.sendRequest(deleteVerbsRoute!!.url, deleteVerbsRoute!!.method, authorization, json), context)
        }
    }

    private fun handleJsonResponse(response: ApiResponse?, context: Context, onSuccess: (List<String>) -> Unit) {
        if (response?.responseBody.isNullOrBlank()) toastNoBody(context, response?.statusCode)
        else if (!GeneralUtil.isJsonArray(response!!.responseBody!!)) toastError("[${response.statusCode}] ${response.responseBody}")
        else onSuccess(GeneralUtil.jsonArrayToList(JSONArray(response.responseBody)))
    }

    private fun handleSuccessResponse(response: ApiResponse?, context: Context, onSuccess: (() -> Unit)? = null) {
        if (response?.statusCode == null) toastNoBody(context, null)
        else if (!WebUtil.statusCodeIsSuccess(response.statusCode)) toastError("[${response.statusCode}] ${response.responseBody}")
        else {
            topToastManager.showToast("[${response.statusCode}] ${response.responseBody}", iconDrawableId = R.drawable.check, iconTintColorType = TopToastColorType.PRIMARY)
            if (onSuccess != null) onSuccess()
        }
    }

    private fun routeInvalid(route: ApiRoute?): Boolean {
        if (route == null) return true
        if (!route.url.contains("://")) return true
        return false
    }

    private fun toastInvalidRoute(context: Context) {
        toastError(context.getString(R.string.ensi_fetch_invalidRoute))
    }

    private fun toastNoBody(context: Context, statusCode: Int?) {
        toastError(context.getString(R.string.ensi_fetch_noBody).replace("%STATUS%", statusCode.toString()))
    }

    private fun toastError(text: String) {
        topToastManager.showToast(text, iconDrawableId = R.drawable.exclamation, iconTintColorType = TopToastColorType.ERROR)
    }
}