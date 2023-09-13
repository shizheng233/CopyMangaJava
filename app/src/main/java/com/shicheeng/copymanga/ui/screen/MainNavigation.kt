package com.shicheeng.copymanga.ui.screen

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavDeepLink
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.shicheeng.copymanga.ui.screen.Router.COMMENT.toCommentScreen
import com.shicheeng.copymanga.ui.screen.Router.EXPLORE.toExplore
import com.shicheeng.copymanga.ui.screen.authorsmanga.AuthorsMangaScreen
import com.shicheeng.copymanga.ui.screen.comment.CommentScreen
import com.shicheeng.copymanga.ui.screen.download.DownloadScreen
import com.shicheeng.copymanga.ui.screen.downloaded.DownloadedScreen
import com.shicheeng.copymanga.ui.screen.history.HistoryScreen
import com.shicheeng.copymanga.ui.screen.list.NewestScreen
import com.shicheeng.copymanga.ui.screen.list.RecommendScreen
import com.shicheeng.copymanga.ui.screen.login.LoginScreen
import com.shicheeng.copymanga.ui.screen.login.loginlist.LoginPersonalListScreen
import com.shicheeng.copymanga.ui.screen.main.MainScreen
import com.shicheeng.copymanga.ui.screen.main.explore.ExploreScreen
import com.shicheeng.copymanga.ui.screen.main.home.search.SearchScreen
import com.shicheeng.copymanga.ui.screen.main.personal.personaldetail.PersonalDetail
import com.shicheeng.copymanga.ui.screen.main.subscribe.SubScribeScreen
import com.shicheeng.copymanga.ui.screen.manga.MangaDetailScreen
import com.shicheeng.copymanga.ui.screen.search.SearchResultScreen
import com.shicheeng.copymanga.ui.screen.setting.SettingScreen
import com.shicheeng.copymanga.ui.screen.setting.about.AboutScreen
import com.shicheeng.copymanga.ui.screen.setting.worker.WorkerScreen
import com.shicheeng.copymanga.ui.screen.topiclist.TopicListScreen
import com.shicheeng.copymanga.ui.screen.topics.TopicsScreen
import com.shicheeng.copymanga.ui.screen.webshelf.WebShelfScreen
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
                },
                onTopicHeaderLineClick = {
                    navController.navigate(Router.TOPICS.name)
                },
                onNewestHeaderLineClick = {
                    navController.navigate(Router.NEWEST.name)
                },
                onTopicClick = { pathWord, type ->
                    navController.navigate(Router.TopicDETAIL.pathWord(pathWord, type))
                },
                onSubscribedClick = {
                    navController.navigate(Router.SUBSCRIBE.name)
                },
                onHistoryClick = {
                    navController.navigate(Router.HISTORY.name)
                },
                onLibraryClick = {
                    navController.navigate(Router.WebSHELF.name)
                },
                onPersonalHeaderClick = { login ->
                    if (login) {
                        navController.navigate(Router.UserShortDETAIL.name)
                    } else {
                        navController.navigate(Router.LOGIN.name)
                    }
                },
                onFinishHeaderLineClick = {
                    navController.toExplore(
                        theme = null,
                        top = "finish",
                        order = null,
                    )
                },
                onHotClick = {
                    navController.toExplore(
                        theme = null,
                        top = null,
                        order = "-popular",
                    )
                }
            )
        }

        composable(
            route = "${Router.EXPLORE.name}?theme={theme}&top={top}&order={order}",
            arguments = listOf(
                navArgument(name = "theme") { nullable = true },
                navArgument(name = "top") { nullable = true },
                navArgument(name = "order") { nullable = true }
            )
        ) { backStackEntry ->
            ExploreScreen(
                top = backStackEntry.arguments?.getString("top"),
                theme = backStackEntry.arguments?.getString("theme"),
                order = backStackEntry.arguments?.getString("order"),
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
            deepLinks = listOf(
                navDeepLink { uriPattern = Router.DETAIL.deepLink },
                navDeepLink { uriPattern = Router.DETAIL.copyMangaWebURl }
            )
        ) { backStackEntry ->
            val pathWord = backStackEntry.arguments?.getString("path_word")
            MangaDetailScreen(
                pathWord = pathWord,
                onTagsClick = {
                    navController.toExplore(
                        top = null,
                        order = null,
                        theme = it.pathWord
                    )
                },
                onAuthorClick = {
                    navController.navigate("${Router.AuthorsMANGA.name}/${it}")
                },
                onCommentClick = {
                    navController toCommentScreen it
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
                },
                onUserClick = {
                    navController.navigate(Router.LoginSelect.name)
                }
            ) {
                navController.navigate(Router.ABOUT.name)
            }
        }

        composable(
            route = Router.DOWNLOAD.name,
            deepLinks = listOf(
                NavDeepLink(uri = Router.DOWNLOAD.deepLink)
            )
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

        composable(route = Router.HISTORY.name) {
            HistoryScreen(
                navigationClick = {
                    navController.popBackStack()
                },
                onRequestLogin = {
                    navController.navigate(Router.LOGIN.name)
                }
            ) { pathWord ->
                navController.navigate("${Router.DETAIL.name}/$pathWord")
            }
        }

        composable(route = Router.SUBSCRIBE.name) {
            SubScribeScreen(
                navClick = {
                    navController.popBackStack()
                }
            ) { pathWord ->
                navController.navigate("${Router.DETAIL.name}/$pathWord")
            }
        }

        composable(
            route = "${Router.TopicDETAIL.name}/{pathWord}?type={type}",
            arguments = listOf(
                navArgument(name = "pathWord") { type = NavType.StringType },
                navArgument(name = "type") { type = NavType.IntType }
            )
        ) { navBackStackEntry ->
            val pathWord = navBackStackEntry.arguments?.getString("pathWord")
            val type = navBackStackEntry.arguments?.getInt("type")
            TopicsScreen(
                pathWord = pathWord,
                type = type,
                onBack = {
                    navController.popBackStack()
                }
            ) {
                navController.navigate("${Router.DETAIL.name}/${it}")
            }
        }

        composable(
            route = Router.TOPICS.name,
        ) {
            TopicListScreen(
                onBack = { navController.popBackStack() }
            ) {
                navController.navigate(Router.TopicDETAIL.pathWord(it.pathWord, it.type))
            }
        }

        composable(route = Router.LOGIN.name) {
            LoginScreen(
                onNavClick = {
                    navController.popBackStack()
                }
            ) {
                navController.popBackStack()
            }
        }

        composable(route = Router.LoginSelect.name) {
            LoginPersonalListScreen(
                onAddClicked = {
                    navController.navigate(Router.LOGIN.name)
                }
            ) {
                navController.popBackStack()
            }
        }

        composable(route = Router.WebSHELF.name) {
            WebShelfScreen(
                navClick = {
                    navController.popBackStack()
                },
                reLoginClick = {
                    navController.navigate(Router.LOGIN.name)
                }
            ) {
                navController.navigate("${Router.DETAIL.name}/${it}")
            }
        }

        composable(route = Router.UserShortDETAIL.name) {
            PersonalDetail(
                onReLogin = {
                    navController.navigate(Router.LOGIN.name)
                }
            ) {
                navController.popBackStack()
            }
        }

        composable(
            route = Router.AuthorsMANGA.name + "/{author_path_word}",
            arguments = listOf(
                navArgument(name = "author_path_word") {
                    nullable = true
                    type = NavType.StringType
                }
            )
        ) {
            AuthorsMangaScreen(
                onNav = {
                    navController.popBackStack()
                }
            ) {
                navController.navigate("${Router.DETAIL.name}/${it}")
            }
        }

        composable(route = Router.COMMENT.name + "/{uuid_comic}") {
            CommentScreen(
                navClick = navController::popBackStack
            )
        }

    }


}

