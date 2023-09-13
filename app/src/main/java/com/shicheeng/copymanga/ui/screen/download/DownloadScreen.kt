package com.shicheeng.copymanga.ui.screen.download

import android.content.Intent
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import com.shicheeng.copymanga.R
import com.shicheeng.copymanga.server.DownloadService
import com.shicheeng.copymanga.ui.screen.compoents.PlainButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DownloadScreen(
    onNavigationClick: () -> Unit,
) {
    val localContext = LocalContext.current
    val lifecycle = LocalLifecycleOwner.current
    val connection = remember { DownloadConnection(lifecycle = lifecycle.lifecycle) }
    val download by connection.download.collectAsState()
    val connectionState by connection.connectionState.collectAsState()
    val topAppBarScrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    DisposableEffect(key1 = connection) {
        localContext.bindService(Intent(localContext, DownloadService::class.java), connection, 0)
        onDispose {
            localContext.unbindService(connection)
        }
    }
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
        if (connectionState) {
            LazyColumn(contentPadding = paddingValues) {
                items(download) { downloadJob ->
                    val downloadState by downloadJob.progressAsFlow().collectAsState(null)
                    downloadState?.let {
                        DownloadItem(downloadStateChapter = it)
                    }
                }
            }
        } else {
            EmptyScreen(paddingValues = paddingValues)
        }
    }
}


