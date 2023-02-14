package com.shicheeng.copymanga.pagingsource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.shicheeng.copymanga.data.ListBeanManga
import com.shicheeng.copymanga.json.MangaRankJson

class RankPagingSource(private val rankType: String) : PagingSource<Int, ListBeanManga>() {

    override fun getRefreshKey(state: PagingState<Int, ListBeanManga>): Int? {
        return state.anchorPosition?.let {
            val anchorPage = state.closestPageToPosition(it)
            anchorPage?.nextKey?.minus(21) ?: anchorPage?.prevKey?.plus(21)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ListBeanManga> {
        return try {
            val nextPageKey = params.key ?: 0
            val ranks = MangaRankJson.rankGet(nextPageKey, rankType)
            LoadResult.Page(ranks, prevKey = null, nextKey = nextPageKey + 21)
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

}