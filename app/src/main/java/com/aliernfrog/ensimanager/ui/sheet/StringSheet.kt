package com.aliernfrog.ensimanager.ui.sheet

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.aliernfrog.ensimanager.R
import com.aliernfrog.ensimanager.ui.component.AppModalBottomSheet
import com.aliernfrog.ensimanager.ui.viewmodel.StringsViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StringSheet(
    stringsViewModel: StringsViewModel = koinViewModel(),
    state: SheetState = stringsViewModel.wordSheetState
) {
    val scope = rememberCoroutineScope()

    AppModalBottomSheet(sheetState = state) {
        Text(
            text = stringsViewModel.chosenStringCategory?.replaceFirstChar { it.uppercase() } ?: "",
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        SelectionContainer(Modifier.padding(horizontal = 16.dp)) {
            Text(
                text = stringsViewModel.chosenString
            )
        }
        HorizontalDivider(
            modifier = Modifier.padding(16.dp).alpha(0.7f),
            thickness = 1.dp
        )
        Button(
            onClick = { scope.launch {
                stringsViewModel.deleteChosenWord()
                state.hide()
            } },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
        ) {
            Text(stringResource(R.string.strings_remove))
        }
    }
}