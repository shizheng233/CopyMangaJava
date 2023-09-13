package com.shicheeng.copymanga.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shicheeng.copymanga.data.MangaHistoryDataModel
import com.shicheeng.copymanga.resposity.MangaHistoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val history: MangaHistoryRepository,
) : ViewModel() {

    val historyList = history.allHistoryDao.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = emptyList()
    )

    fun deleteHistory(mangaHistoryDataModel: MangaHistoryDataModel) = viewModelScope.launch {
        history.deleteSingleHistory(mangaHistoryDataModel)
    }

}