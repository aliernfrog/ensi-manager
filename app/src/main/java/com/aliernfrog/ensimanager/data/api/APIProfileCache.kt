package com.aliernfrog.ensimanager.data.api

import com.aliernfrog.ensimanager.util.Destination

data class APIProfileCache(
    val endpoints: APIEndpoints? = null,
    val availableDestinations: List<Destination> = emptyList()
)
