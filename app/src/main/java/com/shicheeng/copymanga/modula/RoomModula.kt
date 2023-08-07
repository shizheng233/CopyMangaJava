package com.shicheeng.copymanga.modula

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.shicheeng.copymanga.database.MangaHistoryDataBase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RoomModula {

    @Provides
    @Singleton
    fun provideHistoryDataBase(@ApplicationContext context: Context) = Room.databaseBuilder(
        context = context,
        klass = MangaHistoryDataBase::class.java,
        name = "manga_history_database_2"
    )
        .addMigrations(VERSION1to2, VERSION2to3, VERSION3to4)
        .build()

    @Provides
    @Singleton
    fun provideHistoryDao(mangaHistoryDataBase: MangaHistoryDataBase) =
        mangaHistoryDataBase.historyDao()

    @Provides
    @Singleton
    fun provideKeyWordHistoryDao(mangaHistoryDataBase: MangaHistoryDataBase) =
        mangaHistoryDataBase.keyWordDao()

}

/**
 * 迁移新的历史记录，该历史记录保存更加详细的内容。
 */
object VERSION1to2 : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        //for local information
        database.execSQL("ALTER TABLE manga_history_key ADD COLUMN alias TEXT NULL DEFAULT NULL")
        database.execSQL("ALTER TABLE manga_history_key ADD COLUMN mangaDetail TEXT NOT NULL DEFAULT \"空\" ")
        database.execSQL("ALTER TABLE manga_history_key ADD COLUMN mangaStatus TEXT NOT NULL DEFAULT \"空\" ")
        database.execSQL("ALTER TABLE manga_history_key ADD COLUMN authorList TEXT NOT NULL DEFAULT \"空\"")
        database.execSQL("ALTER TABLE manga_history_key ADD COLUMN themeList TEXT NOT NULL DEFAULT \"空\"")
        database.execSQL("ALTER TABLE manga_history_key ADD COLUMN mangaStatusId INTEGER NOT NULL DEFAULT 0")
        database.execSQL("ALTER TABLE manga_history_key ADD COLUMN mangaRegion TEXT NOT NULL DEFAULT \"空\"")
        database.execSQL("ALTER TABLE manga_history_key ADD COLUMN mangaLastUpdate TEXT NOT NULL DEFAULT \"空\"")
        database.execSQL("ALTER TABLE manga_history_key ADD COLUMN mangaPopularNumber TEXT NOT NULL DEFAULT \"空\"")

        //for local chapter
        database.execSQL(
            "CREATE TABLE LocalChapter (" +
                    "uuid TEXT PRIMARY KEY NOT NULL," +
                    "groupId TEXT NULL DEFAULT NULL," +
                    "comicId TEXT NOT NULL," +
                    "comicPathWord TEXT NOT NULL," +
                    "groupPathWord TEXT NOT NULL," +
                    "name TEXT NOT NULL," +
                    "imgType INTEGER NOT NULL," +
                    "isReadProgress INTEGER NOT NULL," +
                    "next TEXT NULL," +
                    "ordered INTEGER NOT NULL," +
                    "prev TEXT NULL," +
                    "type INTEGER NOT NULL," +
                    "size INTEGER NOT NULL," +
                    "datetime_created TEXT NOT NULL," +
                    "count INTEGER NOT NULL," +
                    "readIndex INTEGER NOT NULL," +
                    "news TEXT NOT NULL," +
                    "`index` INTEGER NOT NULL" +
                    ")"
        )
    }
}

/**
 * 迁移到新的版本
 */
object VERSION2to3 : Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE manga_history_key ADD COLUMN isSubscribe INTEGER NOT NULL DEFAULT 0")
        database.execSQL("ALTER TABLE LocalChapter ADD COLUMN isDownloaded INTEGER NOT NULL DEFAULT 0")
    }

}

object VERSION3to4 : Migration(3, 4) {

    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            "CREATE TABLE SearchHistory (" +
                    "word TEXT PRIMARY KEY NOT NULL," +
                    "time INTEGER NOT NULL" +
                    ")"
        )
    }

}