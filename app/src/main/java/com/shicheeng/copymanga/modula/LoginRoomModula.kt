package com.shicheeng.copymanga.modula

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.shicheeng.copymanga.database.MangaLoginDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object LoginRoomModula {

    @Provides
    @Singleton
    fun initDatabase(@ApplicationContext context: Context): MangaLoginDatabase {
        return Room.databaseBuilder(
            context = context,
            klass = MangaLoginDatabase::class.java,
            name = "login_data"
        )
            .addMigrations(_version1to2)
            .build()
    }

    @Provides
    @Singleton
    fun provideLoginDao(mangaLoginDatabase: MangaLoginDatabase) = mangaLoginDatabase.loginDao()

}

private val _version1to2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE LocalLoginDataModel ADD COLUMN isExpired INTEGER NOT NULL DEFAULT 0")
    }
}