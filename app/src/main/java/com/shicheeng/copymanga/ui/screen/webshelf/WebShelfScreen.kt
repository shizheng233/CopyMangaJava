package com.shicheeng.copymanga.ui.screen.webshelf

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.shicheeng.copymanga.R
import com.shicheeng.copymanga.ui.screen.compoents.ErrorScreen
import com.shicheeng.copymanga.ui.screen.compoents.LoadingScreen
import com.shicheeng.copymanga.ui.screen.compoents.PlainButton
import com.shicheeng.copymanga.ui.screen.compoents.RefreshLayout
import com.shicheeng.copymanga.ui.screen.compoents.pagingLoadingIndication
import com.shicheeng.copymanga.ui.screen.list.CommonListItem
import com.shicheeng.copymanga.util.copyComposable
import com.shicheeng.copymanga.viewmodel.WebShelfViewModel
import retrofit2.HttpException

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun WebShelfScreen(
    webShelfViewModel: WebShelfViewModel = hiltViewModel(),
    navClick: () -> Unit,
    reLoginClick: () -> Unit,
    onPathWord: (String) -> Unit,
) {
    val data = webShelfViewModel.data.collectAsLazyPagingItems()
    val topAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val pullRefreshState = rememberPullRefreshState(
        refreshing = data.loadState.refresh is LoadState.Loading,
        onRefresh = data::refresh
    )


    if (data.loadState.refresh is LoadState.Loading) {
        LoadingScreen()
        return
    }

    if (data.loadState.refresh is LoadState.Error) {
        val error = (data.loadState.refresh as LoadState.Error).error
        ErrorScreen(
            errorMessage = error.message ?: "",
            onTry = data::refresh,
            needSecondaryText = error is HttpException && error.code() == 401,
            secondaryText = stringResource(id = R.string.re_login),
            onSecondaryClick = reLoginClick
        )
        return
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(id = R.string.shelf_cloud))
                },
                scrollBehavior = topAppBarScrollBehavior,
                navigationIcon = {
                    PlainButton(
                        id = R.string.back_to_up,
                        drawableRes = R.drawable.ic_arrow_back,
                        onButtonClick = navClick
                    )
                }
            )
        }
    ) { paddingValues ->

        RefreshLayout(
            pullRefreshState = pullRefreshState,
            isRefreshing = data.loadState.refresh is LoadState.Loading,
            topPadding = paddingValues.calculateTopPadding()
        ) {
            LazyVerticalGrid(
                contentPadding = paddingValues.copyComposable(
                    start = 16.dp,
                    end = 16.dp
                ),
                columns = GridCells.Fixed(3),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.nestedScroll(topAppBarScrollBehavior.nestedScrollConnection)
            ) {
                items(data.itemCount) { index ->
                    data[index]?.let { item ->
                        CommonListItem(
                            url = item.comic.cover,
                            title = item.comic.name,
                            author = item.comic.author.joinToString { it.name }
                        ) {
                            onPathWord(item.comic.pathWord)
                        }
                    }
                }
                pagingLoadingIndication(
                    loadState = data.loadState.refresh,
                    onTry = data::retry
                )
            }
        }
    }
}