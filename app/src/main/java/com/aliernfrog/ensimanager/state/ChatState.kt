package com.aliernfrog.ensimanager.state

import android.content.Context
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material.icons.rounded.PriorityHigh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarState
import androidx.compose.runtime.mutableStateOf
import com.aliernfrog.ensimanager.ChatScreenType
import com.aliernfrog.ensimanager.FetchingState
import com.aliernfrog.ensimanager.R
import com.aliernfrog.ensimanager.data.HTTPResponse
import com.aliernfrog.ensimanager.util.staticutil.GeneralUtil
import com.aliernfrog.ensimanager.util.staticutil.WebUtil
import com.aliernfrog.toptoast.enum.TopToastColor
import com.aliernfrog.toptoast.state.TopToastState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
class ChatState(
    private val topToastState: TopToastState,
    private val apiState: EnsiAPIState,
    val lazyListState: LazyListState
) {
    val topAppBarState = TopAppBarState(0F, 0F, 0F)
    val addWordSheetState = ModalBottomSheetState(ModalBottomSheetValue.Hidden, isSkipHalfExpanded = true)
    val wordSheetState = ModalBottomSheetState(ModalBottomSheetValue.Hidden)

    val type = mutableStateOf(ChatScreenType.WORDS)
    val filter = mutableStateOf("")
    val addWordInput = mutableStateOf("")
    val fetchingState = mutableStateOf(FetchingState.FETCHING)

    val chosenWord = mutableStateOf("")
    val chosenWordType = mutableStateOf(0)

    private var words = mutableStateOf(listOf<String>())
    private var verbs = mutableStateOf(listOf<String>())

    fun getCurrentList(): List<String> {
        return when(type.value) {
            ChatScreenType.VERBS -> verbs.value
            else -> words.value
        }.filter { it.lowercase().contains(filter.value.lowercase()) }
    }

    suspend fun fetchCurrentList(context: Context) {
        fetchingState.value = FetchingState.FETCHING
        when(type.value) {
            ChatScreenType.WORDS -> fetchWords(context)
            ChatScreenType.VERBS -> fetchVerbs(context)
        }
        fetchingState.value = FetchingState.DONE
    }

    suspend fun deleteChosenWord(context: Context) {
        when(chosenWordType.value) {
            ChatScreenType.WORDS -> deleteWord(chosenWord.value, context)
            ChatScreenType.VERBS -> deleteVerb(chosenWord.value, context)
        }
        fetchCurrentList(context)
    }

    suspend fun addWordFromInput(context: Context) {
        when(type.value) {
            ChatScreenType.WORDS -> addWord(addWordInput.value, context)
            ChatScreenType.VERBS -> addVerb(addWordInput.value, context)
        }
        fetchCurrentList(context)
    }

    suspend fun showWordSheet(word: String) {
        chosenWord.value = word
        chosenWordType.value = type.value
        wordSheetState.show()
    }

    private suspend fun fetchWords(context: Context) {
        withContext(Dispatchers.IO) {
            handleJsonResponse(
                response = apiState.doRequest(apiState.apiData?.getWords),
                context = context
            ) {
                words.value = it
            }
        }
    }

    private suspend fun addWord(word: String, context: Context) {
        val json = JSONObject().put("word", word)
        withContext(Dispatchers.IO) {
            handleSuccessResponse(
                response = apiState.doRequest(
                    endpoint = apiState.apiData?.addWord,
                    json = json
                ),
                context = context
            )
        }
    }

    private suspend fun deleteWord(word: String, context: Context) {
        val json = JSONObject().put("word", word)
        withContext(Dispatchers.IO) {
            handleSuccessResponse(
                response = apiState.doRequest(
                    endpoint = apiState.apiData?.deleteWord,
                    json = json
                ),
                context = context
            )
        }
    }

    private suspend fun fetchVerbs(context: Context) {
        withContext(Dispatchers.IO) {
            handleJsonResponse(
                response = apiState.doRequest(apiState.apiData?.getVerbs),
                context = context
            ) {
                verbs.value = it
            }
        }
    }

    private suspend fun addVerb(verb: String, context: Context) {
        val json = JSONObject().put("verb", verb)
        withContext(Dispatchers.IO) {
            handleSuccessResponse(
                response = apiState.doRequest(
                    endpoint = apiState.apiData?.addVerb,
                    json = json
                ),
                context = context
            )
        }
    }

    private suspend fun deleteVerb(verb: String, context: Context) {
        val json = JSONObject().put("verb", verb)
        withContext(Dispatchers.IO) {
            handleSuccessResponse(
                response = apiState.doRequest(
                    endpoint = apiState.apiData?.deleteVerb,
                    json = json
                ),
                context = context
            )
        }
    }

    private fun handleJsonResponse(response: HTTPResponse?, context: Context, onSuccess: (List<String>) -> Unit) {
        if (response?.responseBody.isNullOrBlank()) toastNoBody(context, response?.statusCode)
        else if (!GeneralUtil.isJsonArray(response!!.responseBody!!)) toastError("[${response.statusCode}] ${response.responseBody}")
        else onSuccess(GeneralUtil.jsonArrayToList(JSONArray(response.responseBody)))
    }

    private fun handleSuccessResponse(response: HTTPResponse?, context: Context, onSuccess: (() -> Unit)? = null) {
        if (response?.statusCode == null) toastNoBody(context, null)
        else if (!WebUtil.statusCodeIsSuccess(response.statusCode)) toastError("[${response.statusCode}] ${response.responseBody}")
        else {
            topToastState.showToast("[${response.statusCode}] ${response.responseBody}", icon = Icons.Rounded.Done, iconTintColor = TopToastColor.PRIMARY)
            if (onSuccess != null) onSuccess()
        }
    }

    private fun toastNoBody(context: Context, statusCode: Int?) {
        toastError(context.getString(R.string.error_noBody).replace("%STATUS%", statusCode.toString()))
    }

    private fun toastError(text: String) {
        topToastState.showToast(text, icon = Icons.Rounded.PriorityHigh, iconTintColor = TopToastColor.ERROR)
    }
}