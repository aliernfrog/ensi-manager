package com.aliernfrog.ensimanager.util.extension

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.PriorityHigh
import com.aliernfrog.ensimanager.data.HTTPResponse
import com.aliernfrog.toptoast.enum.TopToastColor
import com.aliernfrog.toptoast.state.TopToastState

fun TopToastState.showErrorToast(text: Any) {
    showToast(
        text = text,
        icon = Icons.Rounded.PriorityHigh,
        iconTintColor = TopToastColor.ERROR
    )
}

fun TopToastState.toastSummary(response: HTTPResponse?) {
    if (response.isSuccessful) showToast(response.summary, icon = Icons.Rounded.Check)
    else showErrorToast(response.summary)
}