package com.shicheeng.copymanga.ui.screen.comment

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.shicheeng.copymanga.R
import com.shicheeng.copymanga.ui.screen.compoents.EmptyDataScreen
import com.shicheeng.copymanga.ui.screen.compoents.PlainButton
import com.shicheeng.copymanga.ui.screen.compoents.pagingLoadingIndication
import com.shicheeng.copymanga.ui.screen.compoents.pullrefresh.SwipeRefresh
import com.shicheeng.copymanga.ui.screen.compoents.pullrefresh.rememberSwipeRefreshState
import com.shicheeng.copymanga.util.SendUIState
import com.shicheeng.copymanga.viewmodel.CommentViewModel

@OptIn(
    ExperimentalMaterial3Api::class
)
@Composable
fun CommentScreen(
    viewModel: CommentViewModel = hiltViewModel(),
    navClick: () -> Unit,
) {

    val list = viewModel.comments.collectAsLazyPagingItems()
    val pullRefreshState = rememberSwipeRefreshState(
        isRefreshing = list.loadState.refresh is LoadState.Loading,
    )
    val topAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val (sendContent, onSendContent) = rememberSaveable { mutableStateOf("") }
    val commentStatus by viewModel.commentPush.collectAsState()
    val isExpired by viewModel.loginIsExpired.collectAsState()

    LaunchedEffect(key1 = commentStatus) {
        if (commentStatus is SendUIState.Success) {
            list.refresh()
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.ime),
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.comic_comment_title)) },
                navigationIcon = {
                    PlainButton(
                        id = R.string.back_to_up,
                        drawableRes = R.drawable.ic_arrow_back,
                        onButtonClick = navClick
                    )
                },
                scrollBehavior = topAppBarScrollBehavior,
                modifier = Modifier,
                windowInsets = WindowInsets.statusBars
            )
        },
        bottomBar = {
            CommentSendBar(
                value = sendContent,
                onValueChange = onSendContent,
                sendUIState = commentStatus,
                modifier = Modifier,
                isExpired = isExpired
            ) {
                viewModel.sendComment(sendContent)
            }
        }
    ) { padding ->
        SwipeRefresh(
            state = pullRefreshState,
            onRefresh = {
                list.refresh()
            },
            indicatorPadding = padding,
        ) {
            EmptyDataScreen(
                isEmpty = list.itemSnapshotList.isEmpty(),
                modifier = Modifier
            ) {
                LazyColumn(
                    contentPadding = padding,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier
                        .fillMaxSize()
                        .nestedScroll(topAppBarScrollBehavior.nestedScrollConnection)
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