package com.aliernfrog.ensimanager.ui.composable

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun ManagerFAB(
    icon: ImageVector,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    contentColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    onClick: () -> Unit
) {
    FloatingActionButton(
        onClick = onClick,
        modifier = modifier.padding(8.dp),
        shape = RoundedCornerShape(20.dp),
        containerColor = containerColor,
        contentColor = contentColor
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = contentColor)
    }
}