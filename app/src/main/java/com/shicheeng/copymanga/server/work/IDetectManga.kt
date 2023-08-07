package com.shicheeng.copymanga.server.work

import com.shicheeng.copymanga.data.MangaHistoryDataModel
import com.shicheeng.copymanga.data.local.LocalChapter
import com.shicheeng.copymanga.resposity.MangaHistoryRepository
import com.shicheeng.copymanga.resposity.MangaInfoRepository
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class IDetectManga(
    private val repository: MangaHistoryRepository,
    private val infoRepository: MangaInfoRepository,
    private val onMangaDetectUpdate: OnMangaDetectUpdate,
) {

    private val mutex = Mutex()

    suspend fun fetchMangaUpdate() {
        onMangaDetectUpdate.onReady()
        val totalMangas = repository.totalHistoryManga().filter { it.isSubscribe }
            totalMangas.forEachIndexed { index, mangaHistoryDataModel ->
                mutex.withLock {
                try {
                    onMangaDetectUpdate.onSubscribe(
                        index = index,
                        size = totalMangas.size,
                        historyDataModel = mangaHistoryDataModel
                    )
                    val oldList = repository
                        .fetchMangaChapterByPathWord(mangaHistoryDataModel.pathWord)
                    val list = infoRepository
                        .fetchMangaChaptersForce(mangaHistoryDataModel.pathWord)
                    oldList?.let {
                        val newChapter = list.filterNot { y ->
                            it.any { x -> x.uuid == y.uuid }
                        }
                        onMangaDetectUpdate.onSingleSuccess(
                            index = index,
                            historyDataModel = mangaHistoryDataModel,
                            newChapter = newChapter
                        )
                    }
                } catch (e: Exception) {
                    onMangaDetectUpdate.onError(index, mangaHistoryDataModel, e)
                }
            }
        }
        onMangaDetectUpdate.onSuccess()
    }

    interface OnMangaDetectUpdate {
        fun onReady()
        fun onSubscribe(index: Int, size: Int, historyDataModel: MangaHistoryDataModel)
        fun onError(eIndex: Int, historyDataModel: MangaHistoryDataModel, exception: Throwable)
        fun onSingleSuccess(
            index: Int,
            historyDataModel: MangaHistoryDataModel,
            newChapter: List<LocalChapter>,
        )

        fun onSuccess()
    }

}