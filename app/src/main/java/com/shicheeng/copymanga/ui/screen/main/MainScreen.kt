package com.shicheeng.copymanga.ui.screen.main

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.shicheeng.copymanga.R
import com.shicheeng.copymanga.ui.screen.Router
import com.shicheeng.copymanga.ui.screen.compoents.SaveStateContentPager
import com.shicheeng.copymanga.ui.screen.main.explore.ExploreScreen
import com.shicheeng.copymanga.ui.screen.main.home.HomeScreen
import com.shicheeng.copymanga.ui.screen.main.leaderboard.LeaderBoardScreen
import com.shicheeng.copymanga.ui.screen.main.personal.PersonalScreen
import com.shicheeng.copymanga.viewmodel.MainViewModel
import kotlinx.coroutines.launch
import retrofit2.HttpException

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    mainViewModel: MainViewModel = hiltViewModel(),
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
    onLoginExpireClick: () -> Unit,
    onHotClick: () -> Unit,
) {
    val screens = listOf(
        Router.HOME,
        Router.LEADERBOARD,
        Router.EXPLORE,
        Router.PERSONAL
    )
    val corScope = rememberCoroutineScope()
    val savableStateHolder = rememberSaveableStateHolder()
    var selectIndex by rememberSaveable { mutableIntStateOf(0) }
    val loginStatus by mainViewModel.loginInfoStatus.collectAsState()
    val showSnack by mainViewModel.showSnackBar.collectAsState()
    val snackStateHost = remember(::SnackbarHostState)
    val localContext = LocalContext.current

    LaunchedEffect(key1 = loginStatus) {
        if (loginStatus != null && showSnack) {
            if (loginStatus is HttpException && (loginStatus as HttpException).code() == 401) {
                snackStateHost.showSnackbar(
                    message = localContext.getString(R.string.login_expired),
                    actionLabel = localContext.getString(R.string.re_login),
                    duration = SnackbarDuration.Short
                ).also {
                    if (it == SnackbarResult.ActionPerformed) {
                        onLoginExpireClick()
                    }
                    if (it == SnackbarResult.Dismissed) {
                        mainViewModel.dismissShack()
                    }
                }
                mainViewModel.dismissShack()
            } else {
                snackStateHost.showSnackbar(
                    message = localContext.getString(R.string.login_failure),
                    withDismissAction = true,
                    duration = SnackbarDuration.Short
                )
                mainViewModel.dismissShack()
            }
        }
    }

    Scaffold(
        bottomBar = {
            NavigationBar {
                screens.forEach { screen ->
                    NavigationBarItem(
                        selected = selectIndex == screens.indexOf(screen),
                        onClick = {
                            if (
                                (selectIndex == screens.indexOf(screen)) &&
                                (screen.name == Router.PERSONAL.name)
                            ) {
                                onSettingButtonClick()
                            } else {
                                selectIndex = screens.indexOf(screen)
                            }
                        },
                        label = {
                            Text(text = stringResource(id = screen.stringId!!))
                        },
                        icon = {
                            Icon(
                                painter = painterResource(
                                    id = if (selectIndex == screens.indexOf(screen)) {
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
        contentWindowInsets = WindowInsets(top = 0),
        snackbarHost = {
            SnackbarHost(hostState = snackStateHost) {
                Snackbar(snackbarData = it)
            }
        },
    ) { paddingValues ->
        SaveStateContentPager(
            contentPadding = paddingValues,
            savableStateHolder = savableStateHolder,
            currentPager = selectIndex
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
                                selectIndex = 1
                            }
                        },
                        onHotHeaderLineClick = onHotClick,
                        onNewestHeaderLineClick = onNewestHeaderLineClick,
                        onFinishHeaderLineClick = onFinishHeaderLineClick,
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

    BackHandler(enabled = selectIndex != 0) {
        selectIndex = 0
    }

}

