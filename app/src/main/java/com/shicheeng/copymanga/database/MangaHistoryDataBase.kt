package com.shicheeng.copymanga.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.shicheeng.copymanga.dao.MangeLocalHistoryDao
import com.shicheeng.copymanga.data.MangaHistoryDataModel

@Database(entities = [MangaHistoryDataModel::class], version = 1,exportSchema = false)
abstract class MangaHistoryDataBase : RoomDatabase() {

    abstract fun historyDao(): MangeLocalHistoryDao

    companion object {

        @Volatile
        private var INSTANCE: MangaHistoryDataBase? = null
        fun getDataBase(context: Context): MangaHistoryDataBase {
            return INSTANCE ?: synchronized(this) {
                val ins = Room.databaseBuilder(
                    context.applicationContext,
                    MangaHistoryDataBase::class.java,
                    "manga_history_database_2"
                ).build()
                INSTANCE = ins
                ins
            }
        }
    }

}