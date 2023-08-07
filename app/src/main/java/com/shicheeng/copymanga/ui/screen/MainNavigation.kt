package com.shicheeng.copymanga.ui.screen

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavDeepLink
import androidx.navigation.NavHostController
import androidx.navigation.navArgument
import com.shicheeng.copymanga.R
import com.shicheeng.copymanga.ui.screen.download.DownloadScreen
import com.shicheeng.copymanga.ui.screen.downloaded.DownloadedScreen
import com.shicheeng.copymanga.ui.screen.list.NewestScreen
import com.shicheeng.copymanga.ui.screen.list.RecommendScreen
import com.shicheeng.copymanga.ui.screen.main.MainScreen
import com.shicheeng.copymanga.ui.screen.main.explore.ExploreScreen
import com.shicheeng.copymanga.ui.screen.main.home.search.SearchScreen
import com.shicheeng.copymanga.ui.screen.manga.MangaDetailScreen
import com.shicheeng.copymanga.ui.screen.search.SearchResultScreen
import com.shicheeng.copymanga.ui.screen.setting.SettingScreen
import com.shicheeng.copymanga.ui.screen.setting.about.AboutScreen
import com.shicheeng.copymanga.ui.screen.setting.worker.WorkerScreen
import soup.compose.material.motion.animation.materialSharedAxisXIn
import soup.compose.material.motion.animation.materialSharedAxisXOut
import soup.compose.material.motion.animation.rememberSlideDistance
import soup.compose.material.motion.navigation.MaterialMotionNavHost
import soup.compose.material.motion.navigation.composable


@OptIn(ExperimentalAnimationApi::class)
@Composable
fun MainComposeNavigation(
    navController: NavHostController,
) {
    val slide = rememberSlideDistance()
    MaterialMotionNavHost(
        navController = navController,
        startDestination = Router.MAIN.name,
        enterTransition = {
            materialSharedAxisXIn(
                forward = true,
                slideDistance = slide
            )
        },
        exitTransition = {
            materialSharedAxisXOut(
                forward = true,
                slideDistance = slide
            )
        },
        popEnterTransition = {
            materialSharedAxisXIn(
                forward = false,
                slideDistance = slide
            )
        },
        popExitTransition = {
            materialSharedAxisXOut(
                forward = false,
                slideDistance = slide
            )
        }
    ) {
        composable(
            route = Router.MAIN.name,
        ) {
            MainScreen(
                onUUid = { navController.navigate("${Router.DETAIL.name}/$it") },
                onSearchButtonClick = {
                    navController.navigate(Router.SEARCH.name)
                },
                onSettingButtonClick = { navController.navigate(Router.SETTING.name) },
                onRecommendHeaderLineClick = {
                    navController.navigate(Router.RECOMMEND.name)
                },
                onDownloadedBtnClick = {
                    navController.navigate(Router.DOWNLOADED.name)
                }
            ) {
                navController.navigate(Router.NEWEST.name)
            }
        }

        composable(
            route = "${Router.EXPLORE.name}?theme={theme}",
            arguments = listOf(
                navArgument(name = "theme") { nullable = true }
            )
        ) { backStackEntry ->
            ExploreScreen(
                top = null,
                theme = backStackEntry.arguments?.getString("theme"),
                order = null,
                onNavigationIconClick = {
                    navController.popBackStack()
                }
            ) {
                navController.navigate("${Router.DETAIL.name}/${it.pathWord}")
            }
        }

        composable(route = Router.RECOMMEND.name) {
            RecommendScreen(
                onBack = {
                    navController.popBackStack()
                }
            ) {
                navController.navigate("${Router.DETAIL.name}/$it")
            }
        }

        composable(route = Router.NEWEST.name) {
            NewestScreen(
                onBack = {
                    navController.popBackStack()
                }
            ) {
                navController.navigate("${Router.DETAIL.name}/$it")
            }
        }

        composable(route = Router.SEARCH.name) {
            SearchScreen(
                onSearch = {
                    if (it.isNotEmpty() && it.isNotBlank()) {
                        navController.navigate("${Router.SearchResult.name}/$it")
                    }
                }
            ) {
                navController.popBackStack()
            }
        }

        composable(
            route = "${Router.SearchResult.name}/{searchWord}"
        ) { navBackStackEntry ->
            val word = navBackStackEntry.arguments?.getString("searchWord")
            SearchResultScreen(
                searchWord = word,
                onNavigation = {
                    navController.popBackStack()
                },
                onItemClick = {
                    navController.navigate("${Router.DETAIL.name}/${it.pathWord}")
                }
            )
        }

        composable(
            route = "${Router.DETAIL.name}/{path_word}",
        ) { backStackEntry ->
            val pathWord = backStackEntry.arguments?.getString("path_word")
            MangaDetailScreen(
                pathWord = pathWord,
                onTagsClick = {
                    navController.navigate("${Router.EXPLORE.name}?theme=${it.pathWord}")
                }
            ) {
                navController.popBackStack()
            }
        }

        composable(
            route = Router.SETTING.name
        ) {
            SettingScreen(
                onNavigateClick = {
                    navController.popBackStack()
                },
                onDownloadClick = {
                    navController.navigate(Router.DOWNLOAD.name)
                },
                onWorkerClick = {
                    navController.navigate(Router.WORKER.name)
                }
            ) {
                navController.navigate(Router.ABOUT.name)
            }
        }

        composable(
            route = Router.DOWNLOAD.name,
            deepLinks = listOf(NavDeepLink(uri = Router.DOWNLOAD.deepLink))
        ) {
            DownloadScreen {
                navController.popBackStack()
            }
        }

        composable(
            route = Router.ABOUT.name
        ) {
            AboutScreen {
                navController.popBackStack()
            }
        }

        composable(
            route = Router.WORKER.name
        ) {
            WorkerScreen {
                navController.popBackStack()
            }
        }

        composable(route = Router.DOWNLOADED.name) {
            DownloadedScreen(
                onNavigate = {
                    navController.popBackStack()
                }
            ) { pathWord ->
                if (pathWord != null) {
                    navController.navigate("${Router.DETAIL.name}/$pathWord")
                }
            }
        }

    }


}

/**
 * 导航路由
 * @param name 必传参数，名字。
 * @param stringId 非必传参数，适用于导航栏的字串符资源ID。
 * @param drawableRes 非必传参数，适用于导航栏的图标资源ID。
 * @param onClickIcon 非必传参数，但是如果传入[drawableRes]则必传，否则报错。在导航栏按钮被按下时显示的图标。
 */
sealed class Router(
    val name: String,
    @StringRes val stringId: Int? = null,
    @DrawableRes val drawableRes: Int? = null,
    @DrawableRes val onClickIcon: Int? = null,
) {

    object MAIN : Router(
        name = "MAIN"
    )

    object HOME : Router(
        name = "HOME",
        stringId = R.string.home_des,
        drawableRes = R.drawable.outline_home_24,
        onClickIcon = R.drawable.ic_baseline_home_24
    )

    object LEADERBOARD : Router(
        name = "LEADERBOARD",
        stringId = R.string.comic_rank,
        drawableRes = R.drawable.baseline_insert_chart_outlined_24,
        onClickIcon = R.drawable.baseline_insert_chart_24
    )

    object EXPLORE : Router(
        name = "EXPLORE",
        stringId = R.string.explore,
        drawableRes = R.drawable.ic_explore_outline,
        onClickIcon = R.drawable.baseline_explore_24
    )

    object SUBSCRIBE : Router(
        name = "SUBSCRIBE",
        stringId = R.string.subscribe,
        drawableRes = R.drawable.baseline_rss_feed_24,
        onClickIcon = R.drawable.baseline_rss_feed_24
    )

    object HISTORY : Router(
        name = "HISTORY",
        stringId = R.string.history,
        drawableRes = R.drawable.baseline_history_24,
        onClickIcon = R.drawable.baseline_history_24
    )

    object DOWNLOADED : Router(
        name = "DOWNLOADED"
    )

    object RECOMMEND : Router(name = "RECOMMEND")

    object NEWEST : Router(name = "NEWEST")

    object DETAIL : Router(name = "DETAIL")

    object SEARCH : Router(name = "SEARCH")

    object SearchResult : Router(name = "SearchResult")

    object SETTING : Router(name = "SETTING")

    object WORKER : Router(name = "WORKER")

    object DOWNLOAD : Router(name = "DOWNLOAD") {
        const val deepLink = "shicheengcmdm://download"
    }

    object ABOUT : Router(name = "ABOUT")


}