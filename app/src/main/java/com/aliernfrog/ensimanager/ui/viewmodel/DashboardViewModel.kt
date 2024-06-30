package com.aliernfrog.ensimanager.ui.viewmodel

import androidx.compose.foundation.ScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material.icons.rounded.PriorityHigh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.aliernfrog.ensimanager.R
import com.aliernfrog.ensimanager.data.HTTPResponse
import com.aliernfrog.ensimanager.data.doRequest
import com.aliernfrog.ensimanager.util.extension.summary
import com.aliernfrog.ensimanager.util.manager.ContextUtils
import com.aliernfrog.ensimanager.util.staticutil.WebUtil
import com.aliernfrog.toptoast.enum.TopToastColor
import com.aliernfrog.toptoast.state.TopToastState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
class DashboardViewModel(
    private val contextUtils: ContextUtils,
    private val topToastState: TopToastState,
    private val apiViewModel: APIViewModel
) : ViewModel() {
    val topAppBarState = TopAppBarState(0F, 0F, 0F)
    val scrollState = ScrollState(0)

    var status by mutableStateOf(contextUtils.getString(R.string.dashboard_fetching))
        private set

    val isFetching get() = apiViewModel.fetching

    suspend fun fetchStatus() {
        withContext(Dispatchers.IO) {
            val response = apiViewModel.apiData?.getStatus?.doRequest()
            status = response.summary
        }
    }

    suspend fun postEnsicordAddon() {
        withContext(Dispatchers.IO) {
            val response = apiViewModel.apiData?.postEnsicordAddon?.doRequest()
            handleSuccessResponse(response)
        }
    }

    suspend fun destroyProcess() {
        withContext(Dispatchers.IO) {
            val response = apiViewModel.apiData?.destroyProcess?.doRequest()
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