package com.shicheeng.copymanga.ui.screen.main.home

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.shicheeng.copymanga.R
import com.shicheeng.copymanga.data.MainTopicDataModel
import com.shicheeng.copymanga.ui.screen.compoents.ErrorScreen
import com.shicheeng.copymanga.ui.screen.compoents.LoadingScreen
import com.shicheeng.copymanga.ui.screen.compoents.PlainButton
import com.shicheeng.copymanga.util.UIState
import com.shicheeng.copymanga.util.copy
import com.shicheeng.copymanga.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel = hiltViewModel(),
    onUUid: (String) -> Unit,
    onSearchButtonClick: () -> Unit,
    onSettingButtonClick: () -> Unit,
    onRecommendHeaderLineClick: () -> Unit,
    onRankHeaderLineClick: () -> Unit,
    onHotHeaderLineClick: () -> Unit,
    onNewestHeaderLineClick: () -> Unit,
    onFinishHeaderLineClick: () -> Unit,
    onTopicCardClick: (MainTopicDataModel) -> Unit,
    onTopicsClickLineClick: () -> Unit,
) {

    val uiState by homeViewModel.uiState.collectAsState()

    if (uiState is UIState.Loading) {
        LoadingScreen()
        return
    }

    if (uiState is UIState.Error<*>) {
        ErrorScreen(
            errorMessage = (uiState as UIState.Error<*>).errorMessage.message
                ?: stringResource(id = R.string.error),
            secondaryText = stringResource(id = R.string.setting),
            onTry = {
                homeViewModel.loadData()
            },
            onSecondaryClick = onSettingButtonClick
        )
        return
    }

    val successUIState = uiState as UIState.Success
    val layoutDirection = LocalLayoutDirection.current
    val listRank = listOf(
        stringResource(id = R.string.day_rank),
        stringResource(id = R.string.week_rank),
        stringResource(id = R.string.month_rank)
    )
    var selectTabIndex by remember {
        mutableIntStateOf(0)
    }
    val lazyListState = rememberLazyListState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            val firstVisibleItemIndex by remember {
                derivedStateOf { lazyListState.firstVisibleItemIndex }
            }
            val firstVisibleItemScrollOffset by remember {
                derivedStateOf { lazyListState.firstVisibleItemScrollOffset }
            }
            val animatedTitleAlpha by animateFloatAsState(
                if (firstVisibleItemIndex > 0) 1f else 0f, label = "animated_title_alpha",
            )
            val animatedBgAlpha by animateFloatAsState(
                if (firstVisibleItemIndex > 0 || firstVisibleItemScrollOffset > 0) 1f else 0f,
                label = "animated_background_alpha",
            )
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.app_name),
                        modifier = Modifier.alpha(animatedTitleAlpha)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
                        .copy(alpha = animatedBgAlpha)
                ),
                actions = {
                    PlainButton(
                        id = androidx.appcompat.R.string.search_menu_title,
                        drawableRes = R.drawable.ic_manga_search,
                        onButtonClick = onSearchButtonClick
                    )
                    PlainButton(
                        id = R.string.setting,
                        drawableRes = R.drawable.ic_setting_outline,
                        onButtonClick = onSettingButtonClick
                    )
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = padding.copy(
                layoutDirection = layoutDirection,
                bottom = padding.calculateBottomPadding() ,
                top = 0.dp
            ),
            state = lazyListState
        ) {
            item(
                key = HomeListKey.BANNER,
                contentType = HomeListKey.BANNER
            ) {
                Banner(
                    list = successUIState.content.listBanner,
                    modifier = Modifier.height(250.dp)
                ) { model ->
                    onUUid.invoke(model.uuidManga)
                }
            }
            item(
                key = HomeListKey.RECOMMEND,
                contentType = HomeListKey.RECOMMEND
            ) {
                HomeBarColumn(
                    title = stringResource(id = R.string.recommend),
                    list = successUIState.content.listRecommend,
                    onHeaderLineClick = onRecommendHeaderLineClick
                ) {
                    onUUid.invoke(it.pathWordManga)
                }
            }
            miniLeaderBoard(
                selectedTabIndex = selectTabIndex,
                onTabClick = { index ->
                    selectTabIndex = index
                },
                rankList = listRank,
                mainPageDataModel = successUIState.content,
                onHeaderLineClick = onRankHeaderLineClick
            ) {
                onUUid.invoke(it.pathWord)
            }
            item(
                key = HomeListKey.HOT,
                contentType = HomeListKey.HOT
            ) {
                HomeBarColumn(
                    title = stringResource(id = R.string.hot_manga),
                    list = successUIState.content.listHot,
                    onHeaderLineClick = onHotHeaderLineClick
                ) {
                    onUUid.invoke(it.pathWordManga)
                }
            }
            item(
                key = HomeListKey.NEWEST,
                contentType = HomeListKey.NEWEST
            ) {
                HomeBarColumn(
                    title = stringResource(id = R.string.new_manga),
                    list = successUIState.content.listNewest,
                    onHeaderLineClick = onNewestHeaderLineClick
                ) {
                    onUUid.invoke(it.pathWordManga)
                }
            }
            item(
                key = HomeListKey.FINISH,
                contentType = HomeListKey.FINISH
            ) {
                HomeBarColumn(
                    title = stringResource(id = R.string.finish_manga),
                    list = successUIState.content.listFinished,
                    onHeaderLineClick = onFinishHeaderLineClick
                ) {
                    onUUid.invoke(it.pathWordManga)
                }
            }
            item(
                key = HomeListKey.TOPICS_RECOMMEND,
                contentType = HomeListKey.TOPICS_RECOMMEND
            ) {
                HomePageTopicRow(
                    list = successUIState.content.topicList,
                    onTopicBarClick = onTopicsClickLineClick,
                    onItemClick = onTopicCardClick
                )
            }
        }
    }

}

