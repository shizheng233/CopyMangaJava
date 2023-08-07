package com.shicheeng.copymanga.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.shicheeng.copymanga.data.searchhistory.SearchHistory
import kotlinx.coroutines.flow.Flow

@Dao
interface SearchHistoryDao {

    @Query("SELECT * FROM SearchHistory ORDER by time DESC")
    fun loadWordHistory(): Flow<List<SearchHistory>>

    @Delete
    suspend fun detectSearchedWordHistory(searchHistory: SearchHistory)

    @Query("DELETE FROM SearchHistory")
    suspend fun delThing()

    @Upsert
    suspend fun upsertWord(searchHistory: SearchHistory)

}