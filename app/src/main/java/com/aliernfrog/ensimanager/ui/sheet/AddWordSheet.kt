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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.aliernfrog.ensimanager.ChatScreenType
import com.aliernfrog.ensimanager.R
import com.aliernfrog.ensimanager.state.ChatState
import com.aliernfrog.ensimanager.ui.component.AppModalBottomSheet
import com.aliernfrog.ensimanager.ui.theme.AppComponentShape
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AddWordSheet(chatState: ChatState, state: ModalBottomSheetState) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val action = stringResource(when (chatState.type) {
        ChatScreenType.VERBS -> R.string.chat_verbs_add
        else -> R.string.chat_words_add
    })
    val placeholder = stringResource(when (chatState.type) {
        ChatScreenType.VERBS -> R.string.chat_verbs_add_placeholder
        else -> R.string.chat_words_add_placeholder
    })
    AppModalBottomSheet(
        title = action,
        sheetState = state
    ) {
        OutlinedTextField(
            value = chatState.addWordInput,
            onValueChange = { chatState.addWordInput = it },
            placeholder = { Text(placeholder) },
            trailingIcon = {
                AnimatedVisibility(
                    visible = chatState.addWordInput.isNotEmpty(),
                    enter = fadeIn() + expandHorizontally(),
                    exit = fadeOut() + shrinkHorizontally()
                ) {
                    IconButton(onClick = { chatState.addWordInput = "" }) {
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
            onClick = { scope.launch { chatState.addWordFromInput(context); state.hide() } },
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