package com.shicheeng.copymanga.pagingsource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.shicheeng.copymanga.data.finished.Item
import com.shicheeng.copymanga.domin.CopyMangaApi
import com.shicheeng.copymanga.json.MangaSortJson

class FinishedPagingSource(
    private val copyMangaApi: CopyMangaApi,
) : PagingSource<Int, Item>() {

    override fun getRefreshKey(state: PagingState<Int, Item>): Int? {
        return null
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Item> {
        return try {
            val offset = params.key ?: 0
            val data = copyMangaApi.fetchMangaFilter(
                offset = offset,
                top = MangaSortJson.topPath.find { x ->
                    x.pathName == "已完结"
                }?.pathWord
            )
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