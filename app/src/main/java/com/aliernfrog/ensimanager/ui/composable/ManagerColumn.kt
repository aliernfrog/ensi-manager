package com.aliernfrog.ensimanager.ui.composable

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.aliernfrog.ensimanager.ManagerComposableShape

@Composable
fun ManagerColumn(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.surfaceVariant,
    title: String? = null,
    titleColor: Color = MaterialTheme.colorScheme.primary,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    var columnModifier = modifier.fillMaxWidth().padding(8.dp).clip(ManagerComposableShape)
    if (onClick != null) columnModifier = columnModifier.clickable { onClick() }
    Column(columnModifier.background(color).animateContentSize().padding(8.dp)) {
        if (title != null) Text(text = title, color = titleColor, modifier = Modifier.padding(horizontal = 8.dp))
        content()
    }
}