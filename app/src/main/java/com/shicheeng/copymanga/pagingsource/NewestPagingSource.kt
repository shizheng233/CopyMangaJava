package com.shicheeng.copymanga.pagingsource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.shicheeng.copymanga.data.newsest.MangaBlock
import com.shicheeng.copymanga.resposity.MangaNewestRepository

class NewestPagingSource(
    private val repository: MangaNewestRepository,
) : PagingSource<Int, MangaBlock>() {

    override fun getRefreshKey(state: PagingState<Int, MangaBlock>): Int? {
        return null
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MangaBlock> {
        return try {
            val offset = params.key ?: 0
            val data = repository.fetchNewestMangas(offset)
            if (offset <= data.results.total) {
                LoadResult.Page(data.results.list, null, offset + 21)
            } else {
                LoadResult.Page(data.results.list, null, null)
            }
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}