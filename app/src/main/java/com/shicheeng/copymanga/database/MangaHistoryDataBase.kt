package com.shicheeng.copymanga.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.shicheeng.copymanga.dao.MangeLocalHistoryDao
import com.shicheeng.copymanga.dao.SearchHistoryDao
import com.shicheeng.copymanga.data.MangaHistoryDataModel
import com.shicheeng.copymanga.data.local.LocalChapter
import com.shicheeng.copymanga.data.searchhistory.SearchHistory

@Database(
    entities = [MangaHistoryDataModel::class, LocalChapter::class, SearchHistory::class],
    version = 4,
    exportSchema = false
)
abstract class MangaHistoryDataBase : RoomDatabase() {

    abstract fun historyDao(): MangeLocalHistoryDao

    abstract fun keyWordDao(): SearchHistoryDao

}
