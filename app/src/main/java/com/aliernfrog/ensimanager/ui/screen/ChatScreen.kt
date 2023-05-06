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
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.aliernfrog.ensimanager.ChatScreenType
import com.aliernfrog.ensimanager.FetchingState
import com.aliernfrog.ensimanager.R
import com.aliernfrog.ensimanager.state.ChatState
import com.aliernfrog.ensimanager.ui.component.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(chatState: ChatState) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val refreshing = chatState.fetchingState.value == FetchingState.FETCHING
    val pullRefreshState = rememberPullRefreshState(refreshing, {
        scope.launch { chatState.fetchCurrentList(context) }
    })
    AppScaffold(
        title = stringResource(R.string.screen_chat),
        topAppBarState = chatState.topAppBarState
    ) {
        Box(Modifier.fillMaxWidth().pullRefresh(pullRefreshState), contentAlignment = Alignment.TopCenter) {
            WordsList(chatState)
            FloatingButtons(
                chatState = chatState,
                scrollTopButtonModifier = Modifier.align(Alignment.TopEnd),
                bottomButtonsColumnModifier = Modifier.align(Alignment.BottomEnd),
                scrollBottomButtonModifier = Modifier.align(Alignment.TopEnd),
                addWordButtonModifier = Modifier.align(Alignment.BottomEnd)
            )
            PullRefreshIndicator(
                refreshing = refreshing,
                state = pullRefreshState,
                backgroundColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
            )
        }
    }
    LaunchedEffect(Unit) {
        chatState.fetchCurrentList(context)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun WordsList(chatState: ChatState) {
    val list = chatState.getCurrentList()
    val scope = rememberCoroutineScope()
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = chatState.lazyListState
    ) {
        item {
            ListControls(chatState, list.size)
        }
        items(list) {
            Word(it, Modifier.animateItemPlacement()) { scope.launch { chatState.showWordSheet(it) } }
        }
        item {
            Spacer(Modifier.height(70.dp))
        }
    }
}

@Composable
private fun ListControls(chatState: ChatState, wordsShown: Int) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    SegmentedButtons(
        options = listOf(stringResource(R.string.chat_words), stringResource(R.string.chat_verbs)),
        initialIndex = chatState.type.value,
    ) {
        chatState.type.value = it
        scope.launch { chatState.fetchCurrentList(context) }
    }
    TextField(
        value = chatState.filter.value,
        onValueChange = { chatState.filter.value = it },
        placeholder = { Text(stringResource(R.string.chat_search)) },
        leadingIcon = rememberVectorPainter(Icons.Rounded.Search),
        trailingIcon = {
            AnimatedVisibility(
                visible = chatState.filter.value.isNotEmpty(),
                enter = fadeIn() + expandHorizontally(),
                exit = fadeOut() + shrinkHorizontally()
            ) {
                IconButton(onClick = { chatState.filter.value = "" }) {
                    Icon(
                        painter = rememberVectorPainter(Icons.Rounded.Clear),
                        contentDescription = null
                    )
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
        contentColor = MaterialTheme.colorScheme.onSurface
    )
    Text(
        text = stringResource(when (chatState.type.value) {
            ChatScreenType.VERBS -> R.string.chat_verbs_count
            else -> R.string.chat_words_count
        }).replace("%", wordsShown.toString()),
        modifier = Modifier.padding(horizontal = 8.dp)
    )
}

@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterialApi::class)
@SuppressLint("ModifierParameter")
@Composable
private fun FloatingButtons(
    chatState: ChatState,
    scrollTopButtonModifier: Modifier,
    bottomButtonsColumnModifier: Modifier,
    scrollBottomButtonModifier: Modifier,
    addWordButtonModifier: Modifier
) {
    val scope = rememberCoroutineScope()
    val firstVisibleItemIndex = remember { derivedStateOf { chatState.lazyListState.firstVisibleItemIndex } }
    val layoutInfo = remember { derivedStateOf { chatState.lazyListState.layoutInfo } }
    AnimatedVisibility(
        visible = firstVisibleItemIndex.value > 0,
        modifier = scrollTopButtonModifier,
        enter = scaleIn() + fadeIn(),
        exit = scaleOut() + fadeOut()
    ) {
        FloatingActionButton(
            icon = Icons.Outlined.KeyboardArrowUp
        ) {
            scope.launch { chatState.lazyListState.animateScrollToItem(0) }
        }
    }
    Column(bottomButtonsColumnModifier) {
        AnimatedVisibility(
            visible = isAtBottom(layoutInfo.value),
            modifier = scrollBottomButtonModifier,
            enter = scaleIn() + fadeIn(),
            exit = scaleOut() + fadeOut()
        ) {
            FloatingActionButton(
                icon = Icons.Outlined.KeyboardArrowDown
            ) {
                scope.launch { chatState.lazyListState.animateScrollToItem(chatState.lazyListState.layoutInfo.totalItemsCount + 1) }
            }
        }
        FloatingActionButton(
            icon = Icons.Outlined.Add,
            modifier = addWordButtonModifier,
            containerColor = MaterialTheme.colorScheme.primary
        ) {
            scope.launch { chatState.addWordSheetState.show() }
        }
    }
}

private fun isAtBottom(layoutInfo: LazyListLayoutInfo): Boolean {
    val lastItem = layoutInfo.visibleItemsInfo.lastOrNull() ?: return true
    return lastItem.index < layoutInfo.totalItemsCount-1
}