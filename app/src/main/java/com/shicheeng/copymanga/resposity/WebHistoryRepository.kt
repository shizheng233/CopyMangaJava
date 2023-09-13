package com.shicheeng.copymanga.resposity

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.shicheeng.copymanga.domin.CopyMangaApi
import com.shicheeng.copymanga.pagingsource.WebHistoryPagingSource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WebHistoryRepository @Inject constructor(
    private val copyMangaApi: CopyMangaApi,
) {

    fun historyOnWeb() = Pager(
        config = PagingConfig(pageSize = 1)
    ) {
        WebHistoryPagingSource(copyMangaApi)
    }.flow

}