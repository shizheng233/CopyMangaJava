package com.shicheeng.copymanga.ui.screen.compoents

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerScope
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.SaveableStateHolder
import androidx.compose.ui.Modifier
import soup.compose.material.motion.animation.materialFadeThroughIn
import soup.compose.material.motion.animation.materialFadeThroughOut

private const val TabFadeDuration = 200

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
            key = if (keys != null) keys()[it].hashCode() else it,
            content = {
                pageContent(it)
            }
        )
    }
}

/**
 * A content which can switchable and have animation named [materialFadeThroughIn] and [materialFadeThroughOut].
 *
 * @param contentPadding A [PaddingValues] that use in content.
 * @param currentPager A number which pager now showing.
 * @param savableStateHolder Provide a [SaveableStateHolder] that will use in this function.
 * @param keys Provide a key list. It will use [currentPager] if null.
 * @param pageContent Content showing on screen.
 */
@Composable
fun SaveStateContentPager(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues,
    currentPager: Int,
    savableStateHolder: SaveableStateHolder,
    keys: (() -> List<Any>)? = null,
    pageContent: @Composable (Int) -> Unit,
) {
    AnimatedContent(
        modifier = modifier.padding(contentPadding),
        targetState = currentPager,
        transitionSpec = {
            materialFadeThroughIn(
                initialScale = 1f,
                durationMillis = TabFadeDuration
            ) togetherWith materialFadeThroughOut(
                durationMillis = TabFadeDuration
            )
        },
        label = "pager_content_move_with_material_fade"
    ) {
        savableStateHolder.SaveableStateProvider(
            key = if (keys != null) keys()[it].hashCode() else it,
            content = {
                pageContent(it)
            }
        )
    }
}