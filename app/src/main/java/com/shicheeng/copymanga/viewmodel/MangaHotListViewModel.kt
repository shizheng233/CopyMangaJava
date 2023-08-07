package com.shicheeng.copymanga.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.shicheeng.copymanga.pagingsource.HotPagingSource
import com.shicheeng.copymanga.resposity.MangaHotRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MangaHotListViewModel @Inject constructor(
    repository: MangaHotRepository,
) : ViewModel() {

    val hotMangaList = Pager(config = PagingConfig(pageSize = 21)) {
        HotPagingSource(repository)
    }.flow.cachedIn(viewModelScope)

}

