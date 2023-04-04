package com.aliernfrog.ensimanager.util.staticutil

import com.aliernfrog.ensimanager.data.ApiResponse
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class WebUtil {
    companion object {
        fun sendRequest(toUrl: String, method: String, authorization: String? = null, json: JSONObject? = null): ApiResponse? {
            return try {
                val url = URL(toUrl)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = method
                if (authorization != null) connection.setRequestProperty("Authorization", authorization)
                if (json != null) {
                    connection.doOutput = true
                    connection.outputStream.use {
                        it.write(json.toString().toByteArray(Charsets.UTF_8))
                    }
                }
                val response = getResponseFromConnection(connection)
                ApiResponse(connection.responseCode, response)
            } catch (_: Exception) {
                null
            }
        }

        fun statusCodeIsSuccess(statusCode: Int): Boolean {
            return statusCode < 400
        }

        private fun getResponseFromConnection(connection: HttpURLConnection): String {
            return try {
                try {
                    connection.inputStream.bufferedReader().readText()
                } catch (_: Exception) {
                    connection.errorStream.bufferedReader().readText()
                }
            } catch (e: Exception) {
                ""
            }
        }
    }
}