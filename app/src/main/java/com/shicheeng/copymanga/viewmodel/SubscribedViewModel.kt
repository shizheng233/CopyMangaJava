package com.shicheeng.copymanga.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shicheeng.copymanga.resposity.MangaHistoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class SubscribedViewModel @Inject constructor(
    private val repository: MangaHistoryRepository,
) : ViewModel() {

    val data = repository.allHistoryDao.map { mangaHistoryDataModels ->
        mangaHistoryDataModels.filter { it.isSubscribe }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = emptyList()
    )

}