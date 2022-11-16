package com.aliernfrog.ensimanager.util

import android.util.Log
import com.aliernfrog.ensimanager.data.ApiResponse
import java.io.BufferedReader
import java.io.InputStreamReader
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
                Log.d("TAG", "sendRequest: ${connection.responseCode} $response")
                ApiResponse(connection.responseCode, response)
            } catch (_: Exception) {
                null
            }
        }

        private fun getResponseFromConnection(connection: HttpURLConnection): String {
            return try {
                var response = ""
                val bufferedReader = BufferedReader(InputStreamReader(connection.inputStream, "utf-8"))
                bufferedReader.lines().forEach {
                    response += "\n$it"
                }
                bufferedReader.close()
                response.removePrefix("\n")
            } catch (_: Exception) {
                ""
            }
        }
    }
}