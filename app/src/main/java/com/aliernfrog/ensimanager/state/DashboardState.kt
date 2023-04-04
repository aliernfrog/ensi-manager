package com.aliernfrog.ensimanager.state

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.foundation.ScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material.icons.rounded.PriorityHigh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarState
import androidx.compose.runtime.mutableStateOf
import com.aliernfrog.ensimanager.ConfigKey
import com.aliernfrog.ensimanager.FetchingState
import com.aliernfrog.ensimanager.R
import com.aliernfrog.ensimanager.data.ApiResponse
import com.aliernfrog.ensimanager.data.ApiRoute
import com.aliernfrog.ensimanager.util.staticutil.GeneralUtil
import com.aliernfrog.ensimanager.util.staticutil.WebUtil
import com.aliernfrog.toptoast.enum.TopToastColor
import com.aliernfrog.toptoast.state.TopToastState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
class DashboardState(
    private val config: SharedPreferences,
    private val topToastState: TopToastState
) {
    val topAppBarState = TopAppBarState(0F, 0F, 0F)
    val scrollState = ScrollState(0)

    val status = mutableStateOf("Fetching...")
    val fetchingState = mutableStateOf(FetchingState.FETCHING)

    private var authorization: String? = null
    private var getStatusRoute: ApiRoute? = null
    private var postAddonRoute: ApiRoute? = null
    private var destroyProcessRoute: ApiRoute? = null

    fun updateApiProperties() {
        authorization = config.getString(ConfigKey.KEY_API_AUTHORIZATION, null)
        getStatusRoute = GeneralUtil.getApiRouteFromString(config.getString(ConfigKey.KEY_API_STATUS_GET, ""))
        postAddonRoute = GeneralUtil.getApiRouteFromString(config.getString(ConfigKey.KEY_API_POST_ADDON, ""))
        destroyProcessRoute = GeneralUtil.getApiRouteFromString(config.getString(ConfigKey.KEY_API_PROCESS_DESTROY, ""))
    }

    suspend fun fetchStatus(context: Context) {
        if (routeInvalid(getStatusRoute)) return toastInvalidRoute(context)
        fetchingState.value = FetchingState.FETCHING
        withContext(Dispatchers.IO) {
            val response = WebUtil.sendRequest(getStatusRoute!!.url, getStatusRoute!!.method, authorization)
            status.value = "[${response?.statusCode}] ${response?.responseBody}"
            fetchingState.value = FetchingState.DONE
        }
    }

    suspend fun postAddon(context: Context) {
        if (routeInvalid(postAddonRoute)) return toastInvalidRoute(context)
        fetchingState.value = FetchingState.FETCHING
        withContext(Dispatchers.IO) {
            handleSuccessResponse(WebUtil.sendRequest(postAddonRoute!!.url, postAddonRoute!!.method, authorization), context)
            fetchingState.value = FetchingState.DONE
        }
    }

    suspend fun destroyProcess(context: Context) {
        if (routeInvalid(destroyProcessRoute)) return toastInvalidRoute(context)
        fetchingState.value = FetchingState.FETCHING
        withContext(Dispatchers.IO) {
            handleSuccessResponse(WebUtil.sendRequest(destroyProcessRoute!!.url, destroyProcessRoute!!.method, authorization), context)
            fetchingState.value = FetchingState.DONE
        }
    }

    private fun handleSuccessResponse(response: ApiResponse?, context: Context, onSuccess: (() -> Unit)? = null) {
        if (response?.statusCode == null) toastNoBody(context, null)
        else if (!WebUtil.statusCodeIsSuccess(response.statusCode)) toastError("[${response.statusCode}] ${response.responseBody}")
        else {
            topToastState.showToast("[${response.statusCode}] ${response.responseBody}", icon = Icons.Rounded.Done, iconTintColor = TopToastColor.PRIMARY)
            if (onSuccess != null) onSuccess()
        }
    }

    private fun routeInvalid(route: ApiRoute?): Boolean {
        if (route == null) return true
        if (!route.url.contains("://")) return true
        return false
    }

    private fun toastInvalidRoute(context: Context) {
        toastError(context.getString(R.string.error_invalidRoute))
    }

    private fun toastNoBody(context: Context, statusCode: Int?) {
        toastError(context.getString(R.string.error_noBody).replace("%STATUS%", statusCode.toString()))
    }

    private fun toastError(text: String) {
        topToastState.showToast(text, icon = Icons.Rounded.PriorityHigh, iconTintColor = TopToastColor.ERROR)
    }
}