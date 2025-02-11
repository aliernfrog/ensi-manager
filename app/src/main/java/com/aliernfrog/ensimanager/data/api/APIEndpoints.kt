package com.aliernfrog.ensimanager.data.api

data class APIEndpoints(
    val metadata: APIMetadata?,

    val getDashboard: APIEndpoint,
    val getLogs: APIEndpoint,
    val getChatCategories: APIEndpoint,
    val addChatCategory: APIEndpoint,
    val deleteChatCategory: APIEndpoint,

    /**
     * If this is not null, app will automatically set the endpoints URL to the URL specified here.
     */
    val migration: APIEndpoint? = null,

    /**
     * Fields below are only created and used by the app. No need to provide them!
     */
    internal val sslPublicKey: String? = null
)
