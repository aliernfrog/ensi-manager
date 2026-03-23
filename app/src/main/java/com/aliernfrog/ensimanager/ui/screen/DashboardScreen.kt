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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil3.ImageLoader
import coil3.compose.AsyncImage
import coil3.compose.rememberAsyncImagePainter
import coil3.svg.SvgDecoder
import com.aliernfrog.ensimanager.R
import com.aliernfrog.ensimanager.ui.component.SettingsButton
import com.aliernfrog.ensimanager.ui.component.TextWithPlaceholder
import com.aliernfrog.ensimanager.ui.dialog.DestructiveActionDialog
import com.aliernfrog.ensimanager.ui.dialog.ImageDialog
import com.aliernfrog.ensimanager.ui.viewmodel.DashboardViewModel
import com.aliernfrog.ensimanager.util.extension.toastSummary
import io.github.aliernfrog.shared.ui.component.AppScaffold
import io.github.aliernfrog.shared.ui.component.AppTopBar
import io.github.aliernfrog.shared.ui.component.HorizontalSegmentor
import io.github.aliernfrog.shared.ui.component.VerticalSegmentor
import io.github.aliernfrog.shared.ui.component.expressive.ExpressiveButtonRow
import io.github.aliernfrog.shared.ui.component.expressive.ExpressiveRowIcon
import io.github.aliernfrog.shared.ui.component.expressive.toRowFriendlyColor
import io.github.aliernfrog.shared.ui.screen.settings.SettingsDestination
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import java.nio.ByteBuffer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    vm: DashboardViewModel = koinViewModel(),
    onNavigateRequest: (Any) -> Unit
) {
    val scope = rememberCoroutineScope()

    LaunchedEffect(vm.dashboardData) {
        if (vm.dashboardData == null) vm.fetchDashboardData()
    }

    AppScaffold(
        topBar = {
          AppTopBar(
              title = stringResource(R.string.dashboard),
              scrollBehavior = it,
              actions = {
                  SettingsButton(
                      onNavigateSettingsRequest = {
                          onNavigateRequest(SettingsDestination.root)
                      }
                  )
              }
          )
        },
        topAppBarState = vm.topAppBarState
    ) {
        PullToRefreshBox(
            isRefreshing = vm.isFetching,
            onRefresh = { scope.launch {
                vm.fetchDashboardData()
            } }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(vm.scrollState)
                    .navigationBarsPadding()
            ) {
                ScreenContent(vm)
            }
        }
    }
}

@Composable
private fun ScreenContent(
    vm: DashboardViewModel
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    VerticalSegmentor(
        {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = vm.dashboardData?.avatar,
                    contentDescription = null,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.onSurface)
                        .size(100.dp)
                        .clickable {
                            vm.avatarDialogShown = true
                        }
                )
                Column(
                    modifier = Modifier.padding(start = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TextWithPlaceholder(
                        text = vm.dashboardData?.name,
                        placeholderCharRange = Range(12, 18),
                        style = MaterialTheme.typography.titleLarge
                    )
                    TextWithPlaceholder(
                        text = vm.dashboardData?.status,
                        placeholderCharRange = Range(22, 40),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }, {
            vm.dashboardData?.info?.let { data ->
                val rows: List<@Composable () -> Unit> = data.map { info -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
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
                } }

                HorizontalSegmentor(
                    *rows.toTypedArray(),
                    roundness = 0.dp
                )
            }
        },
        dynamic = true,
        itemContainerColor = Color.Transparent,
        modifier = Modifier.padding(12.dp)
    )

    Spacer(Modifier.height(16.dp))

    vm.dashboardData?.actions?.let { actions ->
        val buttons: List<@Composable () -> Unit> = actions.map { action -> {
            ExpressiveButtonRow(
                title = action.label,
                description = action.description,
                contentColor = if (action.destructive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface,
                icon = action.icon?.let { {
                    val defaultIconContainerColor = if (action.destructive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primaryContainer
                    ExpressiveRowIcon(
                        painter = rememberAsyncImagePainter(
                            model = ByteBuffer.wrap(it.toByteArray()),
                            imageLoader = ImageLoader.Builder(context)
                                .components {
                                    add(SvgDecoder.Factory(scaleToDensity = true))
                                }
                                .coroutineContext(Dispatchers.IO)
                                .build()
                        ),
                        containerColor = action.iconContainerColor?.let { hexString ->
                            rememberColorFromHex(hexString, fallback = defaultIconContainerColor).toRowFriendlyColor
                        } ?: defaultIconContainerColor
                    )
                } },
                onClick = action.endpoint?.let { {
                    if (action.destructive) vm.pendingDestructiveAction = action
                    else scope.launch {
                        val response = vm.chosenProfile!!.doRequest({ action.endpoint })
                        vm.topToastState.toastSummary(response)
                    }
                } }
            )
        } }

        VerticalSegmentor(
            *buttons.toTypedArray(),
            modifier = Modifier.padding(12.dp)
        )
    }

    if (vm.avatarDialogShown) ImageDialog(
        onDismissRequest = {
            vm.avatarDialogShown = false
        },
        imageModel = vm.dashboardData?.avatar
    )

    vm.pendingDestructiveAction?.let { action ->
        DestructiveActionDialog(
            action = action,
            onDismissRequest = {
                vm.pendingDestructiveAction = null
            },
            onConfirm = { scope.launch {
                val response = vm.chosenProfile!!.doRequest({ action.endpoint })
                vm.topToastState.toastSummary(response)
                vm.pendingDestructiveAction = null
            } }
        )
    }
}

@Composable
private fun rememberColorFromHex(
    hexColor: String,
    fallback: Color = MaterialTheme.colorScheme.primaryContainer
): Color {
    var safeHex = if (hexColor.startsWith("#")) hexColor.substring(1) else hexColor
    if (safeHex.length == 6) safeHex = "FF$safeHex" // add alpha channel
    return remember(hexColor) {
        try {
            Color(safeHex.toLong(16))
        } catch (_: NumberFormatException) {
            fallback
        }
    }
}
