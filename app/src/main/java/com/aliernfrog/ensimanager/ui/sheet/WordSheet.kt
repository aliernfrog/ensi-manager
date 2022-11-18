package com.aliernfrog.ensimanager.ui.sheet

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.aliernfrog.ensimanager.EnsiScreenType
import com.aliernfrog.ensimanager.state.EnsiState
import com.aliernfrog.ensimanager.ui.composable.ManagerModalBottomSheet
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun WordSheet(ensiState: EnsiState, state: ModalBottomSheetState) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val type = when(ensiState.chosenWordType.value) {
        EnsiScreenType.VERBS -> "verb"
        else -> "word"
    }
    val typeUppercase = type.replaceFirstChar { it.uppercase() }
    ManagerModalBottomSheet(sheetState = state) {
        Text(typeUppercase, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(horizontal = 16.dp))
        SelectionContainer(Modifier.padding(horizontal = 16.dp)) {
            Text(
                text = ensiState.chosenWord.value,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
        Divider(modifier = Modifier.padding(16.dp).alpha(0.7f), thickness = 1.dp, color = MaterialTheme.colorScheme.surfaceVariant)
        Button(
            onClick = { scope.launch { ensiState.deleteChosenWord(context); state.hide() } },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
        ) {
            Text("Remove $type")
        }
    }
}