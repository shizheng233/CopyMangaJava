package com.shicheeng.copymanga.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.shicheeng.copymanga.pagingsource.RankPagingSource

class RankViewModel : ViewModel() {

    fun loadRank(rankType: String) = Pager(PagingConfig(pageSize = 21)) {
        RankPagingSource(rankType)
    }.flow.cachedIn(viewModelScope)

}