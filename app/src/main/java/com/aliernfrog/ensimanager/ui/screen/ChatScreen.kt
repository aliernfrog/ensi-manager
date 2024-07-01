package com.aliernfrog.ensimanager.ui.screen

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListLayoutInfo
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.aliernfrog.ensimanager.R
import com.aliernfrog.ensimanager.ui.component.AppScaffold
import com.aliernfrog.ensimanager.ui.component.AppTopBar
import com.aliernfrog.ensimanager.ui.component.FloatingActionButton
import com.aliernfrog.ensimanager.ui.component.SegmentedButtons
import com.aliernfrog.ensimanager.ui.component.SettingsButton
import com.aliernfrog.ensimanager.ui.component.TextField
import com.aliernfrog.ensimanager.ui.component.Word
import com.aliernfrog.ensimanager.ui.sheet.AddWordSheet
import com.aliernfrog.ensimanager.ui.sheet.WordSheet
import com.aliernfrog.ensimanager.ui.viewmodel.ChatViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    chatViewModel: ChatViewModel = koinViewModel(),
    onNavigateSettingsRequest: () -> Unit
) {
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        chatViewModel.fetchCategories()
    }

    AppScaffold(
        topBar = {
            AppTopBar(
                title = stringResource(R.string.chat),
                scrollBehavior = it,
                actions = {
                    SettingsButton(
                        onClick = onNavigateSettingsRequest
                    )
                }
            )
        },
        topAppBarState = chatViewModel.topAppBarState
    ) {
        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.TopCenter) {
            PullToRefreshBox(
                isRefreshing = chatViewModel.isFetching,
                onRefresh = { scope.launch {
                    chatViewModel.fetchCategories()
                } }
            ) {
                WordsList()
            }
            FloatingButtons(
                scrollTopButtonModifier = Modifier.align(Alignment.TopEnd),
                bottomButtonsColumnModifier = Modifier.align(Alignment.BottomEnd),
                scrollBottomButtonModifier = Modifier.align(Alignment.TopEnd),
                addWordButtonModifier = Modifier.align(Alignment.BottomEnd)
            )
        }
    }

    AddWordSheet()
    WordSheet()
}

@Composable
private fun WordsList(
    chatViewModel: ChatViewModel = koinViewModel()
) {
    val list = chatViewModel.currentCategoryList
    val scope = rememberCoroutineScope()
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = chatViewModel.lazyListState
    ) {
        item {
            ListControls(stringsShown = list.size)
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
    chatViewModel: ChatViewModel = koinViewModel(),
    stringsShown: Int
) {
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
        options = chatViewModel.categories.map { it.title },
        selectedIndex = chatViewModel.currentCategoryIndex,
        modifier = Modifier.fillMaxWidth().padding(8.dp)
    ) {
        chatViewModel.currentCategoryIndex = it
    }

    Text(
        text = stringResource(R.string.chat_shownStrings).replace("%", stringsShown.toString()),
        modifier = Modifier.padding(horizontal = 8.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("ModifierParameter")
@Composable
private fun FloatingButtons(
    chatViewModel: ChatViewModel = koinViewModel(),
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