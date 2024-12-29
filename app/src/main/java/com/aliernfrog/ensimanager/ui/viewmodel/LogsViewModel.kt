package com.aliernfrog.ensimanager.ui.viewmodel

import android.util.Log
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aliernfrog.ensimanager.R
import com.aliernfrog.ensimanager.TAG
import com.aliernfrog.ensimanager.data.api.APILog
import com.aliernfrog.ensimanager.data.api.doRequest
import com.aliernfrog.ensimanager.data.isSuccessful
import com.aliernfrog.ensimanager.enum.APILogType
import com.aliernfrog.ensimanager.util.extension.getTimeStr
import com.aliernfrog.ensimanager.util.extension.showErrorToast
import com.aliernfrog.ensimanager.util.extension.toastSummary
import com.aliernfrog.ensimanager.util.manager.ContextUtils
import com.aliernfrog.toptoast.state.TopToastState
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.cancellation.CancellationException

@OptIn(ExperimentalMaterial3Api::class)
class LogsViewModel(
    private val contextUtils: ContextUtils,
    private val topToastState: TopToastState,
    private val apiViewModel: APIViewModel,
    private val gson: Gson
) : ViewModel() {
    val topAppBarState = TopAppBarState(0F, 0F, 0F)
    val lazyListState = LazyListState()

    var logs by mutableStateOf(listOf<APILog>())
        private set

    val shownLogTypes = mutableStateListOf(*APILogType.entries.toTypedArray())
    var logsReversed by mutableStateOf(false)
    val shownLogs: List<APILog>
        get() = logs.filter {
            shownLogTypes.contains(it.type)
        }.let {
            // inverted logic to make the "reversed" one show oldest at top
            return@let if (!logsReversed) it.reversed() else it
        }

    val isFetching get() = apiViewModel.isChosenProfileFetching

    init {
        viewModelScope.launch {
            snapshotFlow { apiViewModel.chosenProfile }
                .collect {
                    logs = emptyList()
                }
        }
    }

    suspend fun fetchLogs() {
        withContext(Dispatchers.IO) {
            try {
                val response = apiViewModel.chosenProfile?.doRequest({ it.getLogs })
                if (response?.isSuccessful != true) return@withContext topToastState.toastSummary(response)
                logs = gson.fromJson(response.responseBody, Array<APILog>::class.java).toList()
                withContext(Dispatchers.Main) {
                    contextUtils.run { ctx ->
                        logs.forEach {
                            it.getTimeStr(ctx, force = true)
                        }
                    }
                }
            } catch (_: CancellationException) {
            } catch (e: Exception) {
                Log.e(TAG, "fetchLogs: ", e)
                topToastState.showErrorToast(R.string.logs_couldntFetch)
            }
        }
    }
}