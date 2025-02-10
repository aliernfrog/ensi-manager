package com.aliernfrog.ensimanager.util.staticutil

import android.content.Context
import android.os.Build
import android.util.Log
import com.aliernfrog.ensimanager.TAG
import com.aliernfrog.ensimanager.data.HTTPResponse
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class WebUtil {
    companion object {
        fun sendRequest(
            toUrl: String,
            method: String,
            authorization: String? = null,
            json: JSONObject? = null,
            userAgent: String
        ): HTTPResponse {
            return try {
                val url = URL(toUrl)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = method
                connection.setRequestProperty("User-Agent", userAgent)
                if (authorization != null) connection.setRequestProperty("Authorization", authorization)
                if (json != null) {
                    connection.doOutput = true
                    connection.outputStream.use {
                        it.write(json.toString().toByteArray(Charsets.UTF_8))
                    }
                }
                val response = getResponseFromConnection(connection)
                HTTPResponse(
                    statusCode = connection.responseCode,
                    responseBody = response
                )
            } catch (e: Exception) {
                Log.e(TAG, "sendRequest: ", e)
                HTTPResponse(
                    statusCode = null,
                    responseBody = null,
                    error = e.toString()
                )
            }
        }

        fun buildUserAgent(context: Context): String {
            return "EnsiManager/${GeneralUtil.getAppVersionCode(context)} (${context.packageName}), Android ${Build.VERSION.SDK_INT}"
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