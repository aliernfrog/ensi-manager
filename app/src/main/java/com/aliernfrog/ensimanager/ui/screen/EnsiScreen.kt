package com.aliernfrog.ensimanager.ui.screen

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.aliernfrog.ensimanager.R
import com.aliernfrog.ensimanager.state.EnsiState
import com.aliernfrog.ensimanager.ui.composable.ManagerSegmentedButtons
import com.aliernfrog.ensimanager.ui.composable.ManagerTextField
import com.aliernfrog.ensimanager.ui.composable.ManagerWord
import kotlinx.coroutines.launch

@Composable
fun EnsiScreen(ensiState: EnsiState) {
    Column(Modifier.fillMaxHeight()) {
        ListControls(ensiState)
        WordsList(ensiState)
    }
    LaunchedEffect(Unit) {
        ensiState.updateApiProperties()
        ensiState.fetchCurrentList()
    }
}

@Composable
private fun ListControls(ensiState: EnsiState) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    ManagerSegmentedButtons(
        options = listOf(context.getString(R.string.ensi_words), context.getString(R.string.ensi_verbs)),
        initialIndex = ensiState.type.value,
    ) {
        ensiState.type.value = it
        scope.launch { ensiState.fetchCurrentList() }
    }
    ManagerTextField(
        value = ensiState.filter.value,
        onValueChange = { ensiState.filter.value = it },
        label = { Text(context.getString(R.string.ensi_filter)) }
    )
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun WordsList(ensiState: EnsiState) {
    AnimatedContent(ensiState.getCurrentList()) {
        Column {
            it.forEach { ManagerWord(it) }
        }
    }
}