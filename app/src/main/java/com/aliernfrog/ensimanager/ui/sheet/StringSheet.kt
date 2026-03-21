package com.aliernfrog.ensimanager.ui.sheet

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.aliernfrog.ensimanager.R
import io.github.aliernfrog.shared.ui.component.AppModalBottomSheet
import io.github.aliernfrog.shared.ui.component.expressive.ExpressiveSection

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun StringSheet(
    state: SheetState,
    string: String,
    stringCategory: String?,
    onDeleteStringRequest: () -> Unit
) {
    AppModalBottomSheet(sheetState = state) {
        ExpressiveSection(
            title = stringCategory?.replaceFirstChar { it.uppercase() } ?: ""
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 12.dp, end = 12.dp, bottom = 12.dp)
            ) {
                SelectionContainer(Modifier.padding(
                    vertical = 8.dp,
                    horizontal = 12.dp
                )) {
                    Text(string)
                }
            }
        }
        Button(
            onClick = onDeleteStringRequest,
            shapes = ButtonDefaults.shapes(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
        ) {
            Text(stringResource(R.string.strings_remove))
        }
    }
}