package com.shicheeng.copymanga.ui.screen.main

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.shicheeng.copymanga.ui.screen.Router
import com.shicheeng.copymanga.ui.screen.compoents.SaveStatePager
import com.shicheeng.copymanga.ui.screen.main.explore.ExploreScreen
import com.shicheeng.copymanga.ui.screen.main.history.HistoryScreen
import com.shicheeng.copymanga.ui.screen.main.home.HomeScreen
import com.shicheeng.copymanga.ui.screen.main.leaderboard.LeaderBoardScreen
import com.shicheeng.copymanga.ui.screen.main.subscribe.SubScribeScreen
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    mainScreenViewModel: MainScreenViewModel = hiltViewModel(),
    onUUid: (String) -> Unit,
    onDownloadedBtnClick: () -> Unit,
    onSearchButtonClick: () -> Unit,
    onSettingButtonClick: () -> Unit,
    onRecommendHeaderLineClick: () -> Unit,
    onNewestHeaderLineClick: () -> Unit,
) {
    val screens = listOf(
        Router.HOME,
        Router.LEADERBOARD,
        Router.EXPLORE,
        Router.SUBSCRIBE,
        Router.HISTORY
    )
    val pagerState = rememberPagerState(
        pageCount = screens::size,
        initialPage = 0,
        initialPageOffsetFraction = 0f
    )
    val corScope = rememberCoroutineScope()
    val savableStateHolder = rememberSaveableStateHolder()
    val topWord by mainScreenViewModel.top.collectAsState()
    val orderWord by mainScreenViewModel.order.collectAsState()

    Scaffold(
        bottomBar = {
            NavigationBar {
                screens.forEach { screen ->
                    NavigationBarItem(
                        selected = pagerState.currentPage == screens.indexOf(screen),
                        onClick = {
                            corScope.launch {
                                pagerState.animateScrollToPage(screens.indexOf(screen))
                            }
                        },
                        label = {
                            Text(text = stringResource(id = screen.stringId!!))
                        },
                        icon = {
                            Icon(
                                painter = painterResource(
                                    id = if (pagerState.currentPage == screens.indexOf(screen)) {
                                        screen.onClickIcon!!
                                    } else {
                                        screen.drawableRes!!
                                    }
                                ),
                                contentDescription = null
                            )
                        }
                    )
                }
            }
        },
        modifier = modifier,
        contentWindowInsets = WindowInsets(top = 0)
    ) { paddingValues ->
        SaveStatePager(
            pagerState = pagerState,
            contentPadding = paddingValues,
            savableStateHolder = savableStateHolder
        ) { index ->
            when (index) {
                0 -> {
                    HomeScreen(
                        onUUid = onUUid,
                        onSearchButtonClick = onSearchButtonClick,
                        onSettingButtonClick = onSettingButtonClick,
                        onRecommendHeaderLineClick = onRecommendHeaderLineClick,
                        onRankHeaderLineClick = {
                            corScope.launch {
                                pagerState.animateScrollToPage(1)
                            }
                        },
                        onHotHeaderLineClick = {
                            mainScreenViewModel.addOrder("-popular")
                            corScope.launch {
                                pagerState.animateScrollToPage(2)
                            }
                        },
                        onNewestHeaderLineClick = onNewestHeaderLineClick,
                        onFinishHeaderLineClick = {
                            mainScreenViewModel.addTop("finish")
                            corScope.launch {
                                pagerState.animateScrollToPage(2)
                            }
                        }
                    )
                }

                1 -> {
                    LeaderBoardScreen {
                        onUUid(it.comic.pathWord)
                    }
                }

                2 -> {
                    ExploreScreen(
                        top = topWord,
                        theme = null,
                        order = orderWord,
                    ) {
                        onUUid(it.pathWord)
                    }
                }

                3 -> {
                    SubScribeScreen(onPathWord = onUUid)
                }

                4 -> {
                    HistoryScreen(
                        onPathWord = onUUid,
                        onDownloadedBtnClick = onDownloadedBtnClick
                    )
                }
            }
        }
    }

    BackHandler(enabled = pagerState.currentPage != 0) {
        corScope.launch {
            pagerState.animateScrollToPage(0)
        }
    }

}