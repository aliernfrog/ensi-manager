package com.aliernfrog.ensimanager.ui.screen

import android.annotation.SuppressLint
import android.text.format.DateUtils
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListLayoutInfo
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aliernfrog.ensimanager.R
import com.aliernfrog.ensimanager.data.EnsiLog
import com.aliernfrog.ensimanager.ui.component.AppScaffold
import com.aliernfrog.ensimanager.ui.component.FloatingActionButton
import com.aliernfrog.ensimanager.ui.viewmodel.DashboardViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.compose.getViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogsScreen(
    dashboardViewModel: DashboardViewModel = getViewModel(),
    onBackClick: () -> Unit
) {
    val pullToRefreshState = rememberPullToRefreshState()

    LaunchedEffect(Unit) {
        dashboardViewModel.fetchLogs()
    }

    if (pullToRefreshState.isRefreshing) LaunchedEffect(Unit) {
        dashboardViewModel.fetchLogs()
    }

    LaunchedEffect(dashboardViewModel.isFetching) {
        if (dashboardViewModel.isFetching) pullToRefreshState.startRefresh()
        else pullToRefreshState.endRefresh()
    }

    AppScaffold(
        title = stringResource(R.string.logs),
        topAppBarState = dashboardViewModel.logsTopAppBarState,
        onBackClick = onBackClick
    ) {
        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.TopCenter) {
            LogsList(
                nestedScrollConnection = pullToRefreshState.nestedScrollConnection
            )
            FloatingButtons(
                scrollTopButtonModifier = Modifier.align(Alignment.TopEnd),
                scrollBottomButtonModifier = Modifier.align(Alignment.BottomEnd)
            )
            PullToRefreshContainer(
                state = pullToRefreshState
            )
        }
    }
}

@Composable
private fun LogsList(
    dashboardViewModel: DashboardViewModel = getViewModel(),
    nestedScrollConnection: NestedScrollConnection
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().nestedScroll(nestedScrollConnection),
        state = dashboardViewModel.logsLazyListState
    ) {
        itemsIndexed(dashboardViewModel.logs) { index, item ->
            LogItem(
                log = item,
                isLastItem = index == dashboardViewModel.logs.size-1
            )
        }
        item {
            Spacer(Modifier.height(70.dp))
        }
    }
}

@Composable
private fun LogItem(
    log: EnsiLog,
    isLastItem: Boolean
) {
    val context = LocalContext.current
    val color = log.type.getColor()
    val symbolColor = MaterialTheme.colorScheme.contentColorFor(color)
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Max)
                .clickable {}
        ) {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(35.dp)
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
                modifier = Modifier.padding(horizontal = 4.dp)
            ) {
                Text(
                    text = DateUtils.getRelativeDateTimeString(
                        /* c = */ context,
                        /* time = */ log.date,
                        /* minResolution = */ DateUtils.SECOND_IN_MILLIS,
                        /* transitionResolution = */ DateUtils.DAY_IN_MILLIS,
                        /* flags = */ 0
                    ).toString(),
                    fontSize = 14.sp,
                    modifier = Modifier.alpha(0.5f)
                )
                Text(
                    text = log.str,
                    fontFamily = FontFamily.Monospace
                )
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
    dashboardViewModel: DashboardViewModel = getViewModel(),
    scrollTopButtonModifier: Modifier,
    scrollBottomButtonModifier: Modifier
) {
    val scope = rememberCoroutineScope()
    val firstVisibleItemIndex by remember {
        derivedStateOf { dashboardViewModel.logsLazyListState.firstVisibleItemIndex }
    }
    val layoutInfo by remember {
        derivedStateOf { dashboardViewModel.logsLazyListState.layoutInfo }
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
            dashboardViewModel.logsLazyListState.animateScrollToItem(0)
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
            dashboardViewModel.logsLazyListState.animateScrollToItem(
                dashboardViewModel.logsLazyListState.layoutInfo.totalItemsCount + 1
            )
        } }
    }
}

private fun isAtBottom(layoutInfo: LazyListLayoutInfo): Boolean {
    val lastItem = layoutInfo.visibleItemsInfo.lastOrNull() ?: return true
    return lastItem.index < layoutInfo.totalItemsCount-1
}