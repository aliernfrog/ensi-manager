package com.aliernfrog.ensimanager.data

data class HTTPResponse(
    val statusCode: Int?,
    val responseBody: String?,
    val error: String? = null
)
