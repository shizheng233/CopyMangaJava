package com.shicheeng.copymanga.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.shicheeng.copymanga.data.login.LocalLoginDataModel
import kotlinx.coroutines.flow.Flow

@Dao
interface MangaLoginDao {

    @Upsert
    suspend fun updateOrInsertLoginData(localLoginDataModel: LocalLoginDataModel)

    @Query("SELECT * FROM LocalLoginDataModel")
    fun getLoginData(): Flow<List<LocalLoginDataModel>>

    @Query("SELECT * FROM LocalLoginDataModel")
    suspend fun getLoginDataAsync(): List<LocalLoginDataModel>

    @Query("SELECT * FROM LocalLoginDataModel where userID = :userID LIMIT 1")
    fun getLoginDataByUserId(userID: String): Flow<LocalLoginDataModel>

    @Query("SELECT * FROM LocalLoginDataModel where userID = :userID LIMIT 1")
    suspend fun getLoginDataByUserIdSafety(userID: String?): LocalLoginDataModel?

    @Upsert
    suspend fun updateOrInsertLoginData(vararg localLoginDataModels: LocalLoginDataModel)

    @Query("SELECT token FROM LocalLoginDataModel where userID = :uuid LIMIT 1")
    fun getCurrentToken(uuid: String): String

    @Query("SELECT isExpired FROM LocalLoginDataModel where userID = :uuid LIMIT 1")
    fun isExpired(uuid: String): Boolean

    @Query("SELECT isExpired FROM LocalLoginDataModel where userID = :uuid LIMIT 1")
    fun isExpiredFlow(uuid: String): Flow<Boolean>


    @Delete
    suspend fun deleteLoginData(localLoginDataModel: LocalLoginDataModel)

}