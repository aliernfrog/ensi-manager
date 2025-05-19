package com.aliernfrog.ensimanager.ui.dialog

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.zIndex
import coil3.compose.AsyncImage
import com.aliernfrog.ensimanager.R
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.zoomable
import kotlin.math.absoluteValue

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ImageDialog(
    onDismissRequest: () -> Unit,
    imageModel: Any?
) {
    val density = LocalDensity.current
    val zoomState = rememberZoomState()
    var viewportHeight by remember { mutableStateOf(0.dp) }
    var offsetY by remember { mutableStateOf(0.dp) }
    val animatedOffsetY by animateDpAsState(offsetY)

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .onSizeChanged {
                    with(density) {
                        viewportHeight = it.height.toDp()
                    }
                },
            color = Color.Black.copy(
                alpha = (viewportHeight.value/offsetY.value.absoluteValue/11)
            )
        ) {
            Box {
                AnimatedVisibility(
                    visible = zoomState.scale <= 1f,
                    modifier = Modifier.zIndex(1f)
                ) {
                    FilledTonalIconButton(
                        onClick = onDismissRequest,
                        shapes = IconButtonDefaults.shapes()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = stringResource(R.string.action_close)
                        )
                    }
                }
                AsyncImage(
                    model = imageModel,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .zoomable(zoomState)
                        .offset { IntOffset(x = 0, y = animatedOffsetY.roundToPx()) }
                        .pointerInput(Unit) {
                            if (zoomState.scale <= 1f) detectVerticalDragGestures(
                                onDragEnd =  {
                                    if (zoomState.scale <= 1f && offsetY.value.absoluteValue > viewportHeight.value/7) onDismissRequest()
                                    offsetY = 0.dp
                                }
                            ) { _, dragAmount ->
                                if (zoomState.scale <= 1f) with(density) {
                                    offsetY += dragAmount.toDp()
                                }
                            }
                        }
                )
            }
        }
    }
}