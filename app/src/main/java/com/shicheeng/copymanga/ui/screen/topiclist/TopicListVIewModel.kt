package com.shicheeng.copymanga.ui.screen.topiclist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.shicheeng.copymanga.domin.CopyMangaApi
import com.shicheeng.copymanga.pagingsource.MangaTopicListPagingSource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TopicListVIewModel @Inject constructor(
    copyMangaApi: CopyMangaApi,
) : ViewModel() {

    val list = Pager(config = PagingConfig(pageSize = 1)) {
        MangaTopicListPagingSource(copyMangaApi)
    }.flow.cachedIn(viewModelScope)

}