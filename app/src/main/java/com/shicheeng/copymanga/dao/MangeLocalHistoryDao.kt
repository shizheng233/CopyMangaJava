package com.shicheeng.copymanga.dao

import androidx.room.*
import com.shicheeng.copymanga.data.MangaHistoryDataModel
import com.shicheeng.copymanga.data.local.LocalChapter
import com.shicheeng.copymanga.data.local.LocalSavableMangaModel
import kotlinx.coroutines.flow.Flow

@Dao
interface MangeLocalHistoryDao {

    @Query("SELECT * FROM manga_history_key ORDER by time DESC")
    fun getAllHistory(): Flow<List<MangaHistoryDataModel>>

    @Query("SELECT * FROM manga_history_key ORDER by time DESC")
    suspend fun fetchTotalManga(): List<MangaHistoryDataModel>

    @Query("SELECT * FROM manga_history_key WHERE pathWord LIKE :pathWord LIMIT 1")
    suspend fun getHistoryForInfoByPathWord(pathWord: String): MangaHistoryDataModel?

    @Query("SELECT * FROM manga_history_key WHERE pathWord LIKE :pathWord LIMIT 1")
    fun fetchHistoryByPathWordInFlow(pathWord: String): Flow<MangaHistoryDataModel?>

    @Transaction
    @Query("SELECT * FROM manga_history_key WHERE pathWord = :pathWord LIMIT 1")
    suspend fun getMangaByPathWord(pathWord: String): LocalSavableMangaModel?

    @Query("SELECT * FROM LocalChapter WHERE comicPathWord = :pathWord")
    suspend fun fetchMangaChaptersByPathWord(pathWord: String): List<LocalChapter>?

    @Query("SELECT * FROM LocalChapter WHERE comicPathWord = :pathWord")
    fun fetchMangaChaptersByPathWordFlow(pathWord: String): Flow<List<LocalChapter>?>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addLocal(manga: MangaHistoryDataModel)

    @Upsert
    suspend fun addLocalChapter(chapter: LocalChapter)

    @Upsert
    suspend fun addLocalChapter(vararg chapter: LocalChapter)

    @Upsert
    suspend fun updateLocal(manga: MangaHistoryDataModel)

    @Query("DELETE FROM manga_history_key")
    suspend fun deleteAllHistory()

}