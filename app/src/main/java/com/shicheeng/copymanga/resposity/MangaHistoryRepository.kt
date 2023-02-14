package com.shicheeng.copymanga.resposity

import com.shicheeng.copymanga.dao.MangeLocalHistoryDao
import com.shicheeng.copymanga.data.MangaHistoryDataModel
import kotlinx.coroutines.flow.Flow

class MangaHistoryRepository(private val mangeLocalHistoryDao: MangeLocalHistoryDao) {

    val allHistoryDao: Flow<List<MangaHistoryDataModel>> = mangeLocalHistoryDao.getAllHistory()

    suspend fun insert(mangaLocalHistory: MangaHistoryDataModel) {
        mangeLocalHistoryDao.addLocal(mangaLocalHistory)
    }

    suspend fun update(mangaLocalHistory: MangaHistoryDataModel) {
        mangeLocalHistoryDao.updateLocal(mangaLocalHistory)
    }

    suspend fun getHistoryByMangaPathWord(pathWord: String): MangaHistoryDataModel? =
        mangeLocalHistoryDao.getHistoryForInfoByPathWord(pathWord)

    suspend fun delHistory() {
        mangeLocalHistoryDao.deleteAllHistory()
    }

}