package com.aliernfrog.ensimanager.util.extension

import android.content.Context
import android.text.format.DateUtils
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.aliernfrog.ensimanager.data.EnsiLog

var EnsiLog.timeStr by mutableStateOf<String?>(null)

fun EnsiLog.getTimeStr(
    context: Context,
    force: Boolean = false
): String {
    val cached = timeStr
    if (cached != null && !force) return cached
    val new = DateUtils.getRelativeDateTimeString(
        /* c = */ context,
        /* time = */ date,
        /* minResolution = */ DateUtils.SECOND_IN_MILLIS,
        /* transitionResolution = */ DateUtils.DAY_IN_MILLIS,
        /* flags = */ 0
    ).toString()
    timeStr = new
    return new
}