package com.shicheeng.copymanga.ui.screen.comment

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.shicheeng.copymanga.R
import com.shicheeng.copymanga.ui.screen.compoents.EmptyDataScreen
import com.shicheeng.copymanga.ui.screen.compoents.PlainButton
import com.shicheeng.copymanga.ui.screen.compoents.RefreshLayout
import com.shicheeng.copymanga.ui.screen.compoents.pagingLoadingIndication
import com.shicheeng.copymanga.viewmodel.CommentViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun CommentScreen(
    viewModel: CommentViewModel = hiltViewModel(),
    navClick: () -> Unit,
) {

    val list = viewModel.comments.collectAsLazyPagingItems()
    val pullRefreshState = rememberPullRefreshState(
        refreshing = list.loadState.refresh is LoadState.Loading,
        onRefresh = list::refresh
    )
    val topAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "漫画评论") },
                navigationIcon = {
                    PlainButton(
                        id = R.string.back_to_up,
                        drawableRes = R.drawable.ic_arrow_back,
                        onButtonClick = navClick
                    )
                },
                scrollBehavior = topAppBarScrollBehavior
            )
        }
    ) { paddingValues ->
        RefreshLayout(
            pullRefreshState = pullRefreshState,
            isRefreshing = list.loadState.refresh is LoadState.Loading,
            topPadding = paddingValues.calculateTopPadding()
        ) {
            EmptyDataScreen(
                isEmpty = list.itemSnapshotList.isEmpty()
            ) {
                LazyColumn(
                    contentPadding = paddingValues,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.nestedScroll(topAppBarScrollBehavior.nestedScrollConnection)
                ) {
                    items(list.itemCount) {
                        list[it]?.let { commentItem ->
                            CommentItem(commentListItem = commentItem) {

                            }
                        }
                    }
                    pagingLoadingIndication(
                        loadState = list.loadState.append,
                        onTry = list::retry
                    )
                }
            }
        }
    }

}