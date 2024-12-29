package com.aliernfrog.ensimanager.data.api

import androidx.compose.runtime.MutableState
import com.aliernfrog.ensimanager.enum.APILogType

data class APILog(
    val date: Long,
    val type: APILogType,
    val str: String,
    var timeStr: MutableState<String?>? = null
)
