package com.aliernfrog.ensimanager.ui.screen

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListLayoutInfo
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.aliernfrog.ensimanager.R
import com.aliernfrog.ensimanager.ui.component.AppScaffold
import com.aliernfrog.ensimanager.ui.component.AppTopBar
import com.aliernfrog.ensimanager.ui.component.FloatingActionButton
import com.aliernfrog.ensimanager.ui.component.SearchField
import com.aliernfrog.ensimanager.ui.component.SegmentedButtons
import com.aliernfrog.ensimanager.ui.component.SettingsButton
import com.aliernfrog.ensimanager.ui.sheet.AddStringSheet
import com.aliernfrog.ensimanager.ui.sheet.StringSheet
import com.aliernfrog.ensimanager.ui.theme.AppFABPadding
import com.aliernfrog.ensimanager.ui.viewmodel.StringsViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)
@Composable
fun StringsScreen(
    stringsViewModel: StringsViewModel = koinViewModel(),
    onNavigateSettingsRequest: () -> Unit
) {
    val scope = rememberCoroutineScope()

    LaunchedEffect(stringsViewModel.categories) {
        if (stringsViewModel.categories.isEmpty()) stringsViewModel.fetchStrings()
    }

    AppScaffold(
        topBar = {
            AppTopBar(
                title = stringResource(R.string.strings),
                scrollBehavior = it,
                actions = {
                    SettingsButton(
                        onNavigateSettingsRequest = onNavigateSettingsRequest
                    )
                }
            )
        },
        topAppBarState = stringsViewModel.topAppBarState
    ) {
        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.TopCenter) {
            PullToRefreshBox(
                isRefreshing = stringsViewModel.isFetching,
                onRefresh = { scope.launch {
                    stringsViewModel.fetchStrings()
                } }
            ) {
                StringsList()
            }
            FloatingButtons(
                scrollTopButtonModifier = Modifier.align(Alignment.TopEnd),
                bottomButtonsColumnModifier = Modifier.align(Alignment.BottomEnd),
                scrollBottomButtonModifier = Modifier.align(Alignment.TopEnd),
                addStringButtonModifier = Modifier.align(Alignment.BottomEnd)
            )
        }
    }

    AddStringSheet()
    StringSheet()
}

@Composable
private fun StringsList(
    stringsViewModel: StringsViewModel = koinViewModel()
) {
    val list = stringsViewModel.currentCategoryList
    val scope = rememberCoroutineScope()
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = stringsViewModel.lazyListState
    ) {
        item {
            ListControls(stringsShown = list.size)
        }
        items(list) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        vertical = 4.dp,
                        horizontal = 8.dp
                    ),
                onClick = { scope.launch {
                    stringsViewModel.showStringSheet(it)
                } }
            ) {
                Text(
                    text = it,
                    modifier = Modifier.padding(
                        vertical = 8.dp,
                        horizontal = 12.dp
                    )
                )
            }
        }
        item {
            Spacer(Modifier.navigationBarsPadding().height(AppFABPadding))
        }
    }
}

@Composable
private fun ListControls(
    stringsViewModel: StringsViewModel = koinViewModel(),
    stringsShown: Int
) {
    SearchField(
        query = stringsViewModel.filter,
        onQueryChange = { stringsViewModel.filter = it },
        modifier = Modifier
            .fillMaxWidth()
            .offset(y = (-12).dp)
            .padding(
                start = 8.dp,
                end = 8.dp
            )
    )

    SegmentedButtons(
        options = stringsViewModel.categories.map { it.title },
        selectedIndex = stringsViewModel.currentCategoryIndex,
        modifier = Modifier.fillMaxWidth().padding(8.dp)
    ) {
        stringsViewModel.currentCategoryIndex = it
    }

    Text(
        text = stringResource(R.string.strings_shownStrings).replace("{COUNT}", stringsShown.toString()),
        modifier = Modifier.padding(horizontal = 8.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("ModifierParameter")
@Composable
private fun FloatingButtons(
    stringsViewModel: StringsViewModel = koinViewModel(),
    scrollTopButtonModifier: Modifier,
    bottomButtonsColumnModifier: Modifier,
    scrollBottomButtonModifier: Modifier,
    addStringButtonModifier: Modifier
) {
    val scope = rememberCoroutineScope()
    val firstVisibleItemIndex by remember {
        derivedStateOf { stringsViewModel.lazyListState.firstVisibleItemIndex }
    }
    val layoutInfo by remember {
        derivedStateOf { stringsViewModel.lazyListState.layoutInfo }
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
            stringsViewModel.lazyListState.animateScrollToItem(0)
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
                stringsViewModel.lazyListState.animateScrollToItem(stringsViewModel.lazyListState.layoutInfo.totalItemsCount + 1)
            } }
        }

        FloatingActionButton(
            icon = Icons.Outlined.Add,
            modifier = addStringButtonModifier,
            containerColor = MaterialTheme.colorScheme.primary
        ) { scope.launch {
            stringsViewModel.addStringSheetState.show()
        } }
    }
}

private fun isAtBottom(layoutInfo: LazyListLayoutInfo): Boolean {
    val lastItem = layoutInfo.visibleItemsInfo.lastOrNull() ?: return true
    return lastItem.index < layoutInfo.totalItemsCount-1
}