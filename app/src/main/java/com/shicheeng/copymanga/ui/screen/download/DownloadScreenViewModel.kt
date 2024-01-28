package com.shicheeng.copymanga.ui.screen.download

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.Data
import androidx.work.WorkInfo
import com.shicheeng.copymanga.data.downloadmodel.DownloadUiDataModel
import com.shicheeng.copymanga.data.local.LocalSavableMangaModel
import com.shicheeng.copymanga.resposity.MangaHistoryRepository
import com.shicheeng.copymanga.server.download.domin.DownloadState
import com.shicheeng.copymanga.server.download.woker.DownloadedWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class DownloadScreenViewModel @Inject constructor(
    private val downloaderWorker: DownloadedWorker.Caller,
    private val mangaHistoryRepository: MangaHistoryRepository,
) : ViewModel() {

    private val mangaCache = LinkedHashMap<String, LocalSavableMangaModel>()
    private val mutexCache = Mutex()

    @OptIn(ExperimentalCoroutinesApi::class)
    private val workerData = downloaderWorker.observerWorker()
        .mapLatest {
            it.mapToUiDataModel()
        }.stateIn(
            scope = viewModelScope + Dispatchers.Default,
            initialValue = null,
            started = SharingStarted.Eagerly
        )

    val items = workerData.map { dataModels ->
        dataModels?.groupBy { it.workerState }
    }.stateIn(
        scope = viewModelScope,
        initialValue = emptyMap(),
        started = SharingStarted.Eagerly
    )

    fun resume(id: UUID) {
        val snapshot = workerData.value ?: return
        for (work in snapshot) {
            if (id == work.id) {
                downloaderWorker.resume(id)
            }
        }
    }

    fun cancel(id: UUID) = viewModelScope.launch {
        val snapshot = workerData.value ?: return@launch
        for (work in snapshot) {
            if (id == work.id) {
                downloaderWorker.cancel(id)
            }
        }
    }

    fun pause(id: UUID) {
        val snapshot = workerData.value ?: return
        for (work in snapshot) {
            if (id == work.id) {
                downloaderWorker.pause(id)
            }
        }
    }

    private suspend fun List<WorkInfo>.mapToUiDataModel(): List<DownloadUiDataModel> {
        if (isEmpty()) {
            return emptyList()
        }
        val list = mapNotNullTo(ArrayList(size)) { it.toWorkUiDataModel() }
        list.sortedByDescending { it.timeStamp }
        return list
    }

    private suspend fun WorkInfo.toWorkUiDataModel(): DownloadUiDataModel? {
        val data = if (outputData == Data.EMPTY) progress else outputData
        val pathWord = (DownloadState getMangaPathWord data) ?: return null
        val manga = getManga(pathWord) ?: return null
        return DownloadUiDataModel(
            pathWord = pathWord,
            workerState = state,
            localSavableMangaModel = manga,
            isStopped = DownloadState isStoppedIn data,
            isPause = DownloadState isPauseIn data,
            isIndeterminate = DownloadState indeterminateFor data,
            max = DownloadState getMax data,
            totalChapter = DownloadState.downloadChaptersIn(data).size,
            error = DownloadState getError data,
            progress = DownloadState getProgress data,
            timeStamp = DownloadState timeStampWhich data,
            id = id,
            eta = DownloadState timeETAIn data
        )
    }


    private suspend fun getManga(pathWord: String): LocalSavableMangaModel? {
        mangaCache[pathWord]?.let {
            return it
        }
        return mutexCache.withLock {
            mangaCache.getOrElse(pathWord) {
                mangaHistoryRepository.getMangaByPathWord(pathWord)
                    ?.also {
                        mangaCache[pathWord] = it
                    } ?: return null
            }
        }
    }


}