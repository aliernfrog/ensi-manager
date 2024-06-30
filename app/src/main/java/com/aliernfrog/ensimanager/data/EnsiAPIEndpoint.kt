package com.aliernfrog.ensimanager.data

import com.aliernfrog.ensimanager.di.getKoinInstance
import com.aliernfrog.ensimanager.ui.viewmodel.APIViewModel
import com.aliernfrog.ensimanager.util.staticutil.WebUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

data class EnsiAPIEndpoint(
    val url: String,
    val method: String,
    val requiresAuth: Boolean
)

suspend fun EnsiAPIEndpoint.doRequest(body: JSONObject? = null): HTTPResponse {
    val apiViewModel = getKoinInstance<APIViewModel>()
    apiViewModel.fetching = true
    return withContext(Dispatchers.IO) {
        WebUtil.sendRequest(
            toUrl = url,
            method = method,
            authorization = if (requiresAuth) apiViewModel.setupAuthorization else null,
            json = body,
            userAgent = apiViewModel.userAgent
        )
    }
}