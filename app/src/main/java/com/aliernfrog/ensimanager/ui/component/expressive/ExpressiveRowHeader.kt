package com.aliernfrog.ensimanager.ui.component.expressive

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ExpressiveRowHeader(
    title: String,
    modifier: Modifier = Modifier,
    description: String? = null,
    painter: Painter? = null,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    iconContainerColor: Color = MaterialTheme.colorScheme.primaryContainer,
    iconColor: Color = contentColorFor(iconContainerColor),
    iconColorFilter: ColorFilter? = ColorFilter.tint(iconColor),
    iconSize: Dp = ROW_DEFAULT_ICON_SIZE,
    iconShape: Shape = RectangleShape,
    showIconContainer: Boolean = iconSize <= ROW_DEFAULT_ICON_SIZE
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        painter?.let {
            Image(
                painter = painter,
                contentDescription = null,
                modifier = Modifier
                    .padding(end = 16.dp)
                    .clip(CircleShape)
                    .let {
                        if (showIconContainer) it
                          .background(iconContainerColor)
                          .alpha(0.8f)
                        else it
                    }
                    .padding(8.dp)
                    .size(iconSize)
                    .clip(iconShape),
                colorFilter = iconColorFilter
            )
        }
        Column {
            Text(
                text = title,
                color = contentColor,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Normal,
                    fontSize = 19.sp
                ),
                modifier = Modifier.animateContentSize()
            )
            description?.let {
                Text(
                    text = description,
                    color = contentColor,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .alpha(0.7f)
                        .animateContentSize()
                )
            }
        }
    }
}