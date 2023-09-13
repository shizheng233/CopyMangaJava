package com.shicheeng.copymanga.ui.screen.topics

import android.app.Activity
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.shicheeng.copymanga.MainActivity
import com.shicheeng.copymanga.R
import com.shicheeng.copymanga.ui.screen.compoents.ErrorScreen
import com.shicheeng.copymanga.ui.screen.compoents.LoadingScreen
import com.shicheeng.copymanga.ui.screen.compoents.PlainButton
import com.shicheeng.copymanga.ui.screen.compoents.pagingLoadingIndication
import com.shicheeng.copymanga.util.UIState
import com.shicheeng.copymanga.util.copyComposable
import dagger.hilt.android.EntryPointAccessors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopicsScreen(
    pathWord: String?,
    type: Int?,
    topicViewModel: TopicViewModel = topicViewModel(pathWord = pathWord, type = type),
    onBack: () -> Unit,
    onItemClick: (pathWord: String) -> Unit,
) {
    val uiState by topicViewModel.uiState.collectAsState()
    val list = topicViewModel.list.collectAsLazyPagingItems()

    if (uiState == UIState.Loading) {
        LoadingScreen()
        return
    }
    if (uiState is UIState.Error<*>) {
        ErrorScreen(
            errorMessage = (uiState as UIState.Error<*>)
                .errorMessage
                .message
                ?: stringResource(id = R.string.error)
        ) {
            topicViewModel.retry()
        }
        return
    }
    val successUIState = uiState as UIState.Success
    val lazyListState = rememberLazyListState()

    Scaffold(
        topBar = {
            val firstVisibleItemIndex by remember {
                derivedStateOf { lazyListState.firstVisibleItemIndex }
            }
            val firstVisibleItemScrollOffset by remember {
                derivedStateOf { lazyListState.firstVisibleItemScrollOffset }
            }
            val animatedTitleAlpha by animateFloatAsState(
                if (firstVisibleItemIndex > 0) 1f else 0f, label = "animated_title_alpha",
            )
            val animatedBgAlpha by animateFloatAsState(
                if (firstVisibleItemIndex > 0 || firstVisibleItemScrollOffset > 0) 1f else 0f,
                label = "animated_background_alpha",
            )
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.topic_detail_text),
                        modifier = Modifier.alpha(animatedTitleAlpha)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
                        .copy(alpha = animatedBgAlpha)
                ),
                navigationIcon = {
                    PlainButton(
                        id = R.string.back_to_up,
                        drawableRes = R.drawable.ic_arrow_back,
                        onButtonClick = onBack
                    )
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            contentPadding = paddingValues.copyComposable(
                top = 0.dp
            ),
            state = lazyListState
        ) {
            item(
                key = TopicHeaderKeys.HEADER,
                contentType = TopicHeaderKeys.HEADER
            ) {
                TopicHeader(
                    title = successUIState.content.results.title,
                    coverUrl = successUIState.content.results.cover,
                    period = successUIState.content.results.period,
                    time = successUIState.content.results.journal,
                    createTime = successUIState.content.results.datetimeCreated
                )
            }
            item(
                key = TopicHeaderKeys.SUMMARY,
                contentType = TopicHeaderKeys.SUMMARY
            ) {
                Text(
                    text = successUIState.content.results.brief,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    textAlign = TextAlign.Start
                )
            }
            items(
                count = list.itemCount,
                contentType = list.itemContentType { it.pathWord },
                key = list.itemKey { it.pathWord }
            ) { index ->
                list[index]?.let { topicItem ->
                    TopicComicItem(topicItem = topicItem) {
                        onItemClick(it.pathWord)
                    }
                }
            }
            pagingLoadingIndication(
                loadState = list.loadState.append
            ) {
                list.retry()
            }
        }
    }
}


@Composable
private fun topicViewModel(pathWord: String?, type: Int?): TopicViewModel {
    val entryPoint = EntryPointAccessors
        .fromActivity<MainActivity.ViewModelAssistedFactoryProvider>(LocalContext.current as Activity)
    requireNotNull(pathWord) { "Cannot create viewModel, case of the path_word is null" }
    return viewModel(
        factory = TopicViewModel.inFactory(
            pathWord = pathWord,
            type = type ?: 1,
            factory = entryPoint.topicDetailViewModelFactory()
        )
    )
}