package com.aliernfrog.ensimanager.ui.dialog

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.aliernfrog.ensimanager.R
import com.aliernfrog.ensimanager.data.EnsiAPIDashboardAction

@Composable
fun DestructiveActionDialog(
    action: EnsiAPIDashboardAction,
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        modifier = modifier,
        onDismissRequest = onDismissRequest,
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError
                )
            ) {
                Text(stringResource(R.string.dashboard_destructive_proceed))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismissRequest
            ) {
                Text(stringResource(R.string.action_cancel))
            }
        },
        icon = {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null
            )
        },
        title = {
            Text(action.label)
        },
        text = {
            Text(stringResource(R.string.dashboard_destructive_warning))
        }
    )
}