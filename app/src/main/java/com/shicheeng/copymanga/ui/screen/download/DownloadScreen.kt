package com.shicheeng.copymanga.ui.screen.download

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.work.WorkInfo
import com.shicheeng.copymanga.R
import com.shicheeng.copymanga.ui.screen.compoents.PlainButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DownloadScreen(
    viewModel: DownloadScreenViewModel = hiltViewModel(),
    onCardClick: (String) -> Unit,
    onNavigationClick: () -> Unit,
) {

    val topAppBarScrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val items by viewModel.items.collectAsState()

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = { Text(text = stringResource(id = R.string.download_manga)) },
                scrollBehavior = topAppBarScrollBehavior,
                navigationIcon = {
                    PlainButton(
                        id = R.string.back_to_up,
                        drawableRes = R.drawable.ic_arrow_back,
                        onButtonClick = onNavigationClick
                    )
                }
            )
        },
        modifier = Modifier.nestedScroll(topAppBarScrollBehavior.nestedScrollConnection)
    ) { paddingValues ->
        if (items != null) {
            LazyColumn(contentPadding = paddingValues) {
                items?.forEach { (t, u) ->
                    item {
                        Text(
                            text = stringResource(
                                id = when (t) {
                                    WorkInfo.State.ENQUEUED -> R.string.waiting
                                    WorkInfo.State.RUNNING -> R.string.downloading
                                    WorkInfo.State.SUCCEEDED -> R.string.completed
                                    WorkInfo.State.FAILED -> R.string.failure_download
                                    WorkInfo.State.BLOCKED -> R.string.prerequisites_miss
                                    WorkInfo.State.CANCELLED -> R.string.cancel
                                }
                            ),
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                    items(u) {
                        DownloadItem(
                            downloadUiDataModel = it,
                            onCancel = {
                                viewModel.cancel(it.id)
                            },
                            onCardClick = {
                                onCardClick(it.pathWord)
                            }
                        ) {
                            if (it.isPause) {
                                viewModel.resume(it.id)
                            } else {
                                viewModel.pause(it.id)
                            }
                        }
                    }
                }
            }
        }
    }
}


