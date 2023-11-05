package com.aliernfrog.ensimanager.ui.viewmodel

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.Density
import androidx.lifecycle.ViewModel
import com.aliernfrog.ensimanager.R
import com.aliernfrog.ensimanager.data.HTTPResponse
import com.aliernfrog.ensimanager.enum.ChatFilterType
import com.aliernfrog.ensimanager.util.manager.ContextUtils
import com.aliernfrog.ensimanager.util.staticutil.GeneralUtil
import com.aliernfrog.ensimanager.util.staticutil.WebUtil
import com.aliernfrog.toptoast.enum.TopToastColor
import com.aliernfrog.toptoast.state.TopToastState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
class ChatViewModel(
    context: Context,
    private val contextUtils: ContextUtils,
    private val topToastState: TopToastState,
    private val apiViewModel: APIViewModel
) : ViewModel() {
    val topAppBarState = TopAppBarState(0F, 0F, 0F)
    val lazyListState = LazyListState()
    val addWordSheetState = ModalBottomSheetState(ModalBottomSheetValue.Hidden, Density(context), isSkipHalfExpanded = true)
    val wordSheetState = ModalBottomSheetState(ModalBottomSheetValue.Hidden, Density(context))

    var type by mutableStateOf(ChatFilterType.WORDS)
    var filter by mutableStateOf("")
    var addWordInput by mutableStateOf("")
    val isFetching get() = apiViewModel.fetching

    var chosenWord by mutableStateOf("")
    var chosenWordType by mutableStateOf(ChatFilterType.WORDS)

    private var words by mutableStateOf(listOf<String>())
    private var verbs by mutableStateOf(listOf<String>())
    val currentList get() = when(type) {
        ChatFilterType.VERBS -> verbs
        else -> words
    }.filter { it.lowercase().contains(filter.lowercase()) }

    suspend fun fetchCurrentList() {
        when(type) {
            ChatFilterType.WORDS -> fetchWords()
            ChatFilterType.VERBS -> fetchVerbs()
        }
    }

    suspend fun deleteChosenWord() {
        when(chosenWordType) {
            ChatFilterType.WORDS -> deleteWord(chosenWord)
            ChatFilterType.VERBS -> deleteVerb(chosenWord)
        }
        fetchCurrentList()
    }

    suspend fun addWordFromInput() {
        when(type) {
            ChatFilterType.WORDS -> addWord(addWordInput)
            ChatFilterType.VERBS -> addVerb(addWordInput)
        }
        fetchCurrentList()
    }

    suspend fun showWordSheet(word: String) {
        chosenWord = word
        chosenWordType = type
        wordSheetState.show()
    }

    private suspend fun fetchWords() {
        withContext(Dispatchers.IO) {
            handleJsonResponse(
                response = apiViewModel.doRequest(apiViewModel.apiData?.getWords)
            ) {
                words = it
            }
        }
    }

    private suspend fun addWord(word: String) {
        val json = JSONObject().put("word", word)
        withContext(Dispatchers.IO) {
            handleSuccessResponse(
                response = apiViewModel.doRequest(
                    endpoint = apiViewModel.apiData?.addWord,
                    json = json
                )
            )
        }
    }

    private suspend fun deleteWord(word: String) {
        val json = JSONObject().put("word", word)
        withContext(Dispatchers.IO) {
            handleSuccessResponse(
                response = apiViewModel.doRequest(
                    endpoint = apiViewModel.apiData?.deleteWord,
                    json = json
                )
            )
        }
    }

    private suspend fun fetchVerbs() {
        withContext(Dispatchers.IO) {
            handleJsonResponse(
                response = apiViewModel.doRequest(apiViewModel.apiData?.getVerbs)
            ) {
                verbs = it
            }
        }
    }

    private suspend fun addVerb(verb: String) {
        val json = JSONObject().put("verb", verb)
        withContext(Dispatchers.IO) {
            handleSuccessResponse(
                response = apiViewModel.doRequest(
                    endpoint = apiViewModel.apiData?.addVerb,
                    json = json
                )
            )
        }
    }

    private suspend fun deleteVerb(verb: String) {
        val json = JSONObject().put("verb", verb)
        withContext(Dispatchers.IO) {
            handleSuccessResponse(
                response = apiViewModel.doRequest(
                    endpoint = apiViewModel.apiData?.deleteVerb,
                    json = json
                )
            )
        }
    }

    private fun handleJsonResponse(
        response: HTTPResponse?,
        onSuccess: (List<String>) -> Unit
    ) {
        if (response?.responseBody.isNullOrBlank()) toastNoBody(response?.statusCode)
        else if (!GeneralUtil.isJsonArray(response!!.responseBody!!)) toastError("[${response.statusCode}] ${response.responseBody}")
        else onSuccess(GeneralUtil.jsonArrayToList(JSONArray(response.responseBody)))
    }

    private fun handleSuccessResponse(
        response: HTTPResponse?,
        onSuccess: (() -> Unit)? = null
    ) {
        if (response?.statusCode == null) toastNoBody(null)
        else if (!WebUtil.statusCodeIsSuccess(response.statusCode)) toastError("[${response.statusCode}] ${response.responseBody}")
        else {
            topToastState.showToast("[${response.statusCode}] ${response.responseBody}", icon = Icons.Rounded.Done, iconTintColor = TopToastColor.PRIMARY)
            onSuccess?.invoke()
        }
    }

    private fun toastNoBody(statusCode: Int?) {
        toastError(
            contextUtils.getString(R.string.error_noBody).replace("%STATUS%", statusCode.toString())
        )
    }

    private fun toastError(text: String) {
        topToastState.showToast(text, icon = Icons.Rounded.PriorityHigh, iconTintColor = TopToastColor.ERROR)
    }
}