package com.shicheeng.copymanga.ui.screen.main.explore

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.shicheeng.copymanga.LocalMainBottomNavigationPadding
import com.shicheeng.copymanga.R
import com.shicheeng.copymanga.data.finished.Item
import com.shicheeng.copymanga.json.MangaSortJson
import com.shicheeng.copymanga.ui.screen.compoents.ErrorScreen
import com.shicheeng.copymanga.ui.screen.compoents.LoadingScreen
import com.shicheeng.copymanga.ui.screen.compoents.PlainButton
import com.shicheeng.copymanga.ui.screen.compoents.pagingLoadingIndication
import com.shicheeng.copymanga.ui.screen.compoents.withAppBarColor
import com.shicheeng.copymanga.ui.theme.ElevationTokens
import com.shicheeng.copymanga.util.UIState
import com.shicheeng.copymanga.util.copy
import com.shicheeng.copymanga.viewmodel.ExploreMangaViewModel

@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class,
    ExperimentalFoundationApi::class
)
@Composable
fun ExploreScreen(
    top: String?,
    theme: String?,
    order: String?,
    exploreMangaViewModel: ExploreMangaViewModel = hiltViewModel(),
    onNavigationIconClick: (() -> Unit)? = null,
    onItemClick: (Item) -> Unit,
) {

    val sortLoadingState by exploreMangaViewModel.uiState.collectAsState()
    val mangaList = exploreMangaViewModel.loadFilterResult.collectAsLazyPagingItems()
    val bottomWhatToShow by exploreMangaViewModel.showBottomSheet.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    if (sortLoadingState is UIState.Loading) {
        LoadingScreen()
        return
    }

    if (sortLoadingState is UIState.Error<*>) {
        ErrorScreen(
            errorMessage = (sortLoadingState as UIState.Error<*>).errorMessage.message
                ?: stringResource(id = R.string.error)
        ) {
            exploreMangaViewModel.loadData()
        }
        return
    }

    val successData = sortLoadingState as UIState.Success
    if (top != null || order != null || theme != null) {
        LaunchedEffect(Unit) {
            exploreMangaViewModel.filterOn(
                order, theme, top
            )
        }
    }
    var isExpand by remember { mutableStateOf(false) }
    val paddingBottom = LocalMainBottomNavigationPadding.current
    val layoutDirection = LocalLayoutDirection.current
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = false
    )
    val pullRefreshState = rememberPullRefreshState(
        refreshing = mangaList.loadState.refresh is LoadState.Loading,
        onRefresh = {
            mangaList.refresh()
        }
    )
    val (orderSave, onOrderSave) = rememberSaveable {
        mutableStateOf<String?>(null)
    }
    val (topSave, onTopSave) = rememberSaveable {
        mutableStateOf<String?>(null)
    }
    val (themeSave, onThemeSave) = rememberSaveable {
        mutableStateOf<String?>(null)
    }
    val hashMap = remember {
        mutableStateMapOf(
            MangaSortJson.ORDER to MangaSortJson.order.find { x -> x.pathWord == orderSave || x.pathWord == order },
            MangaSortJson.THEME to successData.content.find { x -> x.pathWord == themeSave || x.pathWord == theme },
            MangaSortJson.PATH to MangaSortJson.topPath.find { x -> x.pathWord == topSave || x.pathWord == top },
        )
    }
    val topAppBarState = rememberTopAppBarState()
    val topAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(state = topAppBarState)


    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Text(text = stringResource(id = R.string.explore))
                    },
                    navigationIcon = {
                        if (onNavigationIconClick != null) {
                            PlainButton(
                                id = R.string.back_to_up,
                                drawableRes = R.drawable.ic_arrow_back,
                                onButtonClick = onNavigationIconClick
                            )
                        }
                    },
                    scrollBehavior = topAppBarScrollBehavior
                )
                ExploreFilter(
                    showList = hashMap,
                    onThemeClick = {
                        exploreMangaViewModel.showThemeFilterList()
                        isExpand = true
                    },
                    onOrderClick = {
                        exploreMangaViewModel.showOrderFilterList()
                        isExpand = true
                    },
                    modifier = Modifier
                        .background(withAppBarColor(topAppBarState = topAppBarState))
                        .padding(horizontal = 16.dp)
                ) {
                    exploreMangaViewModel.showTopFilterList()
                    isExpand = true
                }
                HorizontalDivider()
            }
        },
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .pullRefresh(state = pullRefreshState)
                .fillMaxSize()
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                contentPadding = paddingValues.copy(
                    layoutDirection = layoutDirection,
                    bottom = paddingValues.calculateBottomPadding() + paddingBottom,
                    start = 16.dp,
                    end = 16.dp,
                    top = paddingValues.calculateTopPadding() + 16.dp
                ),
                modifier = Modifier
                    .fillMaxSize()
                    .nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(
                    mangaList.itemCount,
                    key = mangaList.itemKey(),
                    contentType = mangaList.itemContentType()
                ) { itemIndex ->
                    mangaList[itemIndex]?.let { item ->
                        ExploreItem(
                            item = item,
                            onItemClick = onItemClick,
                            modifier = Modifier.animateItemPlacement()
                        )
                    }
                }
                pagingLoadingIndication(
                    loadState = mangaList.loadState.append
                ) {
                    mangaList.retry()
                }
            }

            PullRefreshIndicator(
                refreshing = mangaList.loadState.refresh is LoadState.Loading,
                state = pullRefreshState,
                contentColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .padding(top = paddingValues.calculateTopPadding())
                    .align(Alignment.TopCenter)
            )
        }
    }

    val cornerShape by animateDpAsState(
        targetValue = if (sheetState.currentValue == SheetValue.Expanded) {
            0.dp
        } else {
            28.0.dp
        },
        label = "DP_OF_SHEET"
    )
    if (isExpand) {
        ModalBottomSheet(
            onDismissRequest = { isExpand = false },
            sheetState = sheetState,
            windowInsets = WindowInsets(0, 0, 0, 0),
            modifier = Modifier.zIndex(1f),
            shape = RoundedCornerShape(
                topEnd = cornerShape,
                topStart = cornerShape,
                bottomStart = 0.dp,
                bottomEnd = 0.dp
            ),
            tonalElevation = ElevationTokens.Level2
        ) {
            when (bottomWhatToShow) {
                MangaSortJson.ORDER -> {
                    ExploreFilterBottomSheet(
                        list = MangaSortJson.order,
                        sortBean = hashMap[MangaSortJson.ORDER],
                        title = stringResource(id = R.string.order)
                    ) {
                        hashMap[MangaSortJson.ORDER] = it
                        exploreMangaViewModel.filterOn(
                            theme = hashMap[MangaSortJson.THEME]?.pathWord,
                            top = hashMap[MangaSortJson.PATH]?.pathWord,
                            order = it.pathWord.ifBlank { null }
                        )
                        onOrderSave(it.pathWord)
                    }
                }

                MangaSortJson.THEME -> {
                    ExploreFilterBottomSheet(
                        list = successData.content,
                        sortBean = hashMap[MangaSortJson.THEME],
                        title = stringResource(id = R.string.theme)
                    ) {
                        hashMap[MangaSortJson.THEME] = it
                        exploreMangaViewModel.filterOn(
                            theme = it.pathWord.ifBlank { null },
                            top = hashMap[MangaSortJson.PATH]?.pathWord,
                            order = hashMap[MangaSortJson.ORDER]?.pathWord
                        )
                        onThemeSave(it.pathWord)
                    }
                }

                MangaSortJson.PATH -> {
                    ExploreFilterBottomSheet(
                        list = MangaSortJson.topPath,
                        sortBean = hashMap[MangaSortJson.PATH],
                        title = stringResource(id = R.string.top),
                        onSelected = {
                            hashMap[MangaSortJson.PATH] = it
                            exploreMangaViewModel.filterOn(
                                theme = hashMap[MangaSortJson.THEME]?.pathWord,
                                top = it.pathWord.ifBlank { null },
                                order = hashMap[MangaSortJson.ORDER]?.pathWord
                            )
                            onTopSave(it.pathWord)
                        }
                    )
                }
            }
        }
    }

    BackHandler(enabled = isExpand) {
        isExpand = false
    }

}

