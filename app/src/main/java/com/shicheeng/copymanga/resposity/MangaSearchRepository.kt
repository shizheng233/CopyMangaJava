package com.shicheeng.copymanga.resposity

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.shicheeng.copymanga.data.search.SearchResultDataModel
import com.shicheeng.copymanga.domin.CopyMangaApi
import com.shicheeng.copymanga.pagingsource.SearchResultPagingSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MangaSearchRepository @Inject constructor(
    private val copyMangaApi: CopyMangaApi,
) {

    fun fetchSearchResult(query: String): Flow<PagingData<SearchResultDataModel>> {
        return Pager(
            config = PagingConfig(pageSize = 21),
            pagingSourceFactory = {
                SearchResultPagingSource(query, copyMangaApi)
            }
        ).flow
    }

}