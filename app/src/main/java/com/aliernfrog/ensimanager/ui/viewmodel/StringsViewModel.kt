package com.aliernfrog.ensimanager.ui.viewmodel

import android.content.Context
import android.util.Log
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.Density
import androidx.lifecycle.ViewModel
import com.aliernfrog.ensimanager.R
import com.aliernfrog.ensimanager.TAG
import com.aliernfrog.ensimanager.data.api.APIStringsCategory
import com.aliernfrog.ensimanager.data.isSuccessful
import com.aliernfrog.ensimanager.data.summary
import com.aliernfrog.ensimanager.domain.APIState
import com.aliernfrog.ensimanager.util.extension.showErrorToast
import com.aliernfrog.ensimanager.util.extension.toastSummary
import com.aliernfrog.toptoast.state.TopToastState
import com.google.gson.Gson
import io.github.aliernfrog.shared.ui.component.createSheetStateWithDensity
import org.json.JSONObject

@OptIn(ExperimentalMaterial3Api::class)
class StringsViewModel(
    context: Context,
    private val topToastState: TopToastState,
    private val apiState: APIState,
    private val gson: Gson
) : ViewModel() {
    val topAppBarState = TopAppBarState(0F, 0F, 0F)
    val lazyListState = LazyListState()
    val addStringSheetState = createSheetStateWithDensity(skipPartiallyExpanded = true, Density(context))
    val stringSheetState = createSheetStateWithDensity(skipPartiallyExpanded = false, Density(context))

    var filter by mutableStateOf("")
    val isFetching get() = apiState.chosenProfile?.isFetching == true

    var chosenString by mutableStateOf("")
    var chosenStringCategory by mutableStateOf<String?>(null)

    var categories by mutableStateOf(listOf<APIStringsCategory>())
        private set

    var currentCategoryIndex by mutableIntStateOf(0)
    val currentCategory
        get() = categories.getOrNull(currentCategoryIndex)
    val currentCategoryList
        get() = currentCategory?.data?.filter {
            it.contains(filter, ignoreCase = true)
        } ?: listOf()

    init {
        apiState.onProfileSwitchListeners.add {
            categories = emptyList()
        }
    }

    suspend fun fetchStrings() {
        try {
            val response = apiState.chosenProfile?.doRequest({ it.getStrings })
            if (response == null || !response.isSuccessful) return topToastState.showErrorToast(response.summary)
            categories = gson.fromJson(response.responseBody, Array<APIStringsCategory>::class.java).toList()
        } catch (e: Exception) {
            Log.e(TAG, "fetchStrings: ", e)
            topToastState.showErrorToast(R.string.strings_couldntFetch)
        }
    }

    suspend fun deleteChosenWord() {
        val json = JSONObject()
            .put("category", chosenStringCategory)
            .put("string", chosenString)
        val response = apiState.chosenProfile?.doRequest({ it.deleteString }, json)
        topToastState.toastSummary(response)
        fetchStrings()
    }

    suspend fun addString(string: String) {
        val json = JSONObject()
            .put("category", currentCategory?.id)
            .put("string", string)
        val response = apiState.chosenProfile?.doRequest({ it.addString }, json)
        topToastState.toastSummary(response)
        fetchStrings()
    }

    suspend fun showStringSheet(string: String) {
        chosenString = string
        chosenStringCategory = currentCategory?.id
        stringSheetState.show()
    }
}