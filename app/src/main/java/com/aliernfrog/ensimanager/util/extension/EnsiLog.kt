package com.aliernfrog.ensimanager.util.extension

import android.content.Context
import android.text.format.DateUtils
import androidx.compose.runtime.mutableStateOf
import com.aliernfrog.ensimanager.data.EnsiLog
import java.text.SimpleDateFormat
import java.util.Calendar

fun EnsiLog.getTimeStr(
    context: Context,
    force: Boolean = false
): String {
    if (timeStr == null) timeStr = mutableStateOf(null)
    val cached = timeStr?.value
    if (cached != null && !force) return cached
    val formatter = SimpleDateFormat("dd/MM/yyyy hh:mm:ss.SSS", context.resources.configuration.locales[0])
    val calendar = Calendar.getInstance()
    val relative = DateUtils.getRelativeTimeSpanString(
        /* time = */ date,
        /* now = */ calendar.timeInMillis,
        /* minResolution = */ DateUtils.SECOND_IN_MILLIS
    )
    calendar.timeInMillis = date
    val new = "${formatter.format(calendar.time)} - $relative"
    timeStr?.value = new
    return new
}