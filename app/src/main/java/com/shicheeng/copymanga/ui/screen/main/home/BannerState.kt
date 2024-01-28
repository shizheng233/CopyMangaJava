package com.shicheeng.copymanga.ui.screen.main.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import java.lang.Math.floorMod

@OptIn(ExperimentalFoundationApi::class)
class BannerState(
    initialActiveItemIndex: Int = 0,
    updatedPageCount: () -> Int,
) : PagerState() {
    internal var activePauseHandlesCount by mutableIntStateOf(0)

    /**
     * The index of the item that is currently displayed by the carousel
     */
    var activeItemIndex by mutableIntStateOf(initialActiveItemIndex)
        internal set

    var pagerCountState = mutableStateOf(updatedPageCount)

    internal var isMovingBackward = false
        private set

    override val pageCount: Int
        get() = pagerCountState.value.invoke()

    fun moveToPreviousItem(itemCount: Int) {
        // No items available for carousel
        if (itemCount == 0) return

        isMovingBackward = true

        // Go to previous item
        activeItemIndex = floorMod(activeItemIndex - 1, itemCount)
    }

    internal suspend fun moveToNextItem(itemCount: Int) {
        // No items available for carousel
        if (itemCount == 0) return

        isMovingBackward = false

        // Go to next item
        activeItemIndex = floorMod(activeItemIndex + 1, itemCount)
        animateScrollToPage(activeItemIndex)
    }

    companion object {
        /**
         * The default [Saver] implementation for [Banner].
         */
        val Saver: Saver<BannerState, *> = Saver(
            save = { it.activeItemIndex }
        ) { BannerState { it } }
    }
}

@Composable
fun rememberBannerState(
    initialCount: Int,
    pagerCount: () -> Int,
) = rememberSaveable(saver = BannerState.Saver) {
    BannerState(
        updatedPageCount = pagerCount,
        initialActiveItemIndex = initialCount
    ).apply {
        pagerCountState.value = pagerCount
    }
}