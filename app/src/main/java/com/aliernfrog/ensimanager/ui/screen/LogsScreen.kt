package com.aliernfrog.ensimanager.ui.screen

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListLayoutInfo
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aliernfrog.ensimanager.R
import com.aliernfrog.ensimanager.data.api.APILog
import com.aliernfrog.ensimanager.data.api.getTimeStr
import com.aliernfrog.ensimanager.enum.APILogType
import com.aliernfrog.ensimanager.ui.component.SearchField
import com.aliernfrog.ensimanager.ui.component.SettingsButton
import com.aliernfrog.ensimanager.ui.viewmodel.LogsViewModel
import io.github.aliernfrog.shared.ui.component.AppScaffold
import io.github.aliernfrog.shared.ui.component.AppTopBar
import io.github.aliernfrog.shared.ui.component.FloatingActionButton
import io.github.aliernfrog.shared.ui.theme.AppFABPadding
import io.github.aliernfrog.shared.util.extension.horizontalFadingEdge
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogsScreen(
    vm: LogsViewModel = koinViewModel(),
    onNavigateSettingsRequest: () -> Unit,
) {
    val scope = rememberCoroutineScope()

    LaunchedEffect(vm.logs) {
        if (vm.logs.isEmpty()) vm.fetchLogs()
    }

    AppScaffold(
        topBar = {
            AppTopBar(
                title = stringResource(R.string.logs),
                scrollBehavior = it,
                actions = {
                    SettingsButton(
                        onNavigateSettingsRequest = onNavigateSettingsRequest
                    )
                }
            )
        },
        topAppBarState = vm.topAppBarState
    ) {
        Box {
            PullToRefreshBox(
                isRefreshing = vm.isFetching,
                onRefresh = { scope.launch {
                    vm.fetchLogs()
                } }
            ) {
                LogsList(vm)
            }
            FloatingButtons(
                lazyListState = vm.lazyListState,
                scrollTopButtonModifier = Modifier.align(Alignment.TopEnd),
                scrollBottomButtonModifier = Modifier.align(Alignment.BottomEnd)
            )
        }
    }
}

@Composable
private fun LogsList(
    vm: LogsViewModel
) {
    val filtersScrollState = rememberScrollState()
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = vm.lazyListState
    ) {
        item {
            SearchField(
                query = vm.filter,
                onQueryChange = { vm.filter = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = (-12).dp)
                    .padding(
                        start = 8.dp,
                        end = 8.dp
                    )
            )
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalFadingEdge(
                        scrollState = filtersScrollState,
                        edgeColor = MaterialTheme.colorScheme.surface,
                        isRTL = LocalLayoutDirection.current == LayoutDirection.Rtl
                    )
                    .horizontalScroll(filtersScrollState)
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                APILogType.entries.forEach {
                    val selected = vm.shownLogTypes.contains(it)
                    FilterChip(
                        selected = selected,
                        label = { Text(stringResource(it.nameId)) },
                        leadingIcon = if (selected) { {
                            Icon(
                                imageVector = Icons.Default.Done,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                        } } else { null },
                        onClick = {
                            if (selected) vm.shownLogTypes.remove(it)
                            else vm.shownLogTypes.add(it)
                        }
                    )
                }
                VerticalDivider(
                    modifier = Modifier
                        .height(32.dp)
                        .padding(
                            horizontal = 4.dp,
                            vertical = 4.dp
                        ),
                    thickness = 1.dp
                )
                InputChip(
                    selected = vm.logsReversed,
                    onClick = { vm.logsReversed = !vm.logsReversed },
                    label = { Text(stringResource(R.string.logs_reversed)) },
                    leadingIcon = if (vm.logsReversed) { {
                        Icon(
                            imageVector = Icons.Default.Done,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    } } else { null }
                )
            }
        }
        itemsIndexed(vm.shownLogs) { index, item ->
            LogItem(
                log = item,
                isLastItem = index == vm.shownLogs.size-1
            )
        }
        item {
            Spacer(Modifier.navigationBarsPadding().height(AppFABPadding))
        }
    }
}

@Composable
private fun LogItem(
    log: APILog,
    isLastItem: Boolean
) {
    val context = LocalContext.current
    val density = LocalDensity.current
    val color = log.type.getColor()
    val symbolColor = MaterialTheme.colorScheme.contentColorFor(color)
    var height by remember { mutableStateOf(0.dp) }
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                //.height(IntrinsicSize.Max)
        ) {
            Column(
                modifier = Modifier
                    //.fillMaxHeight()
                    .height(height)
                    .width(28.dp)
                    .background(color),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = log.type.symbol.toString(),
                    fontSize = 25.sp,
                    color = symbolColor
                )
            }
            Column(
                modifier = Modifier
                    .onSizeChanged { density.run {
                        height = it.height.toDp()
                    } }
                    .padding(horizontal = 4.dp)
            ) {
                Text(
                    text = log.getTimeStr(context),
                    fontSize = 12.sp,
                    modifier = Modifier.alpha(0.5f)
                )
                SelectionContainer {
                    Text(
                        text = log.str,
                        fontSize = 13.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
        if (!isLastItem) HorizontalDivider(
            thickness = 1.dp,
            modifier = Modifier.fillMaxWidth().padding(bottom = 0.dp)
        )
    }
}

@SuppressLint("ModifierParameter")
@Composable
private fun FloatingButtons(
    lazyListState: LazyListState,
    scrollTopButtonModifier: Modifier,
    scrollBottomButtonModifier: Modifier
) {
    val scope = rememberCoroutineScope()
    val firstVisibleItemIndex by remember {
        derivedStateOf { lazyListState.firstVisibleItemIndex }
    }
    val layoutInfo by remember {
        derivedStateOf { lazyListState.layoutInfo }
    }

    AnimatedVisibility(
        visible = firstVisibleItemIndex > 0,
        modifier = scrollTopButtonModifier.padding(16.dp),
        enter = scaleIn() + fadeIn(),
        exit = scaleOut() + fadeOut()
    ) {
        FloatingActionButton(
            icon = Icons.Outlined.KeyboardArrowUp
        ) { scope.launch {
            lazyListState.animateScrollToItem(0)
        } }
    }

    AnimatedVisibility(
        visible = isAtBottom(layoutInfo),
        modifier = scrollBottomButtonModifier.padding(16.dp).systemBarsPadding(),
        enter = scaleIn() + fadeIn(),
        exit = scaleOut() + fadeOut()
    ) {
        FloatingActionButton(
            icon = Icons.Outlined.KeyboardArrowDown
        ) { scope.launch {
            lazyListState.animateScrollToItem(
                lazyListState.layoutInfo.totalItemsCount + 1
            )
        } }
    }
}

private fun isAtBottom(layoutInfo: LazyListLayoutInfo): Boolean {
    val lastItem = layoutInfo.visibleItemsInfo.lastOrNull() ?: return true
    return lastItem.index < layoutInfo.totalItemsCount-1
}