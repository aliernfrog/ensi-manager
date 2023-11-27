package com.aliernfrog.ensimanager.util.extension

import com.aliernfrog.ensimanager.data.HTTPResponse

val HTTPResponse?.isSuccessful
    get() = this?.error == null && this?.statusCode.toString().startsWith("2")

val HTTPResponse?.summary
    get() = this?.error ?: "[${this?.statusCode}] ${this?.responseBody}"