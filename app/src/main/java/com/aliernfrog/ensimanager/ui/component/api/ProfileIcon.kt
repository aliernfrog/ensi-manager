package com.aliernfrog.ensimanager.ui.component.api

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePainter

@Composable
fun ProfileIcon(
    profileName: String,
    model: Any?,
    containerColor: Color,
    modifier: Modifier = Modifier,
    size: Dp = 40.dp
) {
    var showFallback by rememberSaveable { mutableStateOf(true) }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(size)
            .background(containerColor)
    ) {
        AsyncImage(
            model = model,
            contentDescription = profileName,
            modifier = Modifier.fillMaxSize(),
            onState = { state ->
                showFallback = state !is AsyncImagePainter.State.Success
            }
        )

        if (showFallback) Text(
            text = profileName.firstOrNull()?.uppercase() ?: "-",
            color = contentColorFor(containerColor),
            fontSize = with(LocalDensity.current) {
                (size.toPx() * 0.5f).toSp()
            },
            fontWeight = FontWeight.Bold
        )
    }
}