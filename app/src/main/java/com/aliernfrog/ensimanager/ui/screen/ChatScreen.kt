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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.aliernfrog.ensimanager.R
import com.aliernfrog.ensimanager.enum.ChatFilterType
import com.aliernfrog.ensimanager.ui.component.*
import com.aliernfrog.ensimanager.ui.viewmodel.ChatViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.compose.getViewModel

@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    chatViewModel: ChatViewModel = getViewModel()
) {
    val scope = rememberCoroutineScope()
    val refreshing = chatViewModel.isFetching
    val pullRefreshState = rememberPullRefreshState(refreshing, {
        scope.launch { chatViewModel.fetchCurrentList() }
    })

    AppScaffold(
        title = stringResource(R.string.chat),
        topAppBarState = chatViewModel.topAppBarState
    ) {
        Box(Modifier.fillMaxWidth().pullRefresh(pullRefreshState), contentAlignment = Alignment.TopCenter) {
            WordsList(chatViewModel)
            FloatingButtons(
                chatViewModel = chatViewModel,
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
        chatViewModel.fetchCurrentList()
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun WordsList(
    chatViewModel: ChatViewModel = getViewModel()
) {
    val list = chatViewModel.currentList
    val scope = rememberCoroutineScope()
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = chatViewModel.lazyListState
    ) {
        item {
            ListControls(chatViewModel, list.size)
        }
        items(list) {
            Word(
                word = it,
                modifier = Modifier.animateItemPlacement()
            ) { scope.launch {
                chatViewModel.showWordSheet(it)
            } }
        }
        item {
            Spacer(Modifier.height(70.dp))
        }
    }
}

@Composable
private fun ListControls(
    chatViewModel: ChatViewModel = getViewModel(),
    wordsShown: Int
) {
    val scope = rememberCoroutineScope()
    SegmentedButtons(
        options = ChatFilterType.values().map { stringResource(it.titleId) },
        initialIndex = chatViewModel.type.ordinal,
    ) {
        chatViewModel.type = ChatFilterType.values()[it]
        scope.launch { chatViewModel.fetchCurrentList() }
    }

    TextField(
        value = chatViewModel.filter,
        onValueChange = { chatViewModel.filter = it },
        placeholder = { Text(stringResource(R.string.chat_search)) },
        leadingIcon = rememberVectorPainter(Icons.Rounded.Search),
        trailingIcon = {
            AnimatedVisibility(
                visible = chatViewModel.filter.isNotEmpty(),
                enter = fadeIn() + expandHorizontally(),
                exit = fadeOut() + shrinkHorizontally()
            ) {
                IconButton(onClick = { chatViewModel.filter = "" }) {
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
        text = stringResource(
            chatViewModel.type.countTextId
        ).replace("%", wordsShown.toString()),
        modifier = Modifier.padding(horizontal = 8.dp)
    )
}

@OptIn(ExperimentalMaterialApi::class)
@SuppressLint("ModifierParameter")
@Composable
private fun FloatingButtons(
    chatViewModel: ChatViewModel = getViewModel(),
    scrollTopButtonModifier: Modifier,
    bottomButtonsColumnModifier: Modifier,
    scrollBottomButtonModifier: Modifier,
    addWordButtonModifier: Modifier
) {
    val scope = rememberCoroutineScope()
    val firstVisibleItemIndex by remember {
        derivedStateOf { chatViewModel.lazyListState.firstVisibleItemIndex }
    }
    val layoutInfo by remember {
        derivedStateOf { chatViewModel.lazyListState.layoutInfo }
    }

    AnimatedVisibility(
        visible = firstVisibleItemIndex > 0,
        modifier = scrollTopButtonModifier,
        enter = scaleIn() + fadeIn(),
        exit = scaleOut() + fadeOut()
    ) {
        FloatingActionButton(
            icon = Icons.Outlined.KeyboardArrowUp
        ) { scope.launch {
            chatViewModel.lazyListState.animateScrollToItem(0)
        } }
    }

    Column(bottomButtonsColumnModifier) {
        AnimatedVisibility(
            visible = isAtBottom(layoutInfo),
            modifier = scrollBottomButtonModifier,
            enter = scaleIn() + fadeIn(),
            exit = scaleOut() + fadeOut()
        ) {
            FloatingActionButton(
                icon = Icons.Outlined.KeyboardArrowDown
            ) { scope.launch {
                chatViewModel.lazyListState.animateScrollToItem(chatViewModel.lazyListState.layoutInfo.totalItemsCount + 1)
            } }
        }

        FloatingActionButton(
            icon = Icons.Outlined.Add,
            modifier = addWordButtonModifier,
            containerColor = MaterialTheme.colorScheme.primary
        ) { scope.launch {
            chatViewModel.addWordSheetState.show()
        } }
    }
}

private fun isAtBottom(layoutInfo: LazyListLayoutInfo): Boolean {
    val lastItem = layoutInfo.visibleItemsInfo.lastOrNull() ?: return true
    return lastItem.index < layoutInfo.totalItemsCount-1
}