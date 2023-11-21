package com.aliernfrog.ensimanager.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.aliernfrog.ensimanager.R
import com.aliernfrog.ensimanager.ui.component.AppScaffold
import com.aliernfrog.ensimanager.ui.component.ColumnRounded
import com.aliernfrog.ensimanager.ui.component.VerticalSegmentedButtons
import com.aliernfrog.ensimanager.ui.component.form.ButtonRow
import com.aliernfrog.ensimanager.ui.viewmodel.DashboardViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.compose.getViewModel

@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    dashboardViewModel: DashboardViewModel = getViewModel()
) {
    val scope = rememberCoroutineScope()
    val fetching = dashboardViewModel.isFetching
    val pullRefreshState = rememberPullRefreshState(fetching, {
        scope.launch { dashboardViewModel.fetchStatus() }
    })

    AppScaffold(
        title = stringResource(R.string.dashboard),
        topAppBarState = dashboardViewModel.topAppBarState
    ) {
        Box(Modifier.fillMaxWidth().pullRefresh(pullRefreshState), contentAlignment = Alignment.TopCenter) {
            Column(
                modifier = Modifier.fillMaxSize().verticalScroll(dashboardViewModel.scrollState),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ScreenContent()
            }
            PullRefreshIndicator(
                refreshing = fetching,
                state = pullRefreshState,
                backgroundColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
            )
        }
    }

    LaunchedEffect(Unit) {
        dashboardViewModel.fetchStatus()
    }
}

@Composable
private fun ScreenContent(
    dashboardViewModel: DashboardViewModel = getViewModel()
) {
    val scope = rememberCoroutineScope()

    ColumnRounded(title = stringResource(R.string.dashboard_status)) {
        SelectionContainer(Modifier.padding(horizontal = 8.dp)) {
            Text(
                text = dashboardViewModel.status,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
    VerticalSegmentedButtons({
        ButtonRow(
            title = stringResource(R.string.dashboard_post_addon),
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
    }, modifier = Modifier.padding(8.dp))
}