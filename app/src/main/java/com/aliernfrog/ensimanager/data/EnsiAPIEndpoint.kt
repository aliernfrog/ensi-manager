package com.aliernfrog.ensimanager.data

data class EnsiAPIEndpoint(
    val url: String,
    val method: String,
    val requiresAuth: Boolean
)
