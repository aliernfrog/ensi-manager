package com.aliernfrog.ensimanager.data

import androidx.compose.runtime.MutableState
import com.aliernfrog.ensimanager.enum.EnsiLogType

data class EnsiLog(
    val date: Long,
    val type: EnsiLogType,
    val str: String,
    var timeStr: MutableState<String?>? = null
)
