package com.aliernfrog.ensimanager.util.staticutil

import android.content.Context
import android.os.Build
import android.util.Base64
import android.util.Log
import com.aliernfrog.ensimanager.TAG
import com.aliernfrog.ensimanager.data.HTTPResponse
import okhttp3.CertificatePinner
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.net.URL
import java.security.MessageDigest

class WebUtil {
    companion object {
        fun sendRequest(
            toUrl: String,
            method: String,
            authorization: String? = null,
            json: JSONObject? = null,
            pinnedSha256: String? = null,
            userAgent: String
        ): HTTPResponse {
            return try {
                val url = URL(toUrl)
                val client = pinnedSha256?.let {
                    val certificatePinner = CertificatePinner.Builder()
                        .add(url.host, it).build()
                    OkHttpClient.Builder().certificatePinner(certificatePinner).build()
                } ?: OkHttpClient()
                val request = Request.Builder()
                    .url(url)
                    .method(method, json?.let {
                        json.toString().toRequestBody("application/json".toMediaType())
                    })
                    .addHeader("User-Agent", userAgent)
                    .let {
                        if (authorization != null) it.addHeader("Authorization", authorization)
                        else it
                    }
                    .build()
                client.newCall(request).execute().use { response ->
                    val x509 = response.handshake?.peerCertificates?.firstOrNull()
                    val hash = x509?.encoded?.let {
                        MessageDigest.getInstance("SHA-256").digest(it)
                    }
                    val sha256 = "sha256/"+Base64.encodeToString(hash, Base64.NO_WRAP)
                    return HTTPResponse(
                        statusCode = response.code,
                        responseBody = response.body?.string(),
                        certSha256 = sha256
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "sendRequest: ", e)
                HTTPResponse(
                    statusCode = null,
                    responseBody = null,
                    error = e.toString()
                )
            }
        }

        fun buildUserAgent(context: Context): String =
            "EnsiManager/${GeneralUtil.getAppVersionCode(context)} (${context.packageName}), Android ${Build.VERSION.SDK_INT}"

        /*private fun getResponseFromConnection(connection: HttpURLConnection): String {
            return try {
                try {
                    connection.inputStream.bufferedReader().readText()
                } catch (_: Exception) {
                    connection.errorStream.bufferedReader().readText()
                }
            } catch (e: Exception) {
                ""
            }
        }*/
    }
}