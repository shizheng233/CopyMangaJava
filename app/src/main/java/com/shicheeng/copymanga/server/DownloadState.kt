package com.shicheeng.copymanga.server

import com.shicheeng.copymanga.data.LastMangaDownload
import com.shicheeng.copymanga.data.MangaDownloadChapterInfoModel

sealed interface DownloadStateChapter {
    val chapterID: Int
    val chapter: LastMangaDownload

    class WAITING(
        override val chapterID: Int,
        override val chapter: LastMangaDownload,
    ) : DownloadStateChapter

    class PREPARE(
        override val chapterID: Int,
        override val chapter: LastMangaDownload,
    ) : DownloadStateChapter

    class DOWNLOADING(
        override val chapterID: Int,
        override val chapter: LastMangaDownload,
        val totalChapters: Int,
        val currentChapter: Int,
        val totalPages: Int,
        val currentPage: Int,
    ) : DownloadStateChapter {
        val max: Int = totalChapters * totalPages
        val progress: Int = totalPages * currentChapter + currentPage + 1
        val percent: Float = progress.toFloat() / max
    }

    class ERROR(
        override val chapterID: Int,
        override val chapter: LastMangaDownload,
        val error: Throwable,
    ) : DownloadStateChapter

    class ChapterChange(
        override val chapterID: Int,
        override val chapter: LastMangaDownload,
        val chapterInDownload: MangaDownloadChapterInfoModel,
    ) : DownloadStateChapter

    class DONE(
        override val chapterID: Int,
        override val chapter: LastMangaDownload,
    ) : DownloadStateChapter

    class PostBeforeDone(override val chapterID: Int, override val chapter: LastMangaDownload) :
        DownloadStateChapter

    class CANCEL(override val chapterID: Int, override val chapter: LastMangaDownload) :
        DownloadStateChapter

}




