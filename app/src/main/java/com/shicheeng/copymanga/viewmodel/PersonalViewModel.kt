package com.shicheeng.copymanga.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.shicheeng.copymanga.R
import com.shicheeng.copymanga.data.PersonalDataModel
import com.shicheeng.copymanga.resposity.MangaHistoryRepository
import com.shicheeng.copymanga.util.FileUtil
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


class PersonalViewModel @AssistedInject constructor(
    @Assisted private val fileUtil: FileUtil,
    historyRepository: MangaHistoryRepository,
) : ViewModel() {

    private var downloadMangaList = fileUtil.findDownloadManga()
    private val historyList = historyRepository.allHistoryDao

    val combineOfList = combine(downloadMangaList, historyList) { download, history ->
        listOf(
            PersonalDataModel(R.string.history, history),
            PersonalDataModel(R.string.download_manga, download)
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = emptyList()
    )


    fun updateDownloadList() = viewModelScope.launch {
        downloadMangaList = fileUtil.findDownloadManga()
    }

    @AssistedFactory
    interface Factory {
        fun create(
            fileUtil: FileUtil,
        ): PersonalViewModel
    }

    companion object {
        fun provideFactory(
            assistedFactory: Factory,
            fileUtil: FileUtil,
        ) = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return assistedFactory.create(fileUtil) as T
            }
        }
    }

}

