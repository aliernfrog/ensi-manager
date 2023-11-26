package com.aliernfrog.ensimanager.enum

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Suppress("unused") // actually used by GSON
enum class EnsiLogType(
    val symbol: Char,
    val getColor: @Composable () -> Color
) {
    LOG(
        symbol = 'D',
        getColor = {
            MaterialTheme.colorScheme.surfaceContainerHigh
        }
    ),

    WARN(
        symbol = 'W',
        getColor = {
            MaterialTheme.colorScheme.primary
        }
    ),

    ERROR(
        symbol = 'E',
        getColor = {
            MaterialTheme.colorScheme.error
        }
    )
}