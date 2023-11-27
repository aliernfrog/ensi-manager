package com.aliernfrog.ensimanager.ui.viewmodel

import android.util.Log
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material.icons.rounded.PriorityHigh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.aliernfrog.ensimanager.R
import com.aliernfrog.ensimanager.TAG
import com.aliernfrog.ensimanager.data.EnsiLog
import com.aliernfrog.ensimanager.data.HTTPResponse
import com.aliernfrog.ensimanager.enum.EnsiLogType
import com.aliernfrog.ensimanager.util.extension.getTimeStr
import com.aliernfrog.ensimanager.util.extension.isSuccessful
import com.aliernfrog.ensimanager.util.extension.showErrorToast
import com.aliernfrog.ensimanager.util.extension.summary
import com.aliernfrog.ensimanager.util.extension.toastSummary
import com.aliernfrog.ensimanager.util.manager.ContextUtils
import com.aliernfrog.ensimanager.util.staticutil.WebUtil
import com.aliernfrog.toptoast.enum.TopToastColor
import com.aliernfrog.toptoast.state.TopToastState
import com.google.gson.Gson
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
class DashboardViewModel(
    private val contextUtils: ContextUtils,
    private val topToastState: TopToastState,
    private val apiViewModel: APIViewModel,
    private val gson: Gson
) : ViewModel() {
    val topAppBarState = TopAppBarState(0F, 0F, 0F)
    val logsTopAppBarState = TopAppBarState(0F, 0F, 0F)
    val logsLazyListState = LazyListState()
    val scrollState = ScrollState(0)

    var status by mutableStateOf(contextUtils.getString(R.string.dashboard_fetching))
        private set

    private var logs by mutableStateOf(listOf<EnsiLog>())
    var shownLogTypes = mutableStateListOf(*EnsiLogType.values())
    var logsReversed by mutableStateOf(false)
    val shownLogs: List<EnsiLog>
        get() = logs.filter {
            shownLogTypes.contains(it.type)
        }.let {
            // inverted logic to make the "reversed" one show oldest at top
            return@let if (!logsReversed) it.reversed() else it
        }

    val isFetching get() = apiViewModel.fetching

    suspend fun fetchStatus() {
        withContext(Dispatchers.IO) {
            val response = apiViewModel.doRequest(apiViewModel.apiData?.getStatus)
            status = response.summary
        }
    }

    suspend fun fetchLogs() {
        withContext(Dispatchers.IO) {
            try {
                val response = apiViewModel.doRequest(apiViewModel.apiData?.getLogs)
                if (response?.isSuccessful != true) return@withContext topToastState.toastSummary(response)
                logs = gson.fromJson(response.responseBody, Array<EnsiLog>::class.java).toList()
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

    suspend fun postEnsicordAddon() {
        withContext(Dispatchers.IO) {
            val response = apiViewModel.doRequest(apiViewModel.apiData?.postEnsicordAddon)
            handleSuccessResponse(response)
        }
    }

    suspend fun destroyProcess() {
        withContext(Dispatchers.IO) {
            val response = apiViewModel.doRequest(apiViewModel.apiData?.destroyProcess)
            handleSuccessResponse(response)
        }
    }

    private fun handleSuccessResponse(response: HTTPResponse?, onSuccess: (() -> Unit)? = null) {
        if (response?.statusCode == null) toastNoBody(null)
        else if (!WebUtil.statusCodeIsSuccess(response.statusCode)) toastError("[${response.statusCode}] ${response.responseBody}")
        else {
            topToastState.showToast("[${response.statusCode}] ${response.responseBody}", icon = Icons.Rounded.Done, iconTintColor = TopToastColor.PRIMARY)
            onSuccess?.invoke()
        }
    }

    private fun toastNoBody(statusCode: Int?) {
        toastError(contextUtils.getString(R.string.error_noBody).replace("%STATUS%", statusCode.toString()))
    }

    private fun toastError(text: String) {
        topToastState.showToast(text, icon = Icons.Rounded.PriorityHigh, iconTintColor = TopToastColor.ERROR)
    }
}