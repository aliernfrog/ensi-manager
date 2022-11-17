package com.aliernfrog.ensimanager.util

import com.aliernfrog.ensimanager.data.ApiResponse
import java.net.HttpURLConnection
import java.net.URL

class WebUtil {
    companion object {
        fun sendRequest(toUrl: String, method: String, authorization: String? = null): ApiResponse? {
            return try {
                val url = URL(toUrl)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = method
                if (authorization != null) connection.setRequestProperty("Authorization", authorization)
                val response = getResponseFromConnection(connection)
                ApiResponse(connection.responseCode, response)
            } catch (_: Exception) {
                null
            }
        }

        private fun getResponseFromConnection(connection: HttpURLConnection): String {
            return try {
                connection.inputStream.bufferedReader().readText().ifBlank {
                    connection.errorStream.bufferedReader().readText()
                }
            } catch (_: Exception) {
                ""
            }
        }
    }
}