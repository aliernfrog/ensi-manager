package com.aliernfrog.ensimanager.ui.dialog

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.aliernfrog.ensimanager.R

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun DeleteConfirmationDialog(
    toDelete: String,
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        text = {
            Text(stringResource(R.string.dialog_delete_description).replace("{NAME}", toDelete))
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                shapes = ButtonDefaults.shapes(),
                colors = ButtonDefaults.buttonColors().copy(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError
                )
            ) {
                Text(stringResource(R.string.dialog_delete_confirm))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismissRequest,
                shapes = ButtonDefaults.shapes()
            ) {
                Text(stringResource(R.string.action_cancel))
            }
        }
    )
}