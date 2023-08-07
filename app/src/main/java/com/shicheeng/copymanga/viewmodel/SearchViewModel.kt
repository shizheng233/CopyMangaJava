package com.shicheeng.copymanga.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shicheeng.copymanga.data.searchhistory.SearchHistory
import com.shicheeng.copymanga.resposity.MangaHistoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val historyRepository: MangaHistoryRepository,
) : ViewModel() {

    private var _searchWord = MutableStateFlow("")

    @OptIn(ExperimentalCoroutinesApi::class)
    val searchedHistoryWord = combine(
        _searchWord,
        historyRepository.historySearchedWord()
    ) { s, list ->
        if (s.isBlank()) {
            list
        } else {
            list.filter { x -> x.word.contains(s) }
        }
    }.mapLatest {
        it.map { x -> x.word }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = emptyList()
    )

    fun upWord(wd: String) {
        _searchWord.tryEmit(wd)
    }


    fun saveSearchWord(word: String) = viewModelScope.launch {
        historyRepository.upsertSearchWord(
            SearchHistory(
                word = word,
                time = System.currentTimeMillis()
            )
        )
    }

}