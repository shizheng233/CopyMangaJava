package com.shicheeng.copymanga.ui.screen.setting.about

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember


@Composable
fun rememberThankfulApps() = remember {
    listOf(
        AboutDataUiModel(
            name = "Tachiyomi",
            url = "tachiyomi.org",
            description = "Free and open source manga reader for Android.",
            author = "tachiyomiorg",
            license = ApacheLicense,
            iconUrl = "https://tachiyomi.org/img/logo-128px.png"
        ),
        AboutDataUiModel(
            name = "Kotatsu",
            description = "Manga reader for Android",
            author = "KotatsuApp",
            license = GPLv3License,
            url = "https://kotatsu.app/",
            iconUrl = "https://kotatsu.app/logo-compact.svg"
        ),
        AboutDataUiModel(
            name = "copymanga 拷贝漫画",
            description = "拷贝漫画的第三方APP，优化阅读/下载体验",
            author = "fumiama",
            license = GPLv3License,
            url = "https://github.com/fumiama/copymanga",
            iconUrl = "https://raw.githubusercontent.com/fumiama/copymanga/main/app/src/main/res/mipmap-xxxhdpi/ic_launcher.png"
        ),
        AboutDataUiModel(
            name = "copymanga-downloader",
            description = "使用python编译exe/bash/命令行参数来下载copymanga(拷贝漫画)中的漫画，支持批量+选话下载和获取您收藏的漫画并下载！(windows&linux支持，MacOS代码支持)",
            url = "https://github.com/misaka10843/copymanga-downloader",
            author = "misaka10843",
            license = GPLv3License
        )
    )
}

data class AboutDataUiModel(
    val name: String,
    val url: String,
    val iconUrl: String? = null,
    val description: String,
    val author: String,
    val license: String,
)

private const val GPLv3License = "GNU General Public License v3.0"
private const val ApacheLicense = "Apache License 2.0"