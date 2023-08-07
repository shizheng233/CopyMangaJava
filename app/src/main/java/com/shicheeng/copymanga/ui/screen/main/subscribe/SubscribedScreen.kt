package com.shicheeng.copymanga.ui.screen.main.subscribe

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.shicheeng.copymanga.R
import com.shicheeng.copymanga.server.work.DetectMangaUpdateWork
import com.shicheeng.copymanga.ui.screen.compoents.PlainButton
import com.shicheeng.copymanga.ui.screen.list.CommonListItem
import com.shicheeng.copymanga.util.copyComposable
import com.shicheeng.copymanga.viewmodel.SubscribedViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubScribeScreen(
    viewModel: SubscribedViewModel = hiltViewModel(),
    onPathWord: (String) -> Unit,
) {
    val data by viewModel.data.collectAsState()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(id = R.string.subscribe))
                },
                actions = {
                    PlainButton(
                        id = R.string.refresh,
                        drawableRes = R.drawable.ic_baseline_loop
                    ) {
                        val workManager = WorkManager.getInstance(context)
                        val oneTimeWorkRequest = OneTimeWorkRequestBuilder<DetectMangaUpdateWork>()
                            .build()
                        workManager.enqueue(oneTimeWorkRequest)
                    }
                }
            )
        }
    ) {
        LazyVerticalGrid(
            contentPadding = it.copyComposable(
                start = 16.dp,
                end = 16.dp,
                top = it.calculateTopPadding() + 16.dp,
                bottom = it.calculateBottomPadding() + 16.dp
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            columns = GridCells.Fixed(3)
        ) {
            items(data) { historyItem ->
                CommonListItem(
                    url = historyItem.url,
                    title = historyItem.name,
                    author = historyItem.authorList
                ) {
                    onPathWord(historyItem.pathWord)
                }
            }
        }
    }

}