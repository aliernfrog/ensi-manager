package com.aliernfrog.ensimanager.ui.sheet

import androidx.compose.animation.*
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.aliernfrog.ensimanager.ui.component.AppModalBottomSheet
import com.aliernfrog.ensimanager.ui.theme.AppComponentShape
import com.aliernfrog.ensimanager.ui.viewmodel.ChatViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.compose.getViewModel

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AddWordSheet(
    chatViewModel: ChatViewModel = getViewModel(),
    state: ModalBottomSheetState = chatViewModel.addWordSheetState
) {
    val scope = rememberCoroutineScope()
    val type = chatViewModel.type
    val action = stringResource(type.addWordTitleId)
    AppModalBottomSheet(
        title = action,
        sheetState = state
    ) {
        OutlinedTextField(
            value = chatViewModel.addWordInput,
            onValueChange = { chatViewModel.addWordInput = it },
            placeholder = { Text(stringResource(type.addWordPlaceholderId)) },
            trailingIcon = {
                AnimatedVisibility(
                    visible = chatViewModel.addWordInput.isNotEmpty(),
                    enter = fadeIn() + expandHorizontally(),
                    exit = fadeOut() + shrinkHorizontally()
                ) {
                    IconButton(onClick = { chatViewModel.addWordInput = "" }) {
                        Icon(
                            painter = rememberVectorPainter(Icons.Rounded.Clear),
                            contentDescription = null
                        )
                    }
                }
            },
            shape = AppComponentShape,
            modifier = Modifier.animateContentSize().fillMaxWidth().padding(8.dp)
        )
        Button(
            onClick = { scope.launch {
                chatViewModel.addWordFromInput()
                state.hide()
            } },
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Icon(
                painter = rememberVectorPainter(Icons.Rounded.Done),
                contentDescription = null,
                modifier = Modifier.padding(end = 4.dp)
            )
            Text(action)
        }
    }
}