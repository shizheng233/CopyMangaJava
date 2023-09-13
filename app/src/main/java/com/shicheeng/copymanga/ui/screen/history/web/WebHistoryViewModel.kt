package com.shicheeng.copymanga.ui.screen.history.web

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.shicheeng.copymanga.resposity.WebHistoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class WebHistoryViewModel @Inject constructor(
    webHistoryRepository: WebHistoryRepository,
) : ViewModel() {

    val list = webHistoryRepository.historyOnWeb().cachedIn(viewModelScope)

}