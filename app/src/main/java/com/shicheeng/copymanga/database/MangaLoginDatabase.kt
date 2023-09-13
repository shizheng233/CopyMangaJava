package com.shicheeng.copymanga.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.shicheeng.copymanga.dao.MangaLoginDao
import com.shicheeng.copymanga.data.login.LocalLoginDataModel

@Database(
    entities = [LocalLoginDataModel::class],
    version = 2,
    exportSchema = false
)
abstract class MangaLoginDatabase : RoomDatabase() {

    abstract fun loginDao(): MangaLoginDao

}