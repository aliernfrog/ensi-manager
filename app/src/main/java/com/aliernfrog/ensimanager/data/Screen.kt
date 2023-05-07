package com.aliernfrog.ensimanager.data

import androidx.compose.ui.graphics.painter.Painter

data class Screen(
    val route: String,
    val name: String,
    val iconFilled: Painter?,
    val iconOutlined: Painter?,
    val hideBottomBar: Boolean = false
)
