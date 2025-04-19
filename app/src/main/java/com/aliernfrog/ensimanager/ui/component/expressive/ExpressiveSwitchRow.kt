package com.aliernfrog.ensimanager.ui.component.expressive

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun ExpressiveSwitchRow(
    title: String,
    checked: Boolean,
    modifier: Modifier = Modifier,
    description: String? = null,
    painter: Painter? = null,
    enabled: Boolean = true,
    containerColor: Color = Color.Transparent,
    contentColor: Color =
        if (containerColor == Color.Transparent) MaterialTheme.colorScheme.onSurface
        else contentColorFor(containerColor),
    iconContainerColor: Color = MaterialTheme.colorScheme.primaryContainer,
    iconColor: Color = contentColorFor(iconContainerColor),
    iconColorFilter: ColorFilter? = ColorFilter.tint(iconColor),
    iconSize: Dp = 24.dp,
    onCheckedChange: (Boolean) -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }

    ExpressiveButtonRow(
        title = title,
        modifier = modifier,
        description = description,
        painter = painter,
        enabled = enabled,
        trailingComponent = {
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                enabled = enabled,
                interactionSource = interactionSource
            )
        },
        containerColor = containerColor,
        contentColor = contentColor,
        iconContainerColor = iconContainerColor,
        iconColor = iconColor,
        iconColorFilter = iconColorFilter,
        iconSize = iconSize,
        interactionSource = interactionSource,
        onClick = {
            onCheckedChange(!checked)
        }
    )
}