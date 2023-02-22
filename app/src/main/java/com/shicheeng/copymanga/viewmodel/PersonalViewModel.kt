package com.shicheeng.copymanga.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.shicheeng.copymanga.R
import com.shicheeng.copymanga.data.PersonalDataModel
import com.shicheeng.copymanga.resposity.MangaHistoryRepository
import com.shicheeng.copymanga.util.FileUtil
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class PersonalViewModel(
    private val fileUtil: FileUtil,
    historyRepository: MangaHistoryRepository,
) : ViewModel() {

    private var downloadMangaList = fileUtil.findDownloadManga()
    private val historyList = historyRepository.allHistoryDao

    val combineOfList = combine(downloadMangaList, historyList) { download, history ->
        listOf(
            PersonalDataModel(R.string.history, history),
            PersonalDataModel(R.string.download_manga, download)
        )
    }.asLiveData(viewModelScope.coroutineContext)

    fun updateDownloadList() = viewModelScope.launch {
        downloadMangaList = fileUtil.findDownloadManga()
    }

}

class PersonalViewModelFactory(
    private val fileUtil: FileUtil,
    private val historyRepository: MangaHistoryRepository,
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PersonalViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PersonalViewModel(fileUtil, historyRepository) as T
        }
        throw IllegalArgumentException("ERROR VIEW MODEL CLASS")
    }
}