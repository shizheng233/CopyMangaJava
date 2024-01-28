package com.shicheeng.copymanga.server.download.domin

import androidx.work.Data
import com.shicheeng.copymanga.data.LocalManga
import com.shicheeng.copymanga.data.local.LocalSavableMangaModel

data class DownloadState(
    val localSavableMangaModel: LocalSavableMangaModel,
    val error: String? = null,
    val isStopped: Boolean = false,
    val isPaused: Boolean = false,
    val totalChapters: Int = 0,
    val currentChapter: Int = 0,
    val isIndeterminate: Boolean = false,
    val totalPages: Int = 0,
    val currentPage: Int = 0,
    val localManga: LocalManga? = null,
    val downloadedChapters: Array<String> = emptyArray(),
    val timestamp: Long = System.currentTimeMillis(),
    val eta: Long = -1L,
) {
    val max: Int = totalChapters * totalPages
    val progress: Int = totalPages * currentChapter + currentPage + 1

    val percent: Float = if (max > 0) progress.toFloat() / max else PROGRESS_NONE

    val isParticularProgress: Boolean
        get() = localManga == null && error == null && !isPaused && !isStopped && max > 0 && !isIndeterminate

    val isFinalState: Boolean
        get() = localManga != null || (error != null && !isPaused)


    fun transformToWorkData() = Data.Builder()
        .putInt(MANGA_MAX, max)
        .putInt(MANGA_PROGRESS, progress)
        .putString(MANGA_ERROR, error)
        .putStringArray(MANGA_DOWNLOAD_CHAPTER, downloadedChapters)
        .putLong(MANGA_TIME_STAMP, timestamp)
        .putString(MANGA_PATH_WORD, localSavableMangaModel.mangaHistoryDataModel.pathWord)
        .putBoolean(IS_INDETERMINATE, isIndeterminate)
        .putBoolean(IS_STOPPED, isStopped)
        .putBoolean(IS_PAUSE, isPaused)
        .putLong(MANGA_ETA, eta)
        .build()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DownloadState

        if (localSavableMangaModel != other.localSavableMangaModel) return false
        if (error != other.error) return false
        if (totalChapters != other.totalChapters) return false
        if (currentChapter != other.currentChapter) return false
        if (totalPages != other.totalPages) return false
        if (currentPage != other.currentPage) return false
        if (!downloadedChapters.contentEquals(other.downloadedChapters)) return false
        if (timestamp != other.timestamp) return false
        if (isStopped != other.isStopped) return false
        if (max != other.max) return false
        if (progress != other.progress) return false
        if (isPaused != other.isPaused) return false
        if (isIndeterminate != other.isIndeterminate) return false
        return percent == other.percent
    }

    override fun hashCode(): Int {
        var result = localSavableMangaModel.hashCode()
        result = 31 * result + (error?.hashCode() ?: 0)
        result = 31 * result + totalChapters
        result = 31 * result + currentChapter
        result = 31 * result + totalPages
        result = 31 * result + currentPage
        result = 31 * result + downloadedChapters.contentHashCode()
        result = 31 * result + timestamp.hashCode()
        result = 31 * result + max
        result = 31 * result + totalPages
        result = 31 * result + percent.hashCode()
        result = 31 * result + isIndeterminate.hashCode()
        result = 31 * result + isPaused.hashCode()
        result = 31 * result + isStopped.hashCode()
        return result
    }

    companion object {

        private const val PROGRESS_NONE = -1f
        private const val MANGA_PATH_WORD = "MangaPathWord"
        private const val MANGA_MAX = "MangaMax"
        private const val MANGA_ETA = "MangaEta"
        private const val IS_PAUSE = "IsPause"
        private const val IS_STOPPED = "IsStopped"
        private const val MANGA_ERROR = "MangaError"
        private const val MANGA_DOWNLOAD_CHAPTER = "MangaDownloadChapter"
        private const val MANGA_CURRENT = "MangaCurrent"
        private const val IS_INDETERMINATE = "IsIndeterminate"
        private const val MANGA_TIME_STAMP = "MangaTimeStamp"
        private const val MANGA_PROGRESS = "MangaProgress"

        infix fun getMangaPathWord(data: Data) = data.getString(MANGA_PATH_WORD)
        infix fun getError(data: Data) = data.getString(MANGA_ERROR)
        infix fun getMax(data: Data) = data.getInt(MANGA_MAX, 0)
        infix fun getProgress(data: Data) = data.getInt(MANGA_PROGRESS, 0)
        infix fun timeStampWhich(data: Data) = data.getLong(MANGA_TIME_STAMP, 0L)
        infix fun downloadChaptersIn(data: Data): Array<String> =
            data.getStringArray(MANGA_DOWNLOAD_CHAPTER) ?: emptyArray()

        infix fun indeterminateFor(data: Data): Boolean {
            return data.getBoolean(IS_INDETERMINATE, false)
        }

        infix fun isPauseIn(data: Data): Boolean = data.getBoolean(IS_PAUSE, false)
        infix fun isStoppedIn(data: Data): Boolean = data.getBoolean(IS_STOPPED, false)
        infix fun timeETAIn(data: Data) = data.getLong(MANGA_ETA, 0L)

    }


}
