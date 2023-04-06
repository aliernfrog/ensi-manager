package com.aliernfrog.ensimanager.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.aliernfrog.ensimanager.R
import com.aliernfrog.ensimanager.enum.TopBarStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScaffold(
    title: String,
    topAppBarState: TopAppBarState = TopAppBarState(0F,0F,0F),
    topBarStyle: TopBarStyle = TopBarStyle.LARGE,
    topBarActions: @Composable RowScope.() -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    onBackClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val scrollBehavior = when (topBarStyle) {
        TopBarStyle.LARGE -> TopAppBarDefaults.exitUntilCollapsedScrollBehavior(topAppBarState)
        TopBarStyle.PINNED -> TopAppBarDefaults.pinnedScrollBehavior()
    }
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            AppStyledTopBar(
                style = topBarStyle,
                title = title,
                scrollBehavior = scrollBehavior,
                onBackClick = onBackClick,
                actions = topBarActions
            )
        },
        floatingActionButton = floatingActionButton,
        contentWindowInsets = WindowInsets(0,0,0,0),
        content = {
            Box(modifier = Modifier.padding(it)) {
                content()
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppStyledTopBar(
    style: TopBarStyle,
    title: String,
    scrollBehavior: TopAppBarScrollBehavior,
    onBackClick: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {}
) {
    when (style) {
        TopBarStyle.LARGE -> AppLargeTopBar(
            title = title,
            scrollBehavior = scrollBehavior,
            onBackClick = onBackClick,
            actions = actions
        )
        TopBarStyle.PINNED -> AppPinnedTopBar(
            title = title,
            scrollBehavior = scrollBehavior,
            onBackClick = onBackClick,
            actions = actions
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppLargeTopBar(
    title: String,
    scrollBehavior: TopAppBarScrollBehavior,
    onBackClick: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {}
) {
    LargeTopAppBar(
        title = { Text(title) },
        scrollBehavior = scrollBehavior,
        navigationIcon = { BackButton(onBackClick) },
        actions = actions
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppPinnedTopBar(
    title: String,
    scrollBehavior: TopAppBarScrollBehavior,
    onBackClick: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {}
) {
    TopAppBar(
        title = { Text(title) },
        scrollBehavior = scrollBehavior,
        navigationIcon = { BackButton(onBackClick) },
        actions = actions
    )
}

@Composable
private fun BackButton(onClick: (() -> Unit)?) {
    if (onClick != null) IconButton(onClick = onClick) {
        Icon(
            imageVector = Icons.Rounded.ArrowBack,
            contentDescription = stringResource(R.string.action_back),
            modifier = Modifier.padding(horizontal = 8.dp)
        )
    }
}