package com.shicheeng.copymanga.ui.screen.download

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.IBinder
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.coroutineScope
import com.shicheeng.copymanga.server.DownloadService
import com.shicheeng.copymanga.server.DownloadStateChapter
import com.shicheeng.copymanga.util.DownloadJob
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class DownloadConnection constructor(
    override val lifecycle: Lifecycle,
) : ServiceConnection, LifecycleOwner {

    private var binder: DownloadService.DownloadBinder? = null

    private val _download = MutableStateFlow<List<DownloadJob<DownloadStateChapter>>>(emptyList())
    val download get() = _download.asStateFlow()

    private val _connectionState = MutableStateFlow(false)
    val connectionState = _connectionState.asStateFlow()

    private var collectJob: Job? = null

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        binder = service as? DownloadService.DownloadBinder
        collectJob?.cancel()
        collectJob = lifecycle.coroutineScope.launch {
            binder?.downloads?.collectLatest {
                _download.tryEmit(it)
            }
        }
        _connectionState.tryEmit(true)
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        _connectionState.tryEmit(false)
        collectJob?.cancel()
        collectJob = null
    }

}