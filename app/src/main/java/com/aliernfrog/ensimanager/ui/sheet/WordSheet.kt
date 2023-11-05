package com.aliernfrog.ensimanager.ui.sheet

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import com.aliernfrog.ensimanager.ui.component.AppModalBottomSheet
import com.aliernfrog.ensimanager.ui.viewmodel.ChatViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.compose.getViewModel

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun WordSheet(
    chatViewModel: ChatViewModel = getViewModel(),
    state: ModalBottomSheetState = chatViewModel.wordSheetState
) {
    val scope = rememberCoroutineScope()
    val type = chatViewModel.chosenWordType.type
    val typeUppercase = type.replaceFirstChar { it.uppercase() }

    AppModalBottomSheet(sheetState = state) {
        Text(typeUppercase, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(horizontal = 16.dp))
        SelectionContainer(Modifier.padding(horizontal = 16.dp)) {
            Text(
                text = chatViewModel.chosenWord,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
        HorizontalDivider(
            modifier = Modifier.padding(16.dp).alpha(0.7f),
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.surfaceVariant
        )
        Button(
            onClick = { scope.launch {
                chatViewModel.deleteChosenWord()
                state.hide()
            } },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
        ) {
            Text("Remove $type")
        }
    }
}