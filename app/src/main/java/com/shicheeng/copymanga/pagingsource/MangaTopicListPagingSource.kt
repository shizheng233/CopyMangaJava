package com.shicheeng.copymanga.pagingsource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.shicheeng.copymanga.data.topicalllist.TopicAllListItem
import com.shicheeng.copymanga.domin.CopyMangaApi

class MangaTopicListPagingSource(
    private val copyMangaApi: CopyMangaApi,
) : PagingSource<Int, TopicAllListItem>() {

    override fun getRefreshKey(state: PagingState<Int, TopicAllListItem>): Int? {
        return null
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, TopicAllListItem> {
        val offset = params.key ?: 0
        return try {
            val data = copyMangaApi.fetchAllTopicListItem(offset = offset)
            LoadResult.Page(
                data = data.results.list,
                prevKey = null,
                nextKey = if (data.results.offset >= data.results.total) null else offset + 21
            )
        } catch (e: Exception) {
            e.printStackTrace()
            LoadResult.Error(e)
        }
    }
}