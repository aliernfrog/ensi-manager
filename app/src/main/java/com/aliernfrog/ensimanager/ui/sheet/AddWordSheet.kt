package com.aliernfrog.ensimanager.ui.sheet

import androidx.compose.animation.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.aliernfrog.ensimanager.R
import com.aliernfrog.ensimanager.ui.component.AppModalBottomSheet
import com.aliernfrog.ensimanager.ui.component.ButtonIcon
import com.aliernfrog.ensimanager.ui.viewmodel.ChatViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.compose.getViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddWordSheet(
    chatViewModel: ChatViewModel = getViewModel(),
    state: SheetState = chatViewModel.addWordSheetState
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val scope = rememberCoroutineScope()
    val focusRequester = remember { FocusRequester() }
    val type = chatViewModel.type
    val action = stringResource(type.addWordTitleId)

    LaunchedEffect(state.isVisible) {
        if (state.isVisible) try {
            focusRequester.requestFocus()
            keyboardController?.show()
        } catch (_: Exception) {}
    }

    AppModalBottomSheet(
        title = action,
        sheetState = state
    ) {
        OutlinedTextField(
            value = chatViewModel.addWordInput,
            onValueChange = { chatViewModel.addWordInput = it },
            placeholder = { Text(stringResource(type.addWordPlaceholderId)) },
            modifier = Modifier
                .animateContentSize()
                .fillMaxWidth()
                .padding(8.dp)
                .focusRequester(focusRequester)
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
            modifier = Modifier.fillMaxWidth().padding(
                vertical = 4.dp,
                horizontal = 8.dp
            )
        ) {
            Crossfade(chatViewModel.addWordInput.isNotBlank()) { enabled ->
                OutlinedButton(
                    onClick = { chatViewModel.addWordInput = "" },
                    enabled = enabled
                ) {
                    ButtonIcon(rememberVectorPainter(Icons.Default.Clear))
                    Text(stringResource(R.string.action_clear))
                }
            }

            Button(
                onClick = { scope.launch {
                    chatViewModel.addWordFromInput()
                    state.hide()
                } }
            ) {
                ButtonIcon(rememberVectorPainter(Icons.Rounded.Done))
                Text(action)
            }
        }
    }
}