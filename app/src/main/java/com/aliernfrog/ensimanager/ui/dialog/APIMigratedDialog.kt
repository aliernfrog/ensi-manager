package com.aliernfrog.ensimanager.ui.dialog

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoveUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.aliernfrog.ensimanager.R
import com.aliernfrog.ensimanager.ui.viewmodel.APIViewModel
import org.koin.androidx.compose.getViewModel

@Composable
fun APIMigratedDialog(
    apiViewModel: APIViewModel = getViewModel()
) {
    val onDismissRequest = {
        apiViewModel.migratedTo = null
    }

    apiViewModel.migratedTo?.let { migratedTo ->
        AlertDialog(
            onDismissRequest = onDismissRequest,
            confirmButton = {
                TextButton(
                    onClick = onDismissRequest
                ) {
                    Text(stringResource(R.string.action_dismiss))
                }
            },
            title = {
                Text(stringResource(R.string.setup_migrated))
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.MoveUp,
                    contentDescription = null
                )
            },
            text = {
                Text(
                    text = stringResource(R.string.setup_migrated_description)
                        .replace("{URL}", migratedTo),
                    modifier = Modifier.verticalScroll(rememberScrollState())
                )
            }
        )
    }
}