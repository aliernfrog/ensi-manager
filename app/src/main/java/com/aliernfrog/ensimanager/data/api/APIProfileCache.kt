package com.aliernfrog.ensimanager.data.api

import com.aliernfrog.ensimanager.util.MainDestination

data class APIProfileCache(
    val endpoints: APIEndpoints? = null,
    val availableDestinations: List<MainDestination> = emptyList()
)
