package com.shicheeng.copymanga.pagingsource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.shicheeng.copymanga.data.rank.Item
import com.shicheeng.copymanga.domin.CopyMangaApi

class RankPagingSource(
    private val copyMangaApi: CopyMangaApi,
    private val rankType: String,
) : PagingSource<Int, Item>() {

    override fun getRefreshKey(state: PagingState<Int, Item>): Int? {
        return null
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Item> {
        return try {
            val nextPageKey = params.key ?: 0
            val ranks = copyMangaApi.getRank(offset = nextPageKey, dateType = rankType)
            if (nextPageKey <= ranks.results.total) {
                LoadResult.Page(ranks.results.list, prevKey = null, nextKey = nextPageKey + 21)
            } else {
                LoadResult.Page(emptyList(), prevKey = null, nextKey = null)
            }
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

}