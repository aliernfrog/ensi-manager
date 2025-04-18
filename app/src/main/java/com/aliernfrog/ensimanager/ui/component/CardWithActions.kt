package com.aliernfrog.ensimanager.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp

@Composable
fun CardWithActions(
    title: String?,
    modifier: Modifier = Modifier,
    icon: Painter? = null,
    buttons: @Composable () -> Unit,
    content: @Composable () -> Unit
) {
    Card(
        shape = RoundedCornerShape(SEGMENTOR_ROUNDNESS),
        modifier = modifier.width(IntrinsicSize.Max)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (title != null || icon != null) Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                icon?.let {
                    Icon(
                        painter = it,
                        contentDescription = null,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }
                title?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            }
            content()
            Row(
                modifier = Modifier.align(Alignment.End),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
            ) {
                buttons()
            }
        }
    }
}