package com.shicheeng.copymanga.server

import com.shicheeng.copymanga.data.local.LocalChapter
import com.shicheeng.copymanga.data.local.LocalSavableMangaModel

sealed interface DownloadStateChapter {
    val chapterID: Int
    val chapter: LocalSavableMangaModel

    class WAITING(
        override val chapterID: Int,
        override val chapter: LocalSavableMangaModel,
    ) : DownloadStateChapter

    class PREPARE(
        override val chapterID: Int,
        override val chapter: LocalSavableMangaModel,
    ) : DownloadStateChapter

    class DOWNLOADING(
        override val chapterID: Int,
        override val chapter: LocalSavableMangaModel,
        totalChapters: Int,
        currentChapter: Int,
        val totalPages: Int,
        val currentPage: Int,
        val currentLocalChapter: LocalChapter,
    ) : DownloadStateChapter {
        val max: Int = totalChapters * totalPages
        val progress: Int = totalPages * currentChapter + currentPage + 1
        val percent: Float = progress.toFloat() / max
    }

    class ERROR(
        override val chapterID: Int,
        override val chapter: LocalSavableMangaModel,
        val error: Throwable,
    ) : DownloadStateChapter

    class DONE(
        override val chapterID: Int,
        override val chapter: LocalSavableMangaModel,
    ) : DownloadStateChapter

    class PostBeforeDone(
        override val chapterID: Int,
        override val chapter: LocalSavableMangaModel,
    ) :
        DownloadStateChapter

    class CANCEL(override val chapterID: Int, override val chapter: LocalSavableMangaModel) :
        DownloadStateChapter

}




