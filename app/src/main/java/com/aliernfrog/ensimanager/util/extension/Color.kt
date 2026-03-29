package com.aliernfrog.ensimanager.util.extension

import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.aliernfrog.ensimanager.TAG

fun Color.Companion.randomWithoutTransparency(): Color {
    val cl = Color(
        red = (0..255).random(),
        green = (0..255).random(),
        blue = (0..255).random(),
        alpha = 255
    )
    Log.d(TAG, "color: $cl, ${cl.toArgb()}, ${Color(cl.toArgb())}")
    return cl
}