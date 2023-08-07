package com.shicheeng.copymanga.data.local

import androidx.room.Embedded
import androidx.room.Relation
import com.shicheeng.copymanga.data.MangaHistoryDataModel


data class LocalSavableMangaModel(
    @Embedded val mangaHistoryDataModel: MangaHistoryDataModel,
    @Relation(
        parentColumn = "pathWord",
        entityColumn = "comicPathWord"
    )
    val list: List<LocalChapter>,
)