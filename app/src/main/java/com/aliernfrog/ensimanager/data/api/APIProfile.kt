package com.aliernfrog.ensimanager.data.api

import com.aliernfrog.ensimanager.di.getKoinInstance
import com.aliernfrog.ensimanager.ui.viewmodel.APIViewModel

data class APIProfile(
    val name: String,
    val endpointsURL: String,
    val authorization: String
)

val APIProfile.id: String
    get() = endpointsURL

val APIProfile.cache: APIProfileCache?
    get() {
        val apiViewModel = getKoinInstance<APIViewModel>()
        return apiViewModel.getProfileCache(this)
    }