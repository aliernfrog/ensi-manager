package com.aliernfrog.ensimanager.ui.composable

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import com.aliernfrog.ensimanager.ManagerComposableShape

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManagerTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    singleLine: Boolean = false,
    contentColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    containerColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    rounded: Boolean = true,
    colors: TextFieldColors = TextFieldDefaults.textFieldColors(
        focusedTextColor = contentColor,
        unfocusedTextColor = containerColor,
        containerColor = containerColor,
        cursorColor = contentColor,
        selectionColors = TextSelectionColors(handleColor = contentColor, backgroundColor = contentColor.copy(0.5f)),
        focusedLabelColor = contentColor,
        unfocusedLabelColor = contentColor.copy(0.7f),
        focusedPlaceholderColor = contentColor.copy(0.7f),
        unfocusedPlaceholderColor = contentColor.copy(0.7f)
    )
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth().padding(all = 8.dp).clip(if (rounded) ManagerComposableShape else RectangleShape).animateContentSize(),
        label = label,
        placeholder = placeholder,
        singleLine = singleLine,
        colors = colors
    )
}