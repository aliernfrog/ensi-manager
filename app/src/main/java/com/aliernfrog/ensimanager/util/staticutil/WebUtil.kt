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
import java.security.PublicKey

class WebUtil {
    companion object {
        fun sendRequest(
            toUrl: String,
            method: String,
            authorization: String? = null,
            json: JSONObject? = null,
            pinnedPublicKey: String? = null,
            userAgent: String
        ): HTTPResponse {
            return try {
                val url = URL(toUrl)
                val client = pinnedPublicKey?.let {
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
                    val shaKey = response.handshake?.peerCertificates?.firstOrNull()?.let {
                        getPublicKeyHash(it.publicKey)
                    }
                    return HTTPResponse(
                        statusCode = response.code,
                        responseBody = response.body?.string(),
                        sslPublicKey = shaKey
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

        private fun getPublicKeyHash(publicKey: PublicKey): String =
            "sha256/"+Base64.encodeToString(publicKey.encoded, Base64.DEFAULT)

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