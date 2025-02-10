package com.aliernfrog.ensimanager.data.api

data class APIEndpoint(
    val url: String,
    val method: String,
    val requiresAuth: Boolean
)