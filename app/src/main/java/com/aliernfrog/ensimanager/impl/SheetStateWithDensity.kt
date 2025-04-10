package com.aliernfrog.ensimanager.impl

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
fun createSheetStateWithDensity(
    skipPartiallyExpanded: Boolean = true,
    density: Density
): SheetState {
    // ref: [BottomSheetDefaults.PositionalThreshold]
    val positionalThresholdPx = { with(density) { 56.dp.toPx() } }
    val velocityThresholdPx = { with(density) { 125.dp.toPx() } }
    return SheetState(
        skipPartiallyExpanded = skipPartiallyExpanded,
        positionalThreshold = positionalThresholdPx,
        velocityThreshold = velocityThresholdPx
    )
}