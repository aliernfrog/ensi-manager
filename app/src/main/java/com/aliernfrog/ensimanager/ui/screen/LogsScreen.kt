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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListLayoutInfo
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
import com.aliernfrog.ensimanager.enum.APILogType
import com.aliernfrog.ensimanager.ui.component.AppScaffold
import com.aliernfrog.ensimanager.ui.component.AppTopBar
import com.aliernfrog.ensimanager.ui.component.FloatingActionButton
import com.aliernfrog.ensimanager.ui.component.SettingsButton
import com.aliernfrog.ensimanager.ui.theme.AppFABPadding
import com.aliernfrog.ensimanager.ui.viewmodel.LogsViewModel
import com.aliernfrog.ensimanager.util.extension.getTimeStr
import com.aliernfrog.ensimanager.util.extension.horizontalFadingEdge
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogsScreen(
    logsViewModel: LogsViewModel = koinViewModel(),
    onNavigateSettingsRequest: () -> Unit,
) {
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        if (logsViewModel.logs.isEmpty()) logsViewModel.fetchLogs()
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
        topAppBarState = logsViewModel.topAppBarState
    ) {
        Box {
            PullToRefreshBox(
                isRefreshing = logsViewModel.isFetching,
                onRefresh = { scope.launch {
                    logsViewModel.fetchLogs()
                } }
            ) {
                LogsList()
            }
            FloatingButtons(
                scrollTopButtonModifier = Modifier.align(Alignment.TopEnd),
                scrollBottomButtonModifier = Modifier.align(Alignment.BottomEnd)
            )
        }
    }
}

@Composable
private fun LogsList(
    logsViewModel: LogsViewModel = koinViewModel()
) {
    val filtersScrollState = rememberScrollState()
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = logsViewModel.lazyListState
    ) {
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
                    val selected = logsViewModel.shownLogTypes.contains(it)
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
                            if (selected) logsViewModel.shownLogTypes.remove(it)
                            else logsViewModel.shownLogTypes.add(it)
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
                    selected = logsViewModel.logsReversed,
                    onClick = { logsViewModel.logsReversed = !logsViewModel.logsReversed },
                    label = { Text(stringResource(R.string.logs_reversed)) },
                    leadingIcon = if (logsViewModel.logsReversed) { {
                        Icon(
                            imageVector = Icons.Default.Done,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    } } else { null }
                )
            }
        }
        itemsIndexed(logsViewModel.shownLogs) { index, item ->
            LogItem(
                log = item,
                isLastItem = index == logsViewModel.shownLogs.size-1
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
    logsViewModel: LogsViewModel = koinViewModel(),
    scrollTopButtonModifier: Modifier,
    scrollBottomButtonModifier: Modifier
) {
    val scope = rememberCoroutineScope()
    val firstVisibleItemIndex by remember {
        derivedStateOf { logsViewModel.lazyListState.firstVisibleItemIndex }
    }
    val layoutInfo by remember {
        derivedStateOf { logsViewModel.lazyListState.layoutInfo }
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
            logsViewModel.lazyListState.animateScrollToItem(0)
        } }
    }

    AnimatedVisibility(
        visible = isAtBottom(layoutInfo),
        modifier = scrollBottomButtonModifier.systemBarsPadding(),
        enter = scaleIn() + fadeIn(),
        exit = scaleOut() + fadeOut()
    ) {
        FloatingActionButton(
            icon = Icons.Outlined.KeyboardArrowDown
        ) { scope.launch {
            logsViewModel.lazyListState.animateScrollToItem(
                logsViewModel.lazyListState.layoutInfo.totalItemsCount + 1
            )
        } }
    }
}

private fun isAtBottom(layoutInfo: LazyListLayoutInfo): Boolean {
    val lastItem = layoutInfo.visibleItemsInfo.lastOrNull() ?: return true
    return lastItem.index < layoutInfo.totalItemsCount-1
}