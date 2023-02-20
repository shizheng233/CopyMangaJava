package com.shicheeng.copymanga.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MangaInfoChapterDataBean(
    val chapterTitle: String,
    val chapterTime: String,
    val uuidText: String,
    val readerProgress: Int?,
    val isDownloading: Boolean = false,
    val pathWord: String,
    val isSaved: Boolean = false,
) : Parcelable {

    fun toDownloadChapter(): MangaDownloadChapterInfoModel {
        return MangaDownloadChapterInfoModel(chapterTitle, uuidText, pathWord)
    }

    fun toMangaState(): MangaState {
        return MangaState(uuidText, readerProgress ?: 0)
    }

}

@Parcelize
data class LastMangaDownload(
    /**
     * 漫画名字
     */
    val mangaName: String,
    val coverUrl:String,
    val list: List<MangaDownloadChapterInfoModel>) :
    Parcelable

@Parcelize
data class MangaDownloadChapterInfoModel(
    val chapterTitle: String,
    /**
     * 漫画章节的UUID
     */
    val uuidText: String,
    /**
     * 漫画的pathWord
     */
    val pathWord: String,
) : Parcelable {

    override fun hashCode(): Int {
        return pathWord.hashCode() + uuidText.length + chapterTitle.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MangaDownloadChapterInfoModel

        if (chapterTitle != other.chapterTitle) return false
        if (uuidText != other.uuidText) return false
        if (pathWord != other.pathWord) return false

        return true
    }


}


@Parcelize
data class MangaDownloads(
    val urlList: List<String>,
    val wordsList: List<Int>,
) : Parcelable {
    override fun hashCode(): Int {
        return urlList.size + wordsList.size
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MangaDownloads

        if (urlList != other.urlList) return false
        if (wordsList != other.wordsList) return false

        return true
    }
}

