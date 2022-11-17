package com.aliernfrog.ensimanager.ui.screen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.aliernfrog.ensimanager.EnsiFetchingState
import com.aliernfrog.ensimanager.EnsiScreenType
import com.aliernfrog.ensimanager.R
import com.aliernfrog.ensimanager.state.EnsiState
import com.aliernfrog.ensimanager.ui.composable.ManagerSegmentedButtons
import com.aliernfrog.ensimanager.ui.composable.ManagerTextField
import com.aliernfrog.ensimanager.ui.composable.ManagerWord
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun EnsiScreen(ensiState: EnsiState) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val refreshing = ensiState.fetchingState.value == EnsiFetchingState.FETCHING
    val pullRefreshState = rememberPullRefreshState(refreshing, {
        scope.launch { ensiState.fetchCurrentList(context) }
    })
    Box(Modifier.fillMaxWidth().pullRefresh(pullRefreshState), contentAlignment = Alignment.TopCenter) {
        WordsList(ensiState)
        PullRefreshIndicator(
            refreshing = refreshing,
            state = pullRefreshState,
            backgroundColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    }
    LaunchedEffect(Unit) {
        ensiState.updateApiProperties()
        ensiState.fetchCurrentList(context)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun WordsList(ensiState: EnsiState) {
    val list = ensiState.getCurrentList()
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = ensiState.lazyListState
    ) {
        item {
            ListControls(ensiState, list.size)
        }
        items(list) {
            ManagerWord(it, Modifier.animateItemPlacement())
        }
    }
}

@Composable
private fun ListControls(ensiState: EnsiState, wordsShown: Int) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    ManagerSegmentedButtons(
        options = listOf(context.getString(R.string.ensi_words), context.getString(R.string.ensi_verbs)),
        initialIndex = ensiState.type.value,
    ) {
        ensiState.type.value = it
        scope.launch { ensiState.fetchCurrentList(context) }
    }
    ManagerTextField(
        value = ensiState.filter.value,
        onValueChange = { ensiState.filter.value = it },
        label = { Text(context.getString(R.string.ensi_filter)) }
    )
    Text(
        text = context.getString(when (ensiState.type.value) {
            EnsiScreenType.VERBS -> R.string.ensi_verbs_count
            else -> R.string.ensi_words_count
        }).replace("%", wordsShown.toString()),
        modifier = Modifier.padding(horizontal = 8.dp)
    )
}