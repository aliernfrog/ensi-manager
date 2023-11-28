package com.aliernfrog.ensimanager.ui.screen

import android.annotation.SuppressLint
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListLayoutInfo
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.aliernfrog.ensimanager.R
import com.aliernfrog.ensimanager.enum.ChatFilterType
import com.aliernfrog.ensimanager.ui.component.*
import com.aliernfrog.ensimanager.ui.sheet.AddWordSheet
import com.aliernfrog.ensimanager.ui.sheet.WordSheet
import com.aliernfrog.ensimanager.ui.viewmodel.ChatViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.compose.getViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    chatViewModel: ChatViewModel = getViewModel()
) {
    val pullToRefreshState = rememberPullToRefreshState()

    LaunchedEffect(Unit) {
        chatViewModel.fetchCurrentList()
    }

    if (pullToRefreshState.isRefreshing) LaunchedEffect(Unit) {
        chatViewModel.fetchCurrentList()
    }

    LaunchedEffect(chatViewModel.isFetching) {
        if (chatViewModel.isFetching) pullToRefreshState.startRefresh()
        else pullToRefreshState.endRefresh()
    }

    AppScaffold(
        title = stringResource(R.string.chat),
        topAppBarState = chatViewModel.topAppBarState
    ) {
        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.TopCenter) {
            WordsList(
                nestedScrollConnection = pullToRefreshState.nestedScrollConnection
            )
            FloatingButtons(
                scrollTopButtonModifier = Modifier.align(Alignment.TopEnd),
                bottomButtonsColumnModifier = Modifier.align(Alignment.BottomEnd),
                scrollBottomButtonModifier = Modifier.align(Alignment.TopEnd),
                addWordButtonModifier = Modifier.align(Alignment.BottomEnd)
            )
            PullToRefreshContainer(
                state = pullToRefreshState
            )
        }
    }

    AddWordSheet()
    WordSheet()
}

@Composable
private fun WordsList(
    chatViewModel: ChatViewModel = getViewModel(),
    nestedScrollConnection: NestedScrollConnection
) {
    val list = chatViewModel.currentList
    val scope = rememberCoroutineScope()
    LazyColumn(
        modifier = Modifier.fillMaxSize().nestedScroll(nestedScrollConnection),
        state = chatViewModel.lazyListState
    ) {
        item {
            ListControls(chatViewModel, list.size)
        }
        items(list) {
            Word(
                word = it
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

    SegmentedButtons(
        options = ChatFilterType.values().map { stringResource(it.titleId) },
        selectedIndex = chatViewModel.type.ordinal,
        modifier = Modifier.fillMaxWidth().padding(8.dp)
    ) {
        chatViewModel.type = ChatFilterType.values()[it]
        scope.launch { chatViewModel.fetchCurrentList() }
    }

    Text(
        text = stringResource(
            chatViewModel.type.countTextId
        ).replace("%", wordsShown.toString()),
        modifier = Modifier.padding(horizontal = 8.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
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