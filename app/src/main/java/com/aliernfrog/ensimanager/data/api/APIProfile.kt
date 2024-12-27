package com.aliernfrog.ensimanager.data.api

data class APIProfile(
    val name: String,
    val iconModel: String,
    val endpointsURL: String,
    val authorization: String
)

val APIProfile.id: String
    get() = endpointsURL