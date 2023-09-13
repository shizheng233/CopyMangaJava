package com.shicheeng.copymanga.ui.screen

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.navigation.NavHostController
import com.shicheeng.copymanga.R

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
    ) {
        fun NavHostController.toExplore(top: String?, theme: String?, order: String?) {
            navigate(name + "?theme=${theme}&top=${top}&order=${order}")
        }
    }

    object SUBSCRIBE : Router(name = "SUBSCRIBE")

    object HISTORY : Router(name = "HISTORY")

    object PERSONAL : Router(
        name = "PERSONAL",
        stringId = R.string.personal,
        drawableRes = R.drawable.ic_person_center,
        onClickIcon = R.drawable.baseline_person_24
    )

    object DOWNLOADED : Router(
        name = "DOWNLOADED"
    )

    object RECOMMEND : Router(name = "RECOMMEND")

    object NEWEST : Router(name = "NEWEST")

    object DETAIL : Router(name = "DETAIL") {
        const val deepLink = "shicheengcmdm://detail/{path_word}"
        const val copyMangaWebURl = "https://copymanga.site/h5/details/comic/{path_word}"
    }

    object SEARCH : Router(name = "SEARCH")

    object SearchResult : Router(name = "SearchResult")

    object SETTING : Router(name = "SETTING")

    object WORKER : Router(name = "WORKER")

    object DOWNLOAD : Router(name = "DOWNLOAD") {
        const val deepLink = "shicheengcmdm://download"
    }

    object ABOUT : Router(name = "ABOUT")

    object TOPICS : Router(name = "TOPIC")

    object TopicDETAIL : Router("TOPIC_DETAIL") {
        fun pathWord(pathWord: String, type: Int): String {
            return this.name + "/${pathWord}?type=$type"
        }
    }

    object LOGIN : Router("LOGIN")

    object LoginSelect : Router("LoginSelect")

    object WebSHELF : Router("WebSHELF")

    object UserShortDETAIL : Router("UserShortDETAIL")

    object AuthorsMANGA : Router("AuthorsMANGA")

    object COMMENT : Router("COMMENT") {
        private fun comicUUid(uuid: String): String {
            return this.name + "/$uuid"
        }

        infix fun NavHostController.toCommentScreen(uuid: String) {
            navigate(comicUUid(uuid))
        }
    }

}