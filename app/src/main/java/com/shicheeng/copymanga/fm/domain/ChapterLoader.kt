package com.shicheeng.copymanga.fm.domain

import com.shicheeng.copymanga.data.MangaReaderPage
import com.shicheeng.copymanga.data.local.LocalChapter
import com.shicheeng.copymanga.domin.DownloadFileDetectUtil
import com.shicheeng.copymanga.resposity.MangaInfoRepository
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject

@ViewModelScoped
class ChapterLoader @Inject constructor(
    private val fileDetectUtil: DownloadFileDetectUtil,
    private val repository: MangaInfoRepository,
) {

    val chapters = LinkedHashMap<String, LocalChapter>()
    val nextChapterLoadingState =
        MutableStateFlow<NextChapterLoadState>(NextChapterLoadState.NotLoading)

    private val chapterPage = ChapterPages()
    private val mutex = Mutex()

    suspend fun init(list: List<LocalChapter>?) = mutex.withLock {
        chapters.clear()
        list?.forEach {
            chapters[it.uuid] = it
        }
    }

    suspend fun loadPrevNextChapter(
        list: List<LocalChapter>?,
        uuid: String?,
        isNext: Boolean,
    ) {
        nextChapterLoadingState.emit(NextChapterLoadState.Loading)
        val chapters = list ?: return
        val predicate: (LocalChapter) -> Boolean = { it.uuid == uuid }
        val index =
            if (isNext) chapters.indexOfFirst(predicate) else chapters.indexOfLast(predicate)
        if (index == -1) return
        val newChapter = chapters.getOrNull(if (isNext) index + 1 else index - 1) ?: return
        try {
            val newPages = loadChapter(newChapter.comicPathWord, newChapter.uuid)
            mutex.withLock {
                if (chapterPage.chapterSize > 1) {
                    if (chapterPage.size > 130) {
                        if (isNext) {
                            chapterPage.removeFirst()
                        } else {
                            chapterPage.removeLast()
                        }
                    }
                }
                if (isNext) {
                    chapterPage.addLast(newChapter.uuid, newPages)
                } else {
                    chapterPage.addFirst(newChapter.uuid, newPages)
                }
                nextChapterLoadingState.emit(NextChapterLoadState.NotLoading)
            }
        } catch (e: Exception) {
            nextChapterLoadingState.emit(NextChapterLoadState.Error(e))
        }

    }

    suspend fun loadSingleChapter(pathWord: String, uuid: String) {
        val page = loadChapter(pathWord, uuid)
        mutex.withLock {
            chapterPage.clear()
            chapterPage.addLast(uuid, page)
        }
    }

    fun getPage(uuid: String): List<MangaReaderPage> {
        return chapterPage.subList(uuid)
    }

    operator fun get(uuid: String): Int {
        return chapterPage.size(uuid)
    }

    fun snapshot() = chapterPage.toList()

    fun last() = chapterPage.last()

    fun first() = chapterPage.first()

    val size get() = chapters.size

    private suspend fun loadChapter(pathWord: String, uui: String): List<MangaReaderPage> {
        val chapter = checkNotNull(chapters[uui]) { "NO CHAPTER FOUND" }
        val isDownload = fileDetectUtil.isChapterDownloadedWithStringList(pathWord, chapter.uuid)
        val listLocal =
            if (isDownload) fileDetectUtil.ifChapterDownloaded(pathWord, chapter.uuid) else null
        return repository.fetchContentMayLocal(listLocal, pathWord, chapter.uuid)
    }

    sealed class NextChapterLoadState {

        object Loading : NextChapterLoadState()
        data class Error(val e: Throwable) : NextChapterLoadState()
        object NotLoading : NextChapterLoadState()

    }

}