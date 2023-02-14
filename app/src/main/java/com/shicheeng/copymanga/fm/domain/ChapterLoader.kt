package com.shicheeng.copymanga.fm.domain

import com.shicheeng.copymanga.data.MangaInfoChapterDataBean
import com.shicheeng.copymanga.data.MangaReaderPage
import com.shicheeng.copymanga.json.MangaReadJson
import com.shicheeng.copymanga.util.FileUtil
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class ChapterLoader(private val fileUtil: FileUtil) {

    val chapters = LinkedHashMap<String, MangaInfoChapterDataBean>()
    private val chapterPage = ChapterPages()
    private val mutex = Mutex()

    suspend fun loadPrevNextChapter(
        list: Array<MangaInfoChapterDataBean>?,
        uuid: String?,
        isNext: Boolean,
    ) {
        val chapters = list ?: return
        val predicate: (MangaInfoChapterDataBean) -> Boolean = { it.uuidText == uuid }
        val index =
            if (isNext) chapters.indexOfFirst(predicate) else chapters.indexOfLast(predicate)
        if (index == -1) return
        val newChapter = chapters.getOrNull(if (isNext) index + 1 else index - 1) ?: return
        val newPages = loadChapter(newChapter.pathWord, newChapter.uuidText)
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
                chapterPage.addLast(newChapter.uuidText, newPages)
            } else {
                chapterPage.addFirst(newChapter.uuidText, newPages)
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
        return MangaReadJson.getMangaPic(pathWord, chapter.uuidText, fileUtil)
    }

}