package com.shicheeng.copymanga.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.shicheeng.copymanga.pagingsource.NewestPagingSource
import com.shicheeng.copymanga.resposity.MangaNewestRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MangaNewestListViewModel @Inject constructor(
    newestRepository: MangaNewestRepository,
) : ViewModel() {
    val list = Pager(
        config = PagingConfig(pageSize = 21),
    ) {
        NewestPagingSource(newestRepository)
    }.flow.cachedIn(viewModelScope)


}