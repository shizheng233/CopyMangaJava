package com.shicheeng.copymanga.ui.screen.history.local

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.shicheeng.copymanga.R
import com.shicheeng.copymanga.ui.screen.download.EmptyScreen
import com.shicheeng.copymanga.util.convertToTimeGroup
import com.shicheeng.copymanga.viewmodel.HistoryViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LocalHistoryScreen(
    historyViewModel: HistoryViewModel = hiltViewModel(),
    bottomPadding: Dp,
    onPathWord: (String) -> Unit,
) {

    val historyList by historyViewModel.historyList.collectAsState()
    val historyGrouped = historyList
        .filter { it.positionChapter != 0 || it.positionPage != 0 }
        .groupBy { it.time.convertToTimeGroup() }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        if (historyList.isEmpty()) {
            EmptyScreen(id = R.string.no_history)
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = bottomPadding, top = 16.dp)
            ) {
                historyGrouped.forEach { stringListEntry ->
                    if (stringListEntry.value.isNotEmpty()) {
                        item(
                            key = stringListEntry.hashCode(),
                            contentType = "HEADER"
                        ) {
                            Text(
                                text = stringListEntry.key,
                                modifier = Modifier
                                    .padding(horizontal = 16.dp, vertical = 4.dp)
                                    .animateItemPlacement()
                            )
                        }
                    }
                    items(
                        stringListEntry.value,
                        key = { "history-${it.hashCode()}" },
                        contentType = {
                            "item"
                        }
                    ) { historyItem ->
                        HistoryItem(
                            data = historyItem,
                            onClick = {
                                onPathWord(historyItem.pathWord)
                            },
                            modifier = Modifier.animateItemPlacement()
                        ) {
                            historyViewModel.deleteHistory(historyItem)
                        }
                    }
                }
            }
        }
    }
}