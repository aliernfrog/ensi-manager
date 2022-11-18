package com.aliernfrog.ensimanager.ui.sheet

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aliernfrog.ensimanager.state.EnsiState
import com.aliernfrog.ensimanager.ui.composable.ManagerModalBottomSheet

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun WordSheet(ensiState: EnsiState, state: ModalBottomSheetState) {
    ManagerModalBottomSheet(sheetState = state) {
        SelectionContainer(Modifier.padding(8.dp)) {
            Text(
                text = ensiState.lastChosenWord.value,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}