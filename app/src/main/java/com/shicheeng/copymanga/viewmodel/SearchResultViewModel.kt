package com.shicheeng.copymanga.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.shicheeng.copymanga.data.search.SearchResultDataModel
import com.shicheeng.copymanga.resposity.MangaSearchRepository
import com.shicheeng.copymanga.resposity.logD
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchResultViewModel @Inject constructor(
    private val repository: MangaSearchRepository,
) : ViewModel() {

    private val mutableQueryString = MutableStateFlow("")

    @OptIn(ExperimentalCoroutinesApi::class)
    val searchResult: Flow<PagingData<SearchResultDataModel>> = mutableQueryString.flatMapLatest {
        repository.fetchSearchResult(it)
    }.cachedIn(viewModelScope)


    fun loadSearch(word: String) = viewModelScope.launch {
        mutableQueryString.emit(word)
    }

}