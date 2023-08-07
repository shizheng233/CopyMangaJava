package com.shicheeng.copymanga.pagingsource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.shicheeng.copymanga.data.recommend.RecommendDataModel
import com.shicheeng.copymanga.resposity.MangaRecommendRepository

class RecommendPagingSource(
    private val recommendRepository: MangaRecommendRepository,
) : PagingSource<Int, RecommendDataModel.Results.Item>() {

    override fun getRefreshKey(state: PagingState<Int, RecommendDataModel.Results.Item>): Int? {
        return null
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, RecommendDataModel.Results.Item> {
        return try {
            val nextPageNumber = params.key ?: 0
            val responseData = recommendRepository.fetchRecommendMangas(nextPageNumber)
            if (nextPageNumber <= responseData.results.total) {
                LoadResult.Page(
                    data = responseData.results.list,
                    prevKey = null,
                    nextKey = nextPageNumber + 21
                )
            } else {
                LoadResult.Page(data = emptyList(), prevKey = null, nextKey = null)
            }
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

}