package com.aliernfrog.ensimanager.util.extension

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material.ripple
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

fun Modifier.clickableWithColor(
    color: Color,
    interactionSource: MutableInteractionSource? = null,
    onClick: () -> Unit
): Modifier = composed {
    this.clickable(
        interactionSource = interactionSource,
        indication = ripple(color = color),
        onClick = onClick
    )
}

fun Modifier.horizontalFadingEdge(
    scrollState: ScrollState,
    edgeColor: Color,
    isRTL: Boolean
): Modifier {
    return this.drawWithContent {
        val lengthPx = 100.dp.toPx()
        val scrollFromStart = if (isRTL) scrollState.maxValue - scrollState.value else scrollState.value
        val scrollFromEnd = if (isRTL) scrollState.value else scrollState.maxValue - scrollState.value
        val startFadingEdgeStrength = lengthPx * (scrollFromStart / lengthPx).coerceAtMost(1f)
        val endFadingEdgeStrength = lengthPx * (scrollFromEnd / lengthPx).coerceAtMost(1f)

        drawContent()

        drawRect(
            brush = Brush.horizontalGradient(
                colors = listOf(edgeColor, Color.Transparent),
                startX = 0f,
                endX = startFadingEdgeStrength
            ),
            size = Size(
                width = startFadingEdgeStrength,
                height = this.size.height
            )
        )

        drawRect(
            brush = Brush.horizontalGradient(
                colors = listOf(Color.Transparent, edgeColor),
                startX = size.width - endFadingEdgeStrength,
                endX = size.width
            ),
            topLeft = Offset(
                x = size.width - endFadingEdgeStrength,
                y = 0f
            )
        )
    }
}