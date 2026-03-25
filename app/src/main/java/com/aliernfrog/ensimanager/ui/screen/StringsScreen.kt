package com.aliernfrog.ensimanager.ui.screen

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.LazyListState
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
import com.aliernfrog.ensimanager.data.api.APIStringsCategory
import com.aliernfrog.ensimanager.ui.component.SearchField
import com.aliernfrog.ensimanager.ui.component.SettingsButton
import com.aliernfrog.ensimanager.ui.sheet.AddStringSheet
import com.aliernfrog.ensimanager.ui.sheet.StringSheet
import com.aliernfrog.ensimanager.ui.viewmodel.StringsViewModel
import io.github.aliernfrog.shared.ui.component.AppScaffold
import io.github.aliernfrog.shared.ui.component.AppTopBar
import io.github.aliernfrog.shared.ui.component.FloatingActionButton
import io.github.aliernfrog.shared.ui.component.SingleChoiceConnectedButtonGroup
import io.github.aliernfrog.shared.ui.theme.AppFABPadding
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)
@Composable
fun StringsScreen(
    vm: StringsViewModel = koinViewModel(),
    onNavigateSettingsRequest: () -> Unit
) {
    val scope = rememberCoroutineScope()

    LaunchedEffect(vm.categories) {
        if (vm.categories.isEmpty()) vm.fetchStrings()
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
        topAppBarState = vm.topAppBarState
    ) {
        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.TopCenter) {
            PullToRefreshBox(
                isRefreshing = vm.isFetching,
                onRefresh = { scope.launch {
                    vm.fetchStrings()
                } }
            ) {
                StringsList(vm)
            }
            FloatingButtons(
                lazyListState = vm.lazyListState,
                scrollTopButtonModifier = Modifier.align(Alignment.TopEnd),
                bottomButtonsColumnModifier = Modifier.align(Alignment.BottomEnd),
                scrollBottomButtonModifier = Modifier.align(Alignment.TopEnd),
                addStringButtonModifier = Modifier.align(Alignment.BottomEnd),
                onAddStringRequest = { scope.launch {
                    vm.addStringSheetState.show()
                } }
            )
        }
    }

    AddStringSheet(
        state = vm.addStringSheetState,
        currentCategory = vm.currentCategory,
        onAddStringRequest = { scope.launch {
            vm.addString(it)
            vm.addStringSheetState.hide()
        } }
    )
    StringSheet(
        state = vm.stringSheetState,
        string = vm.chosenString,
        stringCategory = vm.chosenStringCategory,
        onDeleteStringRequest = { scope.launch {
            vm.deleteChosenWord()
            vm.stringSheetState.hide()
        } }
    )
}

@Composable
private fun StringsList(
    vm: StringsViewModel
) {
    val list = vm.currentCategoryList
    val scope = rememberCoroutineScope()
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = vm.lazyListState
    ) {
        item {
            ListControls(
                stringsShown = list.size,
                categories = vm.categories,
                currentCategoryIndex = vm.currentCategoryIndex,
                onCategoryIndexChange = { vm.currentCategoryIndex = it },
                filter = vm.filter,
                onFilterChange = { vm.filter = it }
            )
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
                    vm.showStringSheet(it)
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
    stringsShown: Int,
    categories: List<APIStringsCategory>,
    currentCategoryIndex: Int,
    onCategoryIndexChange: (Int) -> Unit,
    filter: String,
    onFilterChange: (String) -> Unit
) {
    SearchField(
        query = filter,
        onQueryChange = onFilterChange,
        modifier = Modifier
            .fillMaxWidth()
            .offset(y = (-12).dp)
            .padding(
                start = 8.dp,
                end = 8.dp
            )
    )

    SingleChoiceConnectedButtonGroup(
        choices = categories.map { it.title },
        selectedIndex = currentCategoryIndex,
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        onSelect = onCategoryIndexChange
    )

    Text(
        text = stringResource(R.string.strings_shownStrings).replace("{COUNT}", stringsShown.toString()),
        modifier = Modifier.padding(horizontal = 8.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("ModifierParameter")
@Composable
private fun FloatingButtons(
    lazyListState: LazyListState,
    scrollTopButtonModifier: Modifier,
    bottomButtonsColumnModifier: Modifier,
    scrollBottomButtonModifier: Modifier,
    addStringButtonModifier: Modifier,
    onAddStringRequest: () -> Unit
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

    Column(
        modifier = bottomButtonsColumnModifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        AnimatedVisibility(
            visible = isAtBottom(layoutInfo),
            modifier = scrollBottomButtonModifier,
            enter = scaleIn() + fadeIn(),
            exit = scaleOut() + fadeOut()
        ) {
            FloatingActionButton(
                icon = Icons.Outlined.KeyboardArrowDown
            ) { scope.launch {
                lazyListState.animateScrollToItem(lazyListState.layoutInfo.totalItemsCount + 1)
            } }
        }

        FloatingActionButton(
            icon = Icons.Outlined.Add,
            modifier = addStringButtonModifier,
            containerColor = MaterialTheme.colorScheme.primary,
            onClick = onAddStringRequest
        )
    }
}

private fun isAtBottom(layoutInfo: LazyListLayoutInfo): Boolean {
    val lastItem = layoutInfo.visibleItemsInfo.lastOrNull() ?: return true
    return lastItem.index < layoutInfo.totalItemsCount-1
}