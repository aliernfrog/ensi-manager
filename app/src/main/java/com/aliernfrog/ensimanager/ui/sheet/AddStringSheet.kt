package com.aliernfrog.ensimanager.ui.sheet

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.aliernfrog.ensimanager.R
import com.aliernfrog.ensimanager.data.api.APIStringsCategory
import io.github.aliernfrog.shared.ui.component.AppModalBottomSheet
import io.github.aliernfrog.shared.ui.component.ButtonIcon

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AddStringSheet(
    state: SheetState,
    currentCategory: APIStringsCategory?,
    onAddStringRequest: (String) -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }
    var stringInput by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(state.isVisible) {
        if (state.isVisible) try {
            focusRequester.requestFocus()
            keyboardController?.show()
        } catch (_: Exception) {}
    }

    AppModalBottomSheet(
        title = stringResource(R.string.strings_add)
            .replace("{CATEGORY}", currentCategory?.title?.lowercase() ?: ""),
        sheetState = state
    ) {
        OutlinedTextField(
            value = stringInput,
            onValueChange = { stringInput = it },
            placeholder = { Text(stringResource(R.string.strings_add_placeholder)) },
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
            Crossfade(stringInput.isNotBlank()) { enabled ->
                OutlinedButton(
                    onClick = { stringInput = "" },
                    shapes = ButtonDefaults.shapes(),
                    enabled = enabled
                ) {
                    ButtonIcon(rememberVectorPainter(Icons.Default.Clear))
                    Text(stringResource(R.string.action_clear))
                }
            }

            Button(
                onClick = {
                    onAddStringRequest(stringInput)
                },
                shapes = ButtonDefaults.shapes()
            ) {
                ButtonIcon(rememberVectorPainter(Icons.Rounded.Done))
                Text(stringResource(R.string.strings_add_confirm))
            }
        }
    }
}