package com.shicheeng.copymanga.ui.screen.compoents

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.PullRefreshState
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun RefreshLayout(
    modifier: Modifier = Modifier,
    pullRefreshState: PullRefreshState,
    isRefreshing: Boolean,
    topPadding: Dp,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = modifier
            .pullRefresh(state = pullRefreshState)
            .fillMaxSize()
    ) {
        content()
        PullRefreshIndicator(
            refreshing = isRefreshing,
            state = pullRefreshState,
            contentColor = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .padding(top = topPadding)
                .align(Alignment.TopCenter)
        )
    }
}