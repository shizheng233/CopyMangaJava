package com.shicheeng.copymanga.viewmodel

import androidx.lifecycle.*
import com.shicheeng.copymanga.data.MangaHistoryDataModel
import com.shicheeng.copymanga.data.MangaInfoChapterDataBean
import com.shicheeng.copymanga.data.PersonalInnerDataModel
import com.shicheeng.copymanga.fm.delegate.InfoDataDelegate
import com.shicheeng.copymanga.resposity.MangaHistoryRepository
import com.shicheeng.copymanga.util.FileUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DownloadViewModel(
    private val fileUtil: FileUtil,
    private val historyRepository: MangaHistoryRepository,
) : ViewModel() {

    private val listInDownload = MutableStateFlow<List<MangaInfoChapterDataBean>?>(null)
    private val historyData = MutableStateFlow<MangaHistoryDataModel?>(null)
    val listOfDownload = combine(listInDownload, historyData) { t1, t2 ->
        InfoDataDelegate.mapDownloadedChapters(t2, t1)
    }.asLiveData(viewModelScope.coroutineContext)
    val list = fileUtil.findDownloadManga()

    fun findDownloadedChapter(data: PersonalInnerDataModel) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            val list = fileUtil.findChaptersWithPathWord(data)
            val historyDataModel = if (data.pathWord == null) {
                null
            } else {
                historyRepository.getHistoryByMangaPathWord(data.pathWord)
            }
            listInDownload.tryEmit(list)
            historyData.tryEmit(historyDataModel)
        }
    }

    fun updateHistoryInInfo(data: PersonalInnerDataModel) = viewModelScope.launch {
        withContext(Dispatchers.Default) {
            val historyDataModel = if (data.pathWord == null) {
                null
            } else {
                historyRepository.getHistoryByMangaPathWord(data.pathWord)
            }
            historyData.tryEmit(historyDataModel)
        }
    }

}

class DownloadViewModelFactory(
    private val fileUtil: FileUtil,
    private val mangaHistoryRepository: MangaHistoryRepository,
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DownloadViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DownloadViewModel(fileUtil = fileUtil, mangaHistoryRepository) as T
        }
        throw IllegalArgumentException("UNKNOWN CLASS NAME!")
    }

}