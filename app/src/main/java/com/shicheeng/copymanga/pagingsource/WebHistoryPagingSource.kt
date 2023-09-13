package com.shicheeng.copymanga.pagingsource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.shicheeng.copymanga.data.webhistory.WebHistoryItem
import com.shicheeng.copymanga.domin.CopyMangaApi

class WebHistoryPagingSource(private val copyMangaApi: CopyMangaApi) :
    PagingSource<Int, WebHistoryItem>() {

    override fun getRefreshKey(state: PagingState<Int, WebHistoryItem>): Int? {
        return null
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, WebHistoryItem> {
        val offset = params.key ?: 0
        return try {
            val data = copyMangaApi.browsedComics(offset = offset)
            return LoadResult.Page(
                data = data.results.list,
                prevKey = null,
                nextKey = if (data.results.offset > data.results.total) null else offset + 21
            )
        } catch (e: Exception) {
            e.printStackTrace()
            LoadResult.Error(e)
        }
    }
}