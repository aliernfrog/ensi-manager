package com.aliernfrog.ensimanager.util

import com.aliernfrog.ensimanager.data.ApiResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

class WebUtil {
    companion object {
        suspend fun sendRequest(toUrl: String, method: String, authorization: String? = null): ApiResponse? {
            return try {
                val url = URL(toUrl)
                val connection = withContext(Dispatchers.IO) { url.openConnection() } as HttpURLConnection
                if (authorization != null) connection.setRequestProperty("Authorization", authorization)
                connection.requestMethod = method
                val response = connection.inputStream.bufferedReader().readText()
                ApiResponse(connection.responseCode, response)
            } catch (_: Exception) {
                null
            }
        }
    }
}