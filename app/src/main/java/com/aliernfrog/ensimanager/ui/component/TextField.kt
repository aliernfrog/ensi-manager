package com.aliernfrog.ensimanager.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.aliernfrog.ensimanager.ui.theme.AppComponentShape
import com.aliernfrog.ensimanager.R

@Composable
fun TextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: Painter? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    supportingText: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    doneIconShown: Boolean = false,
    doneIcon: Painter = rememberVectorPainter(Icons.Rounded.Done),
    onDone: (() -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    singleLine: Boolean = false,
    containerColor: Color = MaterialTheme.colorScheme.secondary,
    contentColor: Color = contentColorFor(containerColor),
    rounded: Boolean = true,
    colors: TextFieldColors = TextFieldDefaults.colors(
        focusedTextColor = contentColor,
        unfocusedTextColor = contentColor,
        focusedContainerColor = containerColor,
        unfocusedContainerColor = containerColor,
        disabledContainerColor = containerColor,
        cursorColor = contentColor,
        selectionColors = TextSelectionColors(handleColor = contentColor, backgroundColor = contentColor.copy(0.5f)),
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
        disabledIndicatorColor = Color.Transparent,
        errorIndicatorColor = Color.Transparent,
        focusedLabelColor = contentColor,
        unfocusedLabelColor = contentColor.copy(0.7f),
        focusedPlaceholderColor = contentColor.copy(0.7f),
        unfocusedPlaceholderColor = contentColor.copy(0.7f),
    )
) {
    Box(
        modifier = modifier
            .padding(all = 8.dp)
            .clip(if (rounded) AppComponentShape else RectangleShape)
            .background(containerColor)
            .padding(bottom = if (supportingText != null) 4.dp else 0.dp)
            .animateContentSize(),
        contentAlignment = Alignment.CenterEnd
    ) {
        androidx.compose.material3.TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            label = label,
            placeholder = placeholder,
            leadingIcon = leadingIcon?.let { { Icon(it, null, tint = contentColor) } },
            trailingIcon = trailingIcon,
            supportingText = supportingText,
            isError = isError,
            keyboardOptions = keyboardOptions,
            singleLine = singleLine,
            colors = colors
        )
        AnimatedVisibility(
            visible = doneIconShown,
            modifier = Modifier.padding(end = 16.dp),
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            IconButton(
                onClick = { onDone?.invoke() },
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = contentColor,
                    contentColor = containerColor
                )
            ) {
                Icon(
                    painter = doneIcon,
                    contentDescription = stringResource(R.string.action_done)
                )
            }
        }
    }
}