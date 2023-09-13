package com.shicheeng.copymanga.ui.screen.main

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.pager.rememberPagerState
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
import com.shicheeng.copymanga.ui.screen.main.home.HomeScreen
import com.shicheeng.copymanga.ui.screen.main.leaderboard.LeaderBoardScreen
import com.shicheeng.copymanga.ui.screen.main.personal.PersonalScreen
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
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
    onSubscribedClick: () -> Unit,
    onHistoryClick: () -> Unit,
    onLibraryClick: () -> Unit,
    onPersonalHeaderClick: (isLogin: Boolean) -> Unit,
    onTopicClick: (pathWord: String, type: Int) -> Unit,
    onTopicHeaderLineClick: () -> Unit,
    onFinishHeaderLineClick: () -> Unit,
    onHotClick: () -> Unit,
) {
    val screens = listOf(
        Router.HOME,
        Router.LEADERBOARD,
        Router.EXPLORE,
        Router.PERSONAL
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
                            if (
                                (pagerState.currentPage == screens.indexOf(screen)) &&
                                (screen.name == Router.PERSONAL.name)
                            ) {
                                onSettingButtonClick()
                            } else {
                                corScope.launch {
                                    pagerState.animateScrollToPage(
                                        page = screens.indexOf(screen),
                                    )
                                }
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
                        onHotHeaderLineClick = onHotClick,
                        onNewestHeaderLineClick = onNewestHeaderLineClick,
                        onFinishHeaderLineClick =onFinishHeaderLineClick,
                        onTopicsClickLineClick = onTopicHeaderLineClick,
                        onTopicCardClick = {
                            onTopicClick(it.pathWord, it.type)
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
                        top = null,
                        theme = null,
                        order = null,
                    ) {
                        onUUid(it.pathWord)
                    }
                }

                3 -> {
                    PersonalScreen(
                        onHistoryClick = onHistoryClick,
                        onLibraryClick = onLibraryClick,
                        onDownloadClick = onDownloadedBtnClick,
                        onSubscribedClick = onSubscribedClick,
                        onPersonalHeaderClick = onPersonalHeaderClick
                    ) {
                        onSettingButtonClick()
                    }
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