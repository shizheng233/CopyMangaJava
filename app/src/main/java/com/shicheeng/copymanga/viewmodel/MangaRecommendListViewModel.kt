package com.shicheeng.copymanga.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.shicheeng.copymanga.pagingsource.RecommendPagingSource
import com.shicheeng.copymanga.resposity.MangaRecommendRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MangaRecommendListViewModel @Inject constructor(
    repository: MangaRecommendRepository,
) : ViewModel() {

    val recommendMangaList = Pager(config = PagingConfig(pageSize = 21)) {
        RecommendPagingSource(repository)
    }.flow.cachedIn(viewModelScope)

}

