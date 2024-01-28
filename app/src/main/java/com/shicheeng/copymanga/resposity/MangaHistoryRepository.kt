package com.shicheeng.copymanga.resposity

import com.shicheeng.copymanga.dao.MangeLocalHistoryDao
import com.shicheeng.copymanga.dao.SearchHistoryDao
import com.shicheeng.copymanga.data.MangaHistoryDataModel
import com.shicheeng.copymanga.data.local.LocalChapter
import com.shicheeng.copymanga.data.local.LocalSavableMangaModel
import com.shicheeng.copymanga.data.searchhistory.SearchHistory
import com.shicheeng.copymanga.util.processLifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MangaHistoryRepository @Inject constructor(
    private val mangeLocalHistoryDao: MangeLocalHistoryDao,
    private val searchedWordDao: SearchHistoryDao,
) {

    val allHistoryDao: Flow<List<MangaHistoryDataModel>> = mangeLocalHistoryDao.getAllHistory()

    suspend fun totalHistoryManga() =
        mangeLocalHistoryDao.fetchTotalManga()

    suspend fun getMangaByPathWord(pathWord: String): LocalSavableMangaModel? {
        return mangeLocalHistoryDao.getMangaByPathWord(pathWord)
    }

    suspend fun fetchMangaChapterByPathWord(pathWord: String): List<LocalChapter>? {
        return mangeLocalHistoryDao.fetchMangaChaptersByPathWord(pathWord)
    }

    fun fetchMangaChapterByPathWordFlow(pathWord: String): Flow<List<LocalChapter>?> {
        return mangeLocalHistoryDao.fetchMangaChaptersByPathWordFlow(pathWord)
    }

    fun fetchMangaByPathWordInFlow(pathWord: String) =
        mangeLocalHistoryDao.fetchHistoryByPathWordInFlow(pathWord)


    suspend fun update(mangaLocalHistory: MangaHistoryDataModel) {
        mangeLocalHistoryDao.updateLocal(mangaLocalHistory)
    }

    /**
     * 保存漫画历史。其生命周期不随ViewModel。
     */
    fun updateAsync(mangaLocalHistory: MangaHistoryDataModel) {
        processLifecycleScope.launch(Dispatchers.IO) {
            try {
                mangeLocalHistoryDao.updateLocal(mangaLocalHistory)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    suspend fun updateLocalChapter(localChapter: LocalChapter) {
        mangeLocalHistoryDao.addLocalChapter(localChapter)
    }

    suspend fun updateLocalChapter(localChapter: List<LocalChapter>) {
        mangeLocalHistoryDao.addLocalChapter(*localChapter.toTypedArray())
    }

    suspend fun getHistoryByMangaPathWord(pathWord: String): MangaHistoryDataModel? =
        mangeLocalHistoryDao.getHistoryForInfoByPathWord(pathWord)

    suspend fun delHistory() {
        mangeLocalHistoryDao.deleteAllHistory()
    }

    suspend fun deleteSingleHistory(mangaHistoryDataModel: MangaHistoryDataModel) {
        mangeLocalHistoryDao.deleteSingle(mangaHistoryDataModel)
    }

    fun historySearchedWord() = searchedWordDao.loadWordHistory()

    suspend fun delKeyWordHistory(searchHistory: SearchHistory) =
        searchedWordDao.detectSearchedWordHistory(searchHistory)

    suspend fun upsertSearchWord(searchHistory: SearchHistory) {
        searchedWordDao.upsertWord(searchHistory)
    }

}