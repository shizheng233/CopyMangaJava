package com.shicheeng.copymanga.fm.domain

import com.shicheeng.copymanga.data.MangaReaderPage
import com.shicheeng.copymanga.data.local.LocalChapter
import com.shicheeng.copymanga.resposity.MangaInfoRepository
import com.shicheeng.copymanga.util.FileUtil
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class ChapterLoader(
    private val fileUtil: FileUtil,
    private val repository: MangaInfoRepository,
) {

    val chapters = LinkedHashMap<String, LocalChapter>()
    private val chapterPage = ChapterPages()
    private val mutex = Mutex()

    suspend fun loadPrevNextChapter(
        list: List<LocalChapter>?,
        uuid: String?,
        isNext: Boolean,
    ) {
        val chapters = list ?: return
        val predicate: (LocalChapter) -> Boolean = { it.uuid == uuid }
        val index =
            if (isNext) chapters.indexOfFirst(predicate) else chapters.indexOfLast(predicate)
        if (index == -1) return
        val newChapter = chapters.getOrNull(if (isNext) index + 1 else index - 1) ?: return
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

    private suspend fun loadChapter(pathWord: String, uui: String): List<MangaReaderPage> {
        val chapter = checkNotNull(chapters[uui]) { "NO CHAPTER FOUND" }
        val isDownload = fileUtil.isChapterDownloadedWithStringList(pathWord, chapter.uuid)
        val listLocal =
            if (isDownload) fileUtil.ifChapterDownloaded(pathWord, chapter.uuid) else null
        return repository.fetchContentMayLocal(listLocal, pathWord, chapter.uuid)
    }

}