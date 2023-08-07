package com.shicheeng.copymanga.ui.screen.main.history

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.shicheeng.copymanga.R
import com.shicheeng.copymanga.ui.screen.compoents.PlainButton
import com.shicheeng.copymanga.ui.screen.download.EmptyScreen
import com.shicheeng.copymanga.util.convertToTimeGroup
import com.shicheeng.copymanga.viewmodel.HistoryViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HistoryScreen(
    historyViewModel: HistoryViewModel = hiltViewModel(),
    onDownloadedBtnClick: () -> Unit,
    onPathWord: (String) -> Unit,
) {

    val historyList by historyViewModel.historyList.collectAsState()
    val historyGrouped = historyList
        .filter { it.positionChapter != 0 || it.positionPage != 0 }
        .groupBy { it.time.convertToTimeGroup() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.history)) },
                actions = {
                    PlainButton(
                        id = R.string.download_manga,
                        drawableRes = R.drawable.outline_download_24,
                        onButtonClick = onDownloadedBtnClick
                    )
                }
            )
        }
    ) { paddingValues ->
        if (historyList.isEmpty()) {
            EmptyScreen(paddingValues = paddingValues, id = R.string.no_history)
        } else {
            LazyColumn(
                contentPadding = paddingValues,
            ) {
                historyGrouped.forEach {
                    if (it.value.isNotEmpty()) {
                        stickyHeader {
                            Text(
                                text = it.key,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                            )
                        }
                    }
                    items(it.value) { historyItem ->
                        HistoryItem(data = historyItem) {
                            onPathWord(historyItem.pathWord)
                        }
                    }
                }
            }
        }
    }
}