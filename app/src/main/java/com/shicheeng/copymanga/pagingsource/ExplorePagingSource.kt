package com.shicheeng.copymanga.pagingsource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.shicheeng.copymanga.data.ListBeanManga
import com.shicheeng.copymanga.json.MangaSortJson

class ExplorePagingSource(private val order: String?, private val themeWord: String?) :
    PagingSource<Int, ListBeanManga>() {

    override fun getRefreshKey(state: PagingState<Int, ListBeanManga>): Int? {
        return state.anchorPosition?.let {
            val anchorPage = state.closestPageToPosition(it)
            anchorPage?.nextKey?.minus(21) ?: anchorPage?.prevKey?.plus(21)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ListBeanManga> {
        return try {
            val offset = params.key ?: 0
            val data = MangaSortJson.getFilterData(offset, themeWord, order)
            LoadResult.Page(data, null, offset + 21)
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}