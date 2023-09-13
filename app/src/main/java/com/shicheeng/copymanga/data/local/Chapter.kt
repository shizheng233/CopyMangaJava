package com.shicheeng.copymanga.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.shicheeng.copymanga.data.MangaState


@Entity
data class LocalChapter(
    val comicId: String,
    val comicPathWord: String,
    val count: Int,
    var datetime_created: String,
    val groupId: String?,
    val groupPathWord: String,
    val imgType: Int,
    val index: Int,
    val readIndex: Int,
    val isReadProgress: Boolean,
    val name: String,
    val news: String,
    val next: String?,
    val ordered: Int,
    val prev: String?,
    val size: Int,
    val type: Int,
    @PrimaryKey val uuid: String,
    val isDownloaded: Boolean,
    val isReadFinish: Boolean,
)

fun LocalChapter.toMangaState(): MangaState {
    return MangaState(
        uuid = uuid,
        page = if (isReadFinish) 0 else readIndex
    )
}