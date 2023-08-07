package com.shicheeng.copymanga.pagingsource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.shicheeng.copymanga.data.search.SearchResultDataModel
import com.shicheeng.copymanga.domin.CopyMangaApi

class SearchResultPagingSource(
    private val word: String,
    private val copyMangaApi: CopyMangaApi,
) : PagingSource<Int, SearchResultDataModel>() {

    override fun getRefreshKey(state: PagingState<Int, SearchResultDataModel>): Int? {
        return null
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, SearchResultDataModel> {
        return try {
            val nextPageNumber = params.key ?: 0
            val responseData = copyMangaApi.search(q = word, offset = nextPageNumber)
            if (nextPageNumber <= responseData.results.total) {
                LoadResult.Page(
                    data = responseData.results.list,
                    prevKey = null,
                    nextKey = nextPageNumber + 21
                )
            } else {
                LoadResult.Page(data = responseData.results.list, prevKey = null, nextKey = null)
            }
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

}