package com.aliernfrog.ensimanager.data.api

import com.google.gson.annotations.SerializedName

data class APIEndpoints(
    // BEGIN OF PROPERTIES APIs CAN PROVIDE
    val metadata: APIMetadata?,

    val getDashboard: APIEndpoint?,

    val getLogs: APIEndpoint?,

    @SerializedName(value = "getStrings", alternate = ["getChatCategories"])
    val getStrings: APIEndpoint?,

    @SerializedName(value = "addString", alternate = ["addChatCategory"])
    val addString: APIEndpoint?,

    @SerializedName(value = "deleteString", alternate = ["deleteChatCategory"])
    val deleteString: APIEndpoint?,

    /**
     * If this is not null, app will automatically set the endpoints URL to the URL specified here.
     */
    val migration: APIEndpoint? = null,

    // END OF PROPERTIES APIs CAN PROVIDE

    // Properties below are only created and used by the app. Values specified by APIs will be ignored for those!
    internal val sslPublicKey: String? = null,
    internal val deprecatedEndpoints: Map<String, String>? = null
)

val DEPRECATED_ENDPOINTS = mapOf(
    "getChatCategories" to "getStrings",
    "addChatCategory" to "addString",
    "deleteChatCategory" to "deleteString"
)