package com.aliernfrog.ensimanager.enum

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.aliernfrog.ensimanager.R

@Suppress("unused") // actually used by GSON
enum class APILogType(
    val nameId: Int,
    val symbol: Char,
    val getColor: @Composable () -> Color
) {
    LOG(
        nameId = R.string.logs_type_log,
        symbol = 'D',
        getColor = {
            MaterialTheme.colorScheme.surfaceContainerHigh
        }
    ),

    WARN(
        nameId = R.string.logs_type_warn,
        symbol = 'W',
        getColor = {
            MaterialTheme.colorScheme.primary
        }
    ),

    ERROR(
        nameId = R.string.logs_type_error,
        symbol = 'E',
        getColor = {
            MaterialTheme.colorScheme.error
        }
    )
}