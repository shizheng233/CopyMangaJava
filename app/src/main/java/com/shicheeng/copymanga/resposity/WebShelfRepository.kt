package com.shicheeng.copymanga.resposity

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.shicheeng.copymanga.domin.CopyMangaApi
import com.shicheeng.copymanga.pagingsource.WebShelfPagingSource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WebShelfRepository @Inject constructor(
    private val copyMangaApi: CopyMangaApi,
) {

    fun loadWebShelf() = Pager(
        pagingSourceFactory = {
            WebShelfPagingSource(copyMangaApi)
        },
        config = PagingConfig(pageSize = 1)
    ).flow

}