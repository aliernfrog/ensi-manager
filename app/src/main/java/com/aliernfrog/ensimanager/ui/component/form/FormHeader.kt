package com.aliernfrog.ensimanager.ui.component.form

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun FormHeader(
    title: String,
    modifier: Modifier = Modifier,
    description: String? = null,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    painter: Painter? = null,
    iconColorFilter: ColorFilter? = ColorFilter.tint(contentColor),
    iconSize: Dp = 24.dp
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        painter?.let {
            Image(
                painter = painter,
                contentDescription = null,
                modifier = Modifier.padding(end = 18.dp).size(iconSize),
                colorFilter = iconColorFilter,
            )
        }
        Column(
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
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
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurface.copy(0.6f)
                    ),
                    modifier = Modifier.animateContentSize()
                )
            }
        }
    }
}