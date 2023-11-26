package com.aliernfrog.ensimanager.util.extension

import com.aliernfrog.ensimanager.data.EnsiAPIEndpoint
import com.aliernfrog.ensimanager.data.HTTPResponse
import com.aliernfrog.ensimanager.util.staticutil.WebUtil
import org.json.JSONObject

fun EnsiAPIEndpoint.doRequest(
    json: JSONObject? = null,
    authorization: String? = null,
    userAgent: String
): HTTPResponse {
    return WebUtil.sendRequest(
        toUrl = this.url,
        method = this.method,
        authorization = if (this.requiresAuth) authorization else null,
        json = json,
        userAgent = userAgent
    )
}