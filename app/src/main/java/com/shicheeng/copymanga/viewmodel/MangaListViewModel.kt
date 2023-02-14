package com.shicheeng.copymanga.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.shicheeng.copymanga.pagingsource.FinishedPagingSource
import com.shicheeng.copymanga.pagingsource.HotPagingSource
import com.shicheeng.copymanga.pagingsource.NewestPagingSource
import com.shicheeng.copymanga.pagingsource.RecommendPagingSource

class MangaListViewModel : ViewModel() {


    val pageMangaRecommendFlow = Pager(PagingConfig(21)) {
        RecommendPagingSource()
    }.flow.cachedIn(viewModelScope)

    val pageMangaNewestFlow = Pager(PagingConfig(pageSize = 21)) {
        NewestPagingSource()
    }.flow.cachedIn(viewModelScope)

    val pageFinishedFlow = Pager(PagingConfig(pageSize = 21)) {
        FinishedPagingSource()
    }.flow.cachedIn(viewModelScope)

    val pageHotFlow = Pager(PagingConfig(pageSize = 21)){
        HotPagingSource()
    }.flow.cachedIn(viewModelScope)

}

