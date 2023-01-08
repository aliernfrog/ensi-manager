package com.aliernfrog.ensimanager.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aliernfrog.ensimanager.FetchingState
import com.aliernfrog.ensimanager.R
import com.aliernfrog.ensimanager.state.DashboardState
import com.aliernfrog.ensimanager.ui.composable.ManagerColumn
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DashboardScreen(dashboardState: DashboardState) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val refreshing = dashboardState.fetchingState.value == FetchingState.FETCHING
    val pullRefreshState = rememberPullRefreshState(refreshing, {
        scope.launch { dashboardState.fetchStatus(context) }
    })
    Box(Modifier.fillMaxWidth().pullRefresh(pullRefreshState), contentAlignment = Alignment.TopCenter) {
        Column(Modifier.fillMaxSize().verticalScroll(dashboardState.scrollState)) {
            Status(dashboardState)
            Actions(dashboardState)
        }
        PullRefreshIndicator(
            refreshing = refreshing,
            state = pullRefreshState,
            backgroundColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    }
    LaunchedEffect(Unit) {
        dashboardState.updateApiProperties()
        dashboardState.fetchStatus(context)
    }
}

@Composable
private fun Status(dashboardState: DashboardState) {
    val context = LocalContext.current
    ManagerColumn(title = context.getString(R.string.dashboard_status)) {
        SelectionContainer(Modifier.padding(horizontal = 8.dp)) {
            Text(text = dashboardState.status.value, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun Actions(dashboardState: DashboardState) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    Button(
        onClick = { scope.launch { dashboardState.postAddon(context) } },
        modifier = Modifier.fillMaxWidth().padding(8.dp)
    ) {
        Text(stringResource(R.string.dashboard_post_addon))
    }
    Button(
        onClick = { scope.launch { dashboardState.destroyProcess(context) } },
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
    ) {
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.Start) {
            Text(stringResource(R.string.dashboard_destroy_process))
            Text(stringResource(R.string.dashboard_destroy_process_description), modifier = Modifier.alpha(0.5f), fontSize = 10.sp)
        }
    }
}