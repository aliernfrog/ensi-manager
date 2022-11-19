package com.aliernfrog.ensimanager.ui.sheet

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.aliernfrog.ensimanager.ChatScreenType
import com.aliernfrog.ensimanager.R
import com.aliernfrog.ensimanager.state.ChatState
import com.aliernfrog.ensimanager.ui.composable.ManagerModalBottomSheet
import com.aliernfrog.ensimanager.ui.composable.ManagerTextField
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AddWordSheet(chatState: ChatState, state: ModalBottomSheetState) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val action = context.getString(when (chatState.type.value) {
        ChatScreenType.VERBS -> R.string.chat_verbs_add
        else -> R.string.chat_words_add
    })
    ManagerModalBottomSheet(
        title = action,
        sheetState = state
    ) {
        ManagerTextField(
            value = chatState.addWordInput.value,
            onValueChange = { chatState.addWordInput.value = it }
        )
        Button(
            onClick = { scope.launch { chatState.addWordFromInput(context); state.hide() } },
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text(action)
        }
    }
}