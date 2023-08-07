package com.shicheeng.copymanga.ui.screen.main.leaderboard

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.shicheeng.copymanga.LocalMainBottomNavigationPadding
import com.shicheeng.copymanga.R
import com.shicheeng.copymanga.data.rank.Item
import com.shicheeng.copymanga.ui.screen.compoents.ErrorScreen
import com.shicheeng.copymanga.ui.screen.compoents.LoadingScreen
import com.shicheeng.copymanga.ui.screen.compoents.pagingLoadingIndication
import com.shicheeng.copymanga.ui.screen.compoents.withAppBarColor
import com.shicheeng.copymanga.util.copy
import com.shicheeng.copymanga.viewmodel.RankViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun LeaderBoardScreen(
    rankViewModel: RankViewModel = hiltViewModel(),
    onRankItemClick: (Item) -> Unit,
) {

    val day = rankViewModel.dayRank.collectAsLazyPagingItems()
    val week = rankViewModel.weekRank.collectAsLazyPagingItems()
    val month = rankViewModel.monthRank.collectAsLazyPagingItems()
    val total = rankViewModel.totalRank.collectAsLazyPagingItems()
    val leaderboardString = listOf(
        stringResource(id = R.string.day_rank),
        stringResource(id = R.string.week_rank),
        stringResource(id = R.string.month_rank),
        stringResource(id = R.string.all_rank)
    )
    val layoutDirection = LocalLayoutDirection.current
    val topAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val paddingBottom = LocalMainBottomNavigationPadding.current
    val scope = rememberCoroutineScope()
    val columns = GridCells.Fixed(3)
    val pagerState = rememberPagerState(
        initialPage = 0,
        initialPageOffsetFraction = 0f,
        pageCount = leaderboardString::size
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(id = R.string.comic_rank))
                },
                scrollBehavior = topAppBarScrollBehavior
            )
        }
    ) {
        Column(
            Modifier
                .nestedScroll(topAppBarScrollBehavior.nestedScrollConnection)
                .fillMaxSize()
                .padding(top = it.calculateTopPadding())
        ) {
            TabRow(
                selectedTabIndex = pagerState.currentPage,
                modifier = Modifier.fillMaxWidth(),
                containerColor = withAppBarColor(topAppBarState = topAppBarScrollBehavior.state)
            ) {
                leaderboardString.forEachIndexed { index, s ->
                    Tab(
                        selected = pagerState.currentPage == index,
                        onClick = {
                            scope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        },
                        text = {
                            Text(text = s)
                        }
                    )
                }
            }

            HorizontalPager(
                state = pagerState,
            ) { index ->
                when (index) {
                    0 -> {
                        LeaderBoradPage(day, columns, it, layoutDirection, paddingBottom, onRankItemClick)
                    }

                    1 -> {
                        LeaderBoradPage(
                            day = week,
                            columns = columns,
                            it = it,
                            layoutDirection = layoutDirection,
                            paddingBottom = paddingBottom,
                            onRankItemClick = onRankItemClick
                        )
                    }

                    2 -> {
                        LeaderBoradPage(
                            day = month,
                            columns = columns,
                            it = it,
                            layoutDirection = layoutDirection,
                            paddingBottom = paddingBottom,
                            onRankItemClick = onRankItemClick
                        )
                    }

                    3 -> {
                        LeaderBoradPage(
                            day = total,
                            columns = columns,
                            it = it,
                            layoutDirection = layoutDirection,
                            paddingBottom = paddingBottom,
                            onRankItemClick = onRankItemClick
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LeaderBoradPage(
    day: LazyPagingItems<Item>,
    columns: GridCells.Fixed,
    it: PaddingValues,
    layoutDirection: LayoutDirection,
    paddingBottom: Dp,
    onRankItemClick: (Item) -> Unit,
) {
    when (day.loadState.refresh) {
        is LoadState.Loading -> {
            LoadingScreen()
        }

        is LoadState.Error -> {
            ErrorScreen(errorMessage = stringResource(id = R.string.error)) {
                day.refresh()
            }
        }

        else -> {
            LazyVerticalGrid(
                columns = columns,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = it.copy(
                    layoutDirection = layoutDirection,
                    bottom = it.calculateBottomPadding() + paddingBottom,
                    start = 16.dp,
                    end = 16.dp,
                    top = 16.dp
                ),
            ) {
                items(day.itemCount) { itemIndex ->
                    val item = day[itemIndex]
                    if (item != null) {
                        LeaderBoardItem(
                            item = item,
                            onItemClick = onRankItemClick
                        )
                    } else {
                        LeaderBoardItemPlaceholder()
                    }
                }
                pagingLoadingIndication(
                    loadState = day.loadState.append
                ) {
                    day.retry()
                }
            }
        }
    }
}

