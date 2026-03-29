package com.aliernfrog.ensimanager.impl.api

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.aliernfrog.ensimanager.R
import com.aliernfrog.ensimanager.TAG
import com.aliernfrog.ensimanager.data.HTTPResponse
import com.aliernfrog.ensimanager.data.api.APIEndpoint
import com.aliernfrog.ensimanager.data.api.APIEndpoints
import com.aliernfrog.ensimanager.data.api.DEPRECATED_ENDPOINTS
import com.aliernfrog.ensimanager.data.isSuccessful
import com.aliernfrog.ensimanager.data.summary
import com.aliernfrog.ensimanager.domain.APIState
import com.aliernfrog.ensimanager.repository.APIProfileRepository
import com.aliernfrog.ensimanager.util.MainDestination
import com.aliernfrog.ensimanager.util.staticutil.WebUtil
import com.google.gson.Gson
import io.github.aliernfrog.shared.impl.ContextUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.String
import kotlin.collections.component1
import kotlin.collections.component2

class APIProfile(
    val name: String,
    val endpointsURL: String,
    val authorization: String,
    val trustedSha256: String? = null,
    val manualFetch: Boolean = false
): KoinComponent {
    val id = endpointsURL

    var endpoints by mutableStateOf<APIEndpoints?>(null)
        private set

    var isFetching by mutableStateOf(false)
    var error by mutableStateOf<String?>(null)
    var migratedTo by mutableStateOf<String?>(null)

    val availableDestinations: List<MainDestination>
        get() = endpoints?.let {
            MainDestination.entries.filter { destination ->
                destination.isAvailableInEndpoints(it)
            }
        } ?: emptyList()

    val isAvailable: Boolean
        get() = availableDestinations.isNotEmpty() && endpoints != null && endpoints?.migration == null

    suspend fun doRequest(endpointSelector: (APIEndpoints) -> APIEndpoint?, body: JSONObject? = null): HTTPResponse {
        val apiState by inject<APIState>()
        val endpoint = endpoints?.let {
            endpointSelector(it)
        } ?: return HTTPResponse(
            statusCode = 0,
            responseBody = "Endpoints data is null",
            error = "Endpoints data is null"
        )

        return withContext(Dispatchers.IO) {
            isFetching = true
            val response = WebUtil.sendRequest(
                toUrl = endpoint.url,
                method = endpoint.method,
                authorization = if (endpoint.requiresAuth) authorization else null,
                json = body,
                pinnedSha256 = trustedSha256,
                userAgent = apiState.userAgent
            )
            isFetching = false
            return@withContext response
        }
    }

    suspend fun fetchAPIEndpoints(): APIEndpoints? {
        val apiProfileRepository by inject<APIProfileRepository>()
        val apiState by inject<APIState>()
        val gson by inject<Gson>()
        val contextUtils by inject<ContextUtils>()

        isFetching = true
        val res = withContext(Dispatchers.IO) {
            try {
                val isAlreadySaved = apiProfileRepository.apiProfiles.value.any {
                    it.id == this@APIProfile.id
                }
                val response = WebUtil.sendRequest(
                    toUrl = this@APIProfile.endpointsURL,
                    method = "GET",
                    pinnedSha256 = this@APIProfile.trustedSha256,
                    userAgent = apiState.userAgent
                )
                if (response.isSuccessful) {
                    endpoints = response.responseBody.let { body ->
                        gson.fromJson(body, APIEndpoints::class.java)?.copy(
                            sslPublicKey = response.certSha256,
                            deprecatedEndpoints = findDeprecatedEndpoints(body.orEmpty())
                        )
                    }
                    migratedTo = endpoints?.migration?.url
                    error = null
                    return@withContext endpoints
                } else {
                    error = if (response.error != WebUtil.SEND_REQUEST_SHA256_UNMATCH_ERROR) response.summary
                    else contextUtils.getString(
                        if (isAlreadySaved) R.string.profiles_sha256fail
                        else R.string.profiles_sha256fail_unsaved
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "fetchAPIEndpoints: failed to fetch endpoints for ${this@APIProfile.id}", e)
                error = e.toString()
            }
            return@withContext null
        }
        isFetching = false
        return res
    }

    private fun findDeprecatedEndpoints(jsonString: String): Map<String, String> = try {
        val json = JSONObject(jsonString)
        DEPRECATED_ENDPOINTS.filter { (old, new) ->
            json.has(old) && !json.has(new)
        }
    } catch (_: Exception) {
        emptyMap()
    }

    fun copy(
        name: String = this.name,
        endpointsURL: String = this.endpointsURL,
        authorization: String = this.authorization,
        trustedSha256: String? = this.trustedSha256
    ): APIProfile = APIProfile(
        name = name,
        endpointsURL = endpointsURL,
        authorization = authorization,
        trustedSha256
    )
}