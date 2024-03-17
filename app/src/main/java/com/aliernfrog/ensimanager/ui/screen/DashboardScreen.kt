package com.aliernfrog.ensimanager.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.aliernfrog.ensimanager.R
import com.aliernfrog.ensimanager.ui.component.AppScaffold
import com.aliernfrog.ensimanager.ui.component.AppTopBar
import com.aliernfrog.ensimanager.ui.component.ColumnRounded
import com.aliernfrog.ensimanager.ui.component.SettingsButton
import com.aliernfrog.ensimanager.ui.component.VerticalSegmentedButtons
import com.aliernfrog.ensimanager.ui.component.form.ButtonRow
import com.aliernfrog.ensimanager.ui.viewmodel.DashboardViewModel
import com.aliernfrog.ensimanager.util.Destination
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    dashboardViewModel: DashboardViewModel = koinViewModel(),
    onNavigateRequest: (Destination) -> Unit
) {
    val pullToRefreshState = rememberPullToRefreshState()

    LaunchedEffect(Unit) {
        dashboardViewModel.fetchStatus()
        dashboardViewModel.fetchLogs()
    }

    if (pullToRefreshState.isRefreshing) LaunchedEffect(Unit) {
        dashboardViewModel.fetchStatus()
        dashboardViewModel.fetchLogs()
    }

    LaunchedEffect(dashboardViewModel.isFetching) {
        if (dashboardViewModel.isFetching) pullToRefreshState.startRefresh()
        else pullToRefreshState.endRefresh()
    }

    AppScaffold(
        topBar = {
          AppTopBar(
              title = stringResource(R.string.dashboard),
              scrollBehavior = it,
              actions = {
                  SettingsButton(
                      onClick = { onNavigateRequest(Destination.SETTINGS) }
                  )
              }
          )
        },
        topAppBarState = dashboardViewModel.topAppBarState
    ) {
        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.TopCenter) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .nestedScroll(pullToRefreshState.nestedScrollConnection)
                    .verticalScroll(dashboardViewModel.scrollState)
            ) {
                ScreenContent(
                    onNavigateLogsScreenRequest = {
                        onNavigateRequest(Destination.LOGS)
                    }
                )
            }
            PullToRefreshContainer(
                state = pullToRefreshState
            )
        }
    }
}

@Composable
private fun ScreenContent(
    dashboardViewModel: DashboardViewModel = koinViewModel(),
    onNavigateLogsScreenRequest: () -> Unit
) {
    val scope = rememberCoroutineScope()

    ColumnRounded(
        title = stringResource(R.string.dashboard_status),
        modifier = Modifier.padding(bottom = 8.dp)
    ) {
        SelectionContainer(Modifier.padding(horizontal = 8.dp)) {
            Text(
                text = dashboardViewModel.status,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }

    VerticalSegmentedButtons({
        ButtonRow(
            title = stringResource(R.string.logs),
            description = stringResource(R.string.logs_description),
            painter = rememberVectorPainter(Icons.AutoMirrored.Filled.Notes),
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            expanded = false,
            arrowRotation = if (LocalLayoutDirection.current == LayoutDirection.Rtl) 270f else 90f
        ) {
            onNavigateLogsScreenRequest()
        }
    }, {
        ButtonRow(
            title = stringResource(R.string.dashboard_post_addon),
            description = stringResource(R.string.dashboard_post_addon_description),
            painter = rememberVectorPainter(Icons.Default.Upload),
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        ) {
            scope.launch { dashboardViewModel.postEnsicordAddon() }
        }
    }, {
        ButtonRow(
            title = stringResource(R.string.dashboard_destroy_process),
            description = stringResource(R.string.dashboard_destroy_process_description),
            painter = rememberVectorPainter(Icons.Default.Close),
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            contentColor = MaterialTheme.colorScheme.error
        ) {
            scope.launch { dashboardViewModel.destroyProcess() }
        }
    }, modifier = Modifier.padding(horizontal = 8.dp))
}