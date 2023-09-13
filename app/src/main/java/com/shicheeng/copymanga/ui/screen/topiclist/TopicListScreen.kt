package com.shicheeng.copymanga.ui.screen.topiclist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.shicheeng.copymanga.R
import com.shicheeng.copymanga.data.topicalllist.TopicAllListItem
import com.shicheeng.copymanga.ui.screen.compoents.PlainButton
import com.shicheeng.copymanga.ui.screen.main.home.HomePageTopicCard
import com.shicheeng.copymanga.util.copyComposable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopicListScreen(
    viewModel: TopicListVIewModel = hiltViewModel(),
    onBack: () -> Unit,
    onTopicClick: (TopicAllListItem) -> Unit,
) {

    val topAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val list = viewModel.list.collectAsLazyPagingItems()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.topic)) },
                scrollBehavior = topAppBarScrollBehavior,
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
                start = 16.dp,
                end = 16.dp
            ),
            modifier = Modifier.nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(
                count = list.itemCount,
                key = list.itemKey { it.pathWord },
                contentType = list.itemContentType { it.pathWord }
            ) { index ->
                list[index]?.let {
                    HomePageTopicCard(
                        title = it.title,
                        supportedText = it.brief,
                        subText = it.period,
                        imageUrl = it.cover,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        onTopicClick(it)
                    }
                }
            }
        }
    }
}