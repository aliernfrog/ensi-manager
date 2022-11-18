package com.aliernfrog.ensimanager.ui.screen

import android.annotation.SuppressLint
import androidx.compose.animation.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListLayoutInfo
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.aliernfrog.ensimanager.EnsiFetchingState
import com.aliernfrog.ensimanager.EnsiScreenType
import com.aliernfrog.ensimanager.R
import com.aliernfrog.ensimanager.state.EnsiState
import com.aliernfrog.ensimanager.ui.composable.ManagerFAB
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
        JumpButtons(ensiState, Modifier.align(Alignment.TopEnd), Modifier.align(Alignment.BottomEnd))
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
    val scope = rememberCoroutineScope()
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = ensiState.lazyListState
    ) {
        item {
            ListControls(ensiState, list.size)
        }
        items(list) {
            ManagerWord(it, Modifier.animateItemPlacement()) { scope.launch { ensiState.showWordSheet(it) } }
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

@OptIn(ExperimentalAnimationApi::class)
@SuppressLint("ModifierParameter")
@Composable
private fun JumpButtons(ensiState: EnsiState, topButtonModifier: Modifier, bottomButtonModifier: Modifier) {
    val scope = rememberCoroutineScope()
    val firstVisibleItemIndex = remember { derivedStateOf { ensiState.lazyListState.firstVisibleItemIndex } }
    val layoutInfo = remember { derivedStateOf { ensiState.lazyListState.layoutInfo } }
    AnimatedVisibility(
        visible = firstVisibleItemIndex.value > 0,
        modifier = topButtonModifier,
        enter = scaleIn() + fadeIn(),
        exit = scaleOut() + fadeOut()
    ) {
        ManagerFAB(icon = Icons.Outlined.KeyboardArrowUp) {
            scope.launch { ensiState.lazyListState.animateScrollToItem(0) }
        }
    }
    AnimatedVisibility(
        visible = isAtBottom(layoutInfo.value),
        modifier = bottomButtonModifier,
        enter = scaleIn() + fadeIn(),
        exit = scaleOut() + fadeOut()
    ) {
        ManagerFAB(icon = Icons.Outlined.KeyboardArrowDown) {
            scope.launch { ensiState.lazyListState.animateScrollToItem(ensiState.lazyListState.layoutInfo.totalItemsCount + 1) }
        }
    }
}

private fun isAtBottom(layoutInfo: LazyListLayoutInfo): Boolean {
    val lastItem = layoutInfo.visibleItemsInfo.lastOrNull() ?: return true
    return lastItem.index < layoutInfo.totalItemsCount-1
}