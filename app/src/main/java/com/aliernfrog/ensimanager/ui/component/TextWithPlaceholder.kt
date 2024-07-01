package com.aliernfrog.ensimanager.ui.component

import android.util.Range
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import kotlin.random.Random
import kotlin.random.nextInt

@Composable
fun TextWithPlaceholder(
    text: String?,
    modifier: Modifier = Modifier,
    style: TextStyle = LocalTextStyle.current,
    placeholderCharRange: Range<Int> = Range(10, 20),
    placeholderColor: Color = MaterialTheme.colorScheme.onSurface,
) {
    val placeholder = remember {
        List(Random.nextInt(placeholderCharRange.lower..placeholderCharRange.upper)) { " " }.joinToString("")
    }

    Box(
        modifier = modifier.let {
            if (!text.isNullOrEmpty()) it
            else it
                .clip(RoundedCornerShape(10.dp))
                .background(placeholderColor)
        }
    ) {
        Text(
            text = text?.ifEmpty { placeholder } ?: placeholder,
            style = style
        )
    }
}