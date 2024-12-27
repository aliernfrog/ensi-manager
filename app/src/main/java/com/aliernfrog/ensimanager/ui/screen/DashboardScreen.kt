package com.aliernfrog.ensimanager.ui.screen

import android.util.Range
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil3.ImageLoader
import coil3.compose.AsyncImage
import coil3.compose.rememberAsyncImagePainter
import coil3.svg.SvgDecoder
import com.aliernfrog.ensimanager.R
import com.aliernfrog.ensimanager.data.doRequest
import com.aliernfrog.ensimanager.ui.component.AppScaffold
import com.aliernfrog.ensimanager.ui.component.AppTopBar
import com.aliernfrog.ensimanager.ui.component.HorizontalSegmentor
import com.aliernfrog.ensimanager.ui.component.SettingsButton
import com.aliernfrog.ensimanager.ui.component.TextWithPlaceholder
import com.aliernfrog.ensimanager.ui.component.VerticalSegmentor
import com.aliernfrog.ensimanager.ui.component.form.ButtonRow
import com.aliernfrog.ensimanager.ui.dialog.DestructiveActionDialog
import com.aliernfrog.ensimanager.ui.dialog.ImageDialog
import com.aliernfrog.ensimanager.ui.viewmodel.DashboardViewModel
import com.aliernfrog.ensimanager.util.Destination
import com.aliernfrog.ensimanager.util.extension.toastSummary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import java.nio.ByteBuffer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    dashboardViewModel: DashboardViewModel = koinViewModel(),
    onNavigateRequest: (Destination) -> Unit
) {
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        if (dashboardViewModel.dashboardData == null) dashboardViewModel.fetchDashboardData()
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
        PullToRefreshBox(
            isRefreshing = dashboardViewModel.isFetching,
            onRefresh = { scope.launch {
                dashboardViewModel.fetchDashboardData()
            } }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(dashboardViewModel.scrollState)
                    .navigationBarsPadding()
            ) {
                ScreenContent()
            }
        }
    }
}

@Composable
private fun ScreenContent(
    dashboardViewModel: DashboardViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    VerticalSegmentor({
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = dashboardViewModel.dashboardData?.avatar,
                contentDescription = null,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.onSurface)
                    .size(100.dp)
                    .clickable {
                        dashboardViewModel.avatarDialogShown = true
                    }
            )
            Column(
                modifier = Modifier.padding(start = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TextWithPlaceholder(
                    text = dashboardViewModel.dashboardData?.name,
                    placeholderCharRange = Range(12, 18),
                    style = MaterialTheme.typography.titleLarge
                )
                TextWithPlaceholder(
                    text = dashboardViewModel.dashboardData?.status,
                    placeholderCharRange = Range(22, 40),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }, {
        val info: List<@Composable () -> Unit> = dashboardViewModel.dashboardData?.info?.map { info -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                    .padding(vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = info.title,
                    style = MaterialTheme.typography.labelLarge
                )
                Text(
                    text = info.value ?: "-"
                )
            }
        } } ?: listOf()

        HorizontalSegmentor(
            *info.toTypedArray(),
            shape = RectangleShape
        )
    }, modifier = Modifier.padding(8.dp))

    Spacer(Modifier.height(16.dp))

    val buttons: List<@Composable () -> Unit> = dashboardViewModel.dashboardData?.actions?.map { action -> {
        ButtonRow(
            title = action.label,
            description = action.description,
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            contentColor = if (action.destructive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface,
            painter = action.icon?.let { rememberAsyncImagePainter(
                model = ByteBuffer.wrap(it.toByteArray()),
                imageLoader = ImageLoader.Builder(context)
                    .components {
                        add(SvgDecoder.Factory())
                    }
                    .coroutineContext(Dispatchers.IO)
                    .build()
            ) }
        ) {
            if (action.destructive) dashboardViewModel.pendingDestructiveAction = action
            else scope.launch {
                val response = action.endpoint.doRequest()
                dashboardViewModel.topToastState.toastSummary(response)
            }
        }
    } } ?: listOf()

    VerticalSegmentor(
        *buttons.toTypedArray(),
        modifier = Modifier
            .padding(horizontal = 8.dp)
            .padding(bottom = 8.dp)
    )

    if (dashboardViewModel.avatarDialogShown) ImageDialog(
        onDismissRequest = {
            dashboardViewModel.avatarDialogShown = false
        },
        imageModel = dashboardViewModel.dashboardData?.avatar
    )

    dashboardViewModel.pendingDestructiveAction?.let {
        DestructiveActionDialog(
            action = it,
            onDismissRequest = {
                dashboardViewModel.pendingDestructiveAction = null
            },
            onConfirm = { scope.launch {
                val response = it.endpoint.doRequest()
                dashboardViewModel.topToastState.toastSummary(response)
                dashboardViewModel.pendingDestructiveAction = null
            } }
        )
    }
}