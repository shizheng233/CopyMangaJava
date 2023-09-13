package com.shicheeng.copymanga.ui.screen.history.web

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.shicheeng.copymanga.R
import com.shicheeng.copymanga.ui.screen.compoents.ErrorScreen
import com.shicheeng.copymanga.ui.screen.compoents.LoadingScreen
import com.shicheeng.copymanga.ui.screen.compoents.RefreshLayout
import com.shicheeng.copymanga.ui.screen.history.local.HistoryItemCloud
import retrofit2.HttpException

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun WebHistoryScreen(
    viewModel: WebHistoryViewModel = hiltViewModel(),
    bottomPadding: Dp,
    onPathWord: (String) -> Unit,
    onRequestLogin: () -> Unit,
) {
    val list = viewModel.list.collectAsLazyPagingItems()
    val refreshState = rememberPullRefreshState(
        refreshing = list.loadState.refresh is LoadState.Loading,
        onRefresh = {
            list.refresh()
        }
    )

    if (list.loadState.refresh is LoadState.Loading) {
        LoadingScreen()
        return
    }

    if (list.loadState.refresh is LoadState.Error) {
        val error = (list.loadState.refresh as LoadState.Error).error
        ErrorScreen(
            errorMessage = error.message ?: "",
            onTry = { list.refresh() },
            needSecondaryText = error is HttpException && error.code() == 401,
            secondaryText = stringResource(id = R.string.re_login),
            onSecondaryClick = onRequestLogin
        )
        return
    }

    RefreshLayout(
        pullRefreshState = refreshState,
        isRefreshing = list.loadState.refresh is LoadState.Loading,
        topPadding = 0.dp
    ) {
        LazyColumn(
            contentPadding = PaddingValues(bottom = bottomPadding, top = 16.dp)
        ) {
            items(list.itemCount) { index ->
                list[index]?.let { item ->
                    HistoryItemCloud(data = item) {
                        onPathWord(item.comic.pathWord)
                    }
                }
            }
        }
    }
}
