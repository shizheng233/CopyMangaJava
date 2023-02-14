package com.shicheeng.copymanga.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "manga_history_key")
data class MangaHistoryDataModel(
    val name: String,
    val time: Long,
    val url: String,
    @PrimaryKey val pathWord: String,
    val nameChapter: String,
    val positionChapter: Int,
    val positionPage: Int,
    val readerModeId: Int,
)

data class MangaState(
    val uuid: String,
    val page: Int,
)