package com.aliernfrog.ensimanager.data

import androidx.compose.ui.graphics.painter.Painter

data class Screen(
    val route: String,
    val name: String,
    val icon: Painter,
    val showInNavigationBar: Boolean = false
)
