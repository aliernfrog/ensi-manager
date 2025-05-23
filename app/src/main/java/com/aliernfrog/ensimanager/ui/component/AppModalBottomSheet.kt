package com.aliernfrog.ensimanager.ui.component

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aliernfrog.ensimanager.imeSupportsSyncAppContent
import com.aliernfrog.ensimanager.ui.theme.AppBottomSheetShape
import com.aliernfrog.ensimanager.ui.viewmodel.InsetsViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppModalBottomSheet(
    title: String? = null,
    sheetState: SheetState,
    sheetScrollState: ScrollState = rememberScrollState(),
    dragHandle: @Composable (() -> Unit)? = { BottomSheetDefaults.DragHandle() },
    sheetContent: @Composable ColumnScope.() -> Unit
) {
    BaseModalBottomSheet(
        sheetState = sheetState,
        dragHandle = dragHandle
    ) { bottomPadding ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(AppBottomSheetShape)
                .verticalScroll(sheetScrollState)
                .padding(bottom = bottomPadding)
        ) {
            title?.let {
                Text(
                    text = it,
                    fontSize = 30.sp,
                    modifier = Modifier.padding(bottom = 8.dp).align(Alignment.CenterHorizontally)
                )
            }
            sheetContent()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BaseModalBottomSheet(
    sheetState: SheetState,
    insetsViewModel: InsetsViewModel = koinViewModel(),
    dragHandle: @Composable (() -> Unit)? = { BottomSheetDefaults.DragHandle() },
    content: @Composable ColumnScope.(bottomPadding: Dp) -> Unit
) {
    val scope = rememberCoroutineScope()
    if (sheetState.currentValue != SheetValue.Hidden || sheetState.targetValue != SheetValue.Hidden) ModalBottomSheet(
        onDismissRequest = { scope.launch {
            sheetState.hide()
        } },
        modifier = Modifier
            .padding(top = insetsViewModel.topPadding),
        sheetState = sheetState,
        dragHandle = dragHandle,
        contentWindowInsets = { WindowInsets(0.dp) }
    ) {
        content(
            insetsViewModel.bottomPadding
            // If IME does not sync app content, keyboard will show over the bottom sheet
            // Add IME padding to workaround this
            + if (imeSupportsSyncAppContent) 0.dp else insetsViewModel.imePadding
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
fun createSheetStateWithDensity(
    skipPartiallyExpanded: Boolean,
    density: Density
): SheetState {
    /**
     * ref: [BottomSheetDefaults.PositionalThreshold]
     */
    val positionalThresholdPx = { with(density) { 56.dp.toPx() } }
    val velocityThresholdPx = { with(density) { 125.dp.toPx() } }
    return SheetState(
        skipPartiallyExpanded = skipPartiallyExpanded,
        positionalThreshold = positionalThresholdPx,
        velocityThreshold = velocityThresholdPx
    )
}