package com.aliernfrog.ensimanager.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.aliernfrog.ensimanager.R
import com.aliernfrog.ensimanager.state.EnsiState
import com.aliernfrog.ensimanager.ui.composable.ManagerSegmentedButtons
import com.aliernfrog.ensimanager.ui.composable.ManagerTextField

@Composable
fun EnsiScreen(ensiState: EnsiState) {
    Column(Modifier.fillMaxHeight()) {
        ListControls(ensiState)
        WordsList(ensiState)
    }
    LaunchedEffect(Unit) {
        ensiState.updateApiProperties()
        ensiState.getWords()
    }
}

@Composable
private fun ListControls(ensiState: EnsiState) {
    val context = LocalContext.current
    ManagerSegmentedButtons(
        options = listOf(context.getString(R.string.ensi_words), context.getString(R.string.ensi_verbs)),
        initialIndex = ensiState.type.value,
    ) {
        ensiState.type.value = it
    }
    ManagerTextField(
        value = ensiState.filter.value,
        onValueChange = { ensiState.filter.value = it },
        label = { Text(context.getString(R.string.ensi_filter)) }
    )
}

@Composable
private fun WordsList(ensiState: EnsiState) {
    ensiState.words.value.forEach { word ->
        Text(word)
    }
}