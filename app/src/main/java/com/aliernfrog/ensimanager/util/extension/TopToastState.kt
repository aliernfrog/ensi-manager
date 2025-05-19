package com.aliernfrog.ensimanager.util.extension

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.PriorityHigh
import com.aliernfrog.ensimanager.R
import com.aliernfrog.ensimanager.data.HTTPResponse
import com.aliernfrog.ensimanager.data.isSuccessful
import com.aliernfrog.ensimanager.data.summary
import com.aliernfrog.toptoast.enum.TopToastColor
import com.aliernfrog.toptoast.state.TopToastState

fun TopToastState.showSuccessToast(text: Any, androidToast: Boolean = false) {
    if (androidToast) showAndroidToast(
        text = text,
        icon = Icons.Rounded.Check,
        iconTintColor = TopToastColor.PRIMARY
    ) else showToast(
        text = text,
        icon = Icons.Rounded.Check,
        iconTintColor = TopToastColor.PRIMARY
    )
}

fun TopToastState.showErrorToast(text: Any = R.string.error_generic, androidToast: Boolean = false) {
    if (androidToast) showAndroidToast(
        text = text,
        icon = Icons.Rounded.PriorityHigh,
        iconTintColor = TopToastColor.ERROR
    ) else showToast(
        text = text,
        icon = Icons.Rounded.PriorityHigh,
        iconTintColor = TopToastColor.ERROR
    )
}

fun TopToastState.toastSummary(response: HTTPResponse?) {
    if (response.isSuccessful) showToast(response.summary, icon = Icons.Rounded.Check)
    else showErrorToast(response.summary)
}