package com.aliernfrog.ensimanager.ui.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.aliernfrog.ensimanager.ManagerComposableShape
import com.aliernfrog.ensimanager.R

@Composable
fun ManagerWord(word: String) {
    val context = LocalContext.current
    Row(
        modifier = Modifier.fillMaxWidth()
            .padding(8.dp)
            .clip(ManagerComposableShape)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SelectionContainer(Modifier.fillMaxWidth().weight(1f)) {
            Text(word, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Text(
            text = context.getString(R.string.ensi_word_remove),
            modifier = Modifier.clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(bounded = false),
                onClick = {}
            ),
            color = MaterialTheme.colorScheme.error
        )
    }
}