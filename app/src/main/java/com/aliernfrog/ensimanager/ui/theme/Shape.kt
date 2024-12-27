package com.aliernfrog.ensimanager.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val AppRoundnessSize = 30.dp
val AppComponentShape = RoundedCornerShape(AppRoundnessSize)
val AppSmallComponentShape = RoundedCornerShape(5.dp)
val AppBottomSheetShape = RoundedCornerShape(topStart = AppRoundnessSize, topEnd = AppRoundnessSize)
val AppFABPadding = 90.dp

val Shapes = Shapes(
    small = RoundedCornerShape(4.dp),
    medium = RoundedCornerShape(4.dp),
    large = RoundedCornerShape(0.dp)
)