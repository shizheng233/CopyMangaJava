package com.shicheeng.copymanga.dao

import androidx.room.*
import com.shicheeng.copymanga.data.MangaHistoryDataModel
import kotlinx.coroutines.flow.Flow

@Dao
interface MangeLocalHistoryDao {

    @Query("SELECT * FROM manga_history_key ORDER by time DESC")
    fun getAllHistory(): Flow<List<MangaHistoryDataModel>>

    @Query("SELECT * FROM manga_history_key WHERE pathWord LIKE :pathWord LIMIT 1")
    suspend fun getHistoryForInfoByPathWord(pathWord:String): MangaHistoryDataModel?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addLocal(manga: MangaHistoryDataModel)

    @Update
    suspend fun updateLocal(manga: MangaHistoryDataModel)

    @Query("DELETE FROM manga_history_key")
    suspend fun deleteAllHistory()

}