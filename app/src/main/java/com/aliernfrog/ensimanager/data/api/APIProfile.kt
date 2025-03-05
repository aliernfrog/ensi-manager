package com.aliernfrog.ensimanager.data.api

import com.aliernfrog.ensimanager.data.HTTPResponse
import com.aliernfrog.ensimanager.di.getKoinInstance
import com.aliernfrog.ensimanager.ui.viewmodel.APIViewModel
import com.aliernfrog.ensimanager.util.staticutil.WebUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

data class APIProfile(
    val name: String,
    val endpointsURL: String,
    val authorization: String,
    val trustedSha256: String? = null
)

val APIProfile.id: String
    get() = endpointsURL

val APIProfile.cache: APIProfileCache?
    get() {
        val apiViewModel = getKoinInstance<APIViewModel>()
        return apiViewModel.getProfileCache(this)
    }

val APIProfile.isAvailable: Boolean
    get() = cache?.let {
        it.endpoints != null && it.endpoints.migration == null && it.availableDestinations.any { dest ->
            dest.isAvailableInEndpoints != null
        }
    } == true

suspend fun APIProfile.doRequest(endpointSelector: (APIEndpoints) -> APIEndpoint?, body: JSONObject? = null): HTTPResponse {
    val apiViewModel = getKoinInstance<APIViewModel>()
    val endpoint = cache?.endpoints?.let {
        endpointSelector(it) ?: return HTTPResponse(0, "Endpoint is null", error = "Endpoint is null")
    } ?: return HTTPResponse(0, "Endpoints data is null", error = "Endpoints data is null")
    apiViewModel.isChosenProfileFetching = true
    return withContext(Dispatchers.IO) {
        val response = WebUtil.sendRequest(
            toUrl = endpoint.url,
            method = endpoint.method,
            authorization = if (endpoint.requiresAuth) authorization else null,
            json = body,
            pinnedSha256 = trustedSha256,
            userAgent = apiViewModel.userAgent
        )
        apiViewModel.isChosenProfileFetching = false
        return@withContext response
    }
}