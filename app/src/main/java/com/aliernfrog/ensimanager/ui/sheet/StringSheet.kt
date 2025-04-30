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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.aliernfrog.ensimanager.R
import com.aliernfrog.ensimanager.ui.component.AppModalBottomSheet
import com.aliernfrog.ensimanager.ui.component.expressive.ExpressiveSection
import com.aliernfrog.ensimanager.ui.viewmodel.StringsViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun StringSheet(
    stringsViewModel: StringsViewModel = koinViewModel(),
    state: SheetState = stringsViewModel.wordSheetState
) {
    val scope = rememberCoroutineScope()

    AppModalBottomSheet(sheetState = state) {
        ExpressiveSection(
            title = stringsViewModel.chosenStringCategory?.replaceFirstChar { it.uppercase() } ?: ""
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
                    Text(stringsViewModel.chosenString)
                }
            }
        }
        Button(
            onClick = { scope.launch {
                stringsViewModel.deleteChosenWord()
                state.hide()
            } },
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