package com.shicheeng.copymanga.fm.domain

import com.shicheeng.copymanga.data.MangaReaderPage

/**
 * Copy from Kotatsu
 *
 * The class was _reformed_ for this App
 */
class ChapterPages private constructor(private val pages: ArrayDeque<MangaReaderPage>) :
    List<MangaReaderPage> by pages {

    private val indices = LinkedHashMap<String, IntRange>()

    constructor() : this(ArrayDeque())

    val chapterSize: Int get() = indices.size

    fun removeFirst() {
        val chapterId = pages.first().uuid
        indices.remove(chapterId)
        var delta = 0
        while (pages.first().uuid == chapterId) {
            pages.removeFirst()
            delta--
        }
        shiftIndices(delta)
    }

    fun removeLast() {
        val chapterId = pages.last().uuid
        indices.remove(chapterId)
        while (pages.last().uuid == chapterId) {
            pages.removeLast()
        }
    }

    fun addLast(id: String, newPages: List<MangaReaderPage>) {
        indices[id] = pages.size until (pages.size + newPages.size)
        pages.addAll(newPages)
    }

    fun addFirst(id: String, newPages: List<MangaReaderPage>) {
        shiftIndices(newPages.size)
        indices[id] = newPages.indices
        pages.addAll(0, newPages)
    }

    fun clear() {
        indices.clear()
        pages.clear()
    }

    fun size(id: String) = indices[id]?.run {
        endInclusive - start + 1
    } ?: 0

    fun subList(id: String): List<MangaReaderPage> {
        val range = indices[id] ?: return emptyList()
        return pages.subList(range.first, range.last + 1)
    }

    private fun shiftIndices(delta: Int) {
        indices.forEach { (t, u) ->
            indices[t] = u + delta
        }
    }

    private operator fun IntRange.plus(delta: Int): IntRange {
        return IntRange(start + delta, endInclusive + delta)
    }
}