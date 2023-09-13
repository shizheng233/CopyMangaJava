package com.shicheeng.copymanga.ui.screen.compoents

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerScope
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.SaveableStateHolder
import androidx.compose.ui.Modifier

/**
 * 可以保存状态的[HorizontalPager]，实际上是一个封装。
 *
 * @param pageContent 内容
 * @param savableStateHolder 将状态提升到主界面
 * @see HorizontalPager
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SaveStatePager(
    modifier: Modifier = Modifier,
    pagerState: PagerState,
    contentPadding: PaddingValues,
    savableStateHolder: SaveableStateHolder,
    keys: (() -> List<Any>)? = null,
    pageContent: @Composable (PagerScope.(Int) -> Unit),
) {
    HorizontalPager(
        state = pagerState,
        modifier = modifier,
        contentPadding = contentPadding,
        userScrollEnabled = false
    ) {
        savableStateHolder.SaveableStateProvider(
            key = if (keys != null) keys()[it] else it,
            content = {
                pageContent(it)
            }
        )
    }
}