package com.shicheeng.copymanga.ui.screen.setting.worker

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.asFlow
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.WorkQuery
import com.shicheeng.copymanga.R
import com.shicheeng.copymanga.ui.screen.compoents.PlainButton
import com.shicheeng.copymanga.ui.theme.MonospaceStyle
import com.shicheeng.copymanga.util.copyComposable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkerScreen(
    onBack: () -> Unit,
) {
    val context = LocalContext.current
    val workManager = WorkManager.getInstance(context)
    val successWork by workManager
        .getWorkInfosLiveData(WorkQuery.fromStates(WorkInfo.State.SUCCEEDED))
        .asFlow()
        .collectAsState(initial = emptyList())
    val enqueueWord by workManager
        .getWorkInfosLiveData(WorkQuery.fromStates(WorkInfo.State.ENQUEUED))
        .asFlow()
        .collectAsState(initial = emptyList())
    val runningWork by workManager
        .getWorkInfosLiveData(WorkQuery.fromStates(WorkInfo.State.RUNNING))
        .asFlow()
        .collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(id = R.string.work_information))
                },
                navigationIcon = {
                    PlainButton(
                        id = R.string.back_to_up,
                        drawableRes = R.drawable.ic_arrow_back,
                        onButtonClick = onBack
                    )
                }
            )
        },
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        LazyColumn(
            contentPadding = paddingValues.copyComposable(
                start = 16.dp,
                end = 16.dp,
                top = 16.dp + paddingValues.calculateTopPadding(),
                bottom = 16.dp + paddingValues.calculateBottomPadding()
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                Text(text = stringResource(R.string.successed))
            }
            items(successWork) {
                Text(
                    text = it.tags.joinToString(),
                    style = MonospaceStyle
                )
                Text(
                    text = it.id.toString(),
                    style = MonospaceStyle
                )
                Text(
                    text = it.state.name,
                    style = MonospaceStyle
                )
            }
            item {
                Text(text = stringResource(R.string.running))
            }
            items(runningWork) {
                Text(
                    text = it.tags.joinToString(),
                    style = MonospaceStyle
                )
                Text(
                    text = it.id.toString(),
                    style = MonospaceStyle
                )
                Text(
                    text = it.state.name,
                    style = MonospaceStyle
                )
            }
            item {
                Text(text = stringResource(R.string.enqueue))
            }
            items(enqueueWord) {
                Text(
                    text = it.tags.joinToString(),
                    style = MonospaceStyle
                )
                Text(
                    text = it.id.toString(),
                    style = MonospaceStyle
                )
                Text(
                    text = it.state.name,
                    style = MonospaceStyle
                )
            }
        }
    }

}