package com.aliernfrog.ensimanager.ui.viewmodel

import android.content.Context
import android.util.Log
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.material3.TopAppBarState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.Density
import androidx.lifecycle.ViewModel
import com.aliernfrog.ensimanager.R
import com.aliernfrog.ensimanager.TAG
import com.aliernfrog.ensimanager.data.EnsiAPIChatCategory
import com.aliernfrog.ensimanager.data.doRequest
import com.aliernfrog.ensimanager.data.isSuccessful
import com.aliernfrog.ensimanager.data.summary
import com.aliernfrog.ensimanager.util.extension.showErrorToast
import com.aliernfrog.ensimanager.util.extension.toastSummary
import com.aliernfrog.toptoast.state.TopToastState
import com.google.gson.Gson
import org.json.JSONObject

@OptIn(ExperimentalMaterial3Api::class)
class ChatViewModel(
    context: Context,
    private val topToastState: TopToastState,
    private val apiViewModel: APIViewModel,
    private val gson: Gson
) : ViewModel() {
    val topAppBarState = TopAppBarState(0F, 0F, 0F)
    val lazyListState = LazyListState()
    val addWordSheetState = SheetState(skipPartiallyExpanded = true, Density(context))
    val wordSheetState = SheetState(skipPartiallyExpanded = false, Density(context))

    var filter by mutableStateOf("")
    var addWordInput by mutableStateOf("")
    val isFetching get() = apiViewModel.fetching

    var chosenWord by mutableStateOf("")
    var chosenWordType by mutableStateOf<String?>(null)

    var categories by mutableStateOf(listOf<EnsiAPIChatCategory>())
        private set

    var currentCategoryIndex by mutableIntStateOf(0)
    val currentCategory
        get() = categories.getOrNull(currentCategoryIndex)
    val currentCategoryList
        get() = currentCategory?.data?.filter {
            it.lowercase().contains(filter.lowercase())
        } ?: listOf()

    suspend fun fetchCategories() {
        try {
            val response = apiViewModel.apiData?.getChatCategories?.doRequest()
            if (response == null || !response.isSuccessful) return topToastState.showErrorToast(response.summary)
            categories = gson.fromJson(response.responseBody, Array<EnsiAPIChatCategory>::class.java).toList()
        } catch (e: Exception) {
            Log.e(TAG, "fetchCategories: ", e)
            topToastState.showErrorToast(R.string.chat_couldntFetch)
        }
    }

    suspend fun deleteChosenWord() {
        val json = JSONObject()
            .put("category", chosenWordType)
            .put("string", chosenWord)
        val response = apiViewModel.apiData?.deleteChatCategory?.doRequest(json)
        topToastState.toastSummary(response)
        fetchCategories()
    }

    suspend fun addWordFromInput() {
        val json = JSONObject()
            .put("category", currentCategory?.id)
            .put("string", addWordInput)
        val response = apiViewModel.apiData?.addChatCategory?.doRequest(json)
        topToastState.toastSummary(response)
        fetchCategories()
    }

    suspend fun showWordSheet(word: String) {
        chosenWord = word
        chosenWordType = currentCategory?.id
        wordSheetState.show()
    }
}