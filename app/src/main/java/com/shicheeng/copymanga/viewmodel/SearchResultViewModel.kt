package com.shicheeng.copymanga.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.shicheeng.copymanga.pagingsource.SearchResultPagingSource

class SearchResultViewModel : ViewModel() {



    fun loadSearch(word: String) = Pager(PagingConfig(21)) {
        SearchResultPagingSource(word)
    }.flow.cachedIn(viewModelScope)

}