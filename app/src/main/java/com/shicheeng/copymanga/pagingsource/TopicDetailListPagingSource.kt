package com.shicheeng.copymanga.pagingsource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.shicheeng.copymanga.data.topiclist.TopicItem
import com.shicheeng.copymanga.domin.CopyMangaApi

class TopicDetailListPagingSource(
    private val copyMangaApi: CopyMangaApi,
    private val pathWord: String,
    private val type: Int,
) :
    PagingSource<Int, TopicItem>() {

    override fun getRefreshKey(state: PagingState<Int, TopicItem>): Int? {
        return null
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, TopicItem> {
        val offset = params.key ?: 0
        return try {
            val data = copyMangaApi.getMangaTopicList(
                offset = offset,
                type = type,
                name = pathWord
            )
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