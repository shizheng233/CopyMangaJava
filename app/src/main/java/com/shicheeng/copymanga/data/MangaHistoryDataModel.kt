package com.shicheeng.copymanga.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.shicheeng.copymanga.database.StringToBeanConvert

@TypeConverters(StringToBeanConvert::class)
@Entity(tableName = "manga_history_key")
data class MangaHistoryDataModel(
    val name: String,
    val time: Long,
    val alias: String?,
    val url: String,
    @PrimaryKey val pathWord: String,
    val nameChapter: String,
    val positionChapter: Int,
    val positionPage: Int,
    val readerModeId: Int,
    val mangaDetail: String,
    val mangaStatus: String,
    val authorList: String,
    val themeList: List<MangaSortBean>,
    val mangaStatusId: Int,
    val mangaRegion: String,
    val mangaLastUpdate: String,
    val mangaPopularNumber: String,
    val isSubscribe:Boolean
)

data class MangaState(
    val uuid: String,
    val page: Int,
)