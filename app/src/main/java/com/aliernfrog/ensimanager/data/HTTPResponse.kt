package com.aliernfrog.ensimanager.data

data class HTTPResponse(
    val statusCode: Int?,
    val responseBody: String?,
    val error: String? = null,
    val sslPublicKey: String? = null
)

val HTTPResponse?.isSuccessful
    get() = this?.error == null && this?.statusCode.toString().startsWith("2")

val HTTPResponse?.summary
    get() = this?.error ?: "[${this?.statusCode}] ${this?.responseBody}"