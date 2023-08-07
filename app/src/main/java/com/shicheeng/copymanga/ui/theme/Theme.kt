package com.shicheeng.copymanga.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import com.google.accompanist.themeadapter.material3.createMdc3Theme

/**
 * 拷贝漫画的主题
 */
@Composable
fun CopyMangaTheme(
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current
    val layoutDirection = LocalLayoutDirection.current
    val (colorScheme, typography, shapes) = createMdc3Theme(
        context = context,
        layoutDirection = layoutDirection
    )

    MaterialTheme(
        content = content,
        colorScheme = colorScheme!!,
        typography = typography!!,
        shapes = shapes!!
    )
}