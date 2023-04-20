package com.aliernfrog.ensimanager.state

import android.content.Context
import androidx.compose.foundation.ScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material.icons.rounded.PriorityHigh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarState
import androidx.compose.runtime.mutableStateOf
import com.aliernfrog.ensimanager.FetchingState
import com.aliernfrog.ensimanager.R
import com.aliernfrog.ensimanager.data.HTTPResponse
import com.aliernfrog.ensimanager.util.staticutil.WebUtil
import com.aliernfrog.toptoast.enum.TopToastColor
import com.aliernfrog.toptoast.state.TopToastState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
class DashboardState(
    private val topToastState: TopToastState,
    private val apiState: EnsiAPIState
) {
    val topAppBarState = TopAppBarState(0F, 0F, 0F)
    val scrollState = ScrollState(0)

    val status = mutableStateOf("Fetching...")
    val fetchingState = mutableStateOf(FetchingState.FETCHING)

    suspend fun fetchStatus() {
        fetchingState.value = FetchingState.FETCHING
        withContext(Dispatchers.IO) {
            val response = apiState.doRequest(apiState.apiData?.getStatus)
            status.value = "[${response?.statusCode}] ${response?.responseBody}"
            fetchingState.value = FetchingState.DONE
        }
    }

    suspend fun postAddon(context: Context) {
        fetchingState.value = FetchingState.FETCHING
        withContext(Dispatchers.IO) {
            handleSuccessResponse(
                response = apiState.doRequest(apiState.apiData?.postEnsicordAddon),
                context = context
            )
            fetchingState.value = FetchingState.DONE
        }
    }

    suspend fun destroyProcess(context: Context) {
        fetchingState.value = FetchingState.FETCHING
        withContext(Dispatchers.IO) {
            handleSuccessResponse(
                response = apiState.doRequest(apiState.apiData?.destroyProcess),
                context = context
            )
            fetchingState.value = FetchingState.DONE
        }
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