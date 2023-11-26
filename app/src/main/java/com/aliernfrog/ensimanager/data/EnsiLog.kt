package com.aliernfrog.ensimanager.data

import com.aliernfrog.ensimanager.enum.EnsiLogType

data class EnsiLog(
    val date: Long,
    val type: EnsiLogType,
    val str: String
)
