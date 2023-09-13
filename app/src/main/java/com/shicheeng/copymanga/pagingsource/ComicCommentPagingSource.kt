package com.shicheeng.copymanga.pagingsource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.shicheeng.copymanga.data.mangacomment.MangaCommentListItem
import com.shicheeng.copymanga.domin.CopyMangaApi

class ComicCommentPagingSource(
    private val uuid: String,
    private val copyMangaApi: CopyMangaApi,
) : PagingSource<Int, MangaCommentListItem>() {

    override fun getRefreshKey(state: PagingState<Int, MangaCommentListItem>): Int? {
        return null
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MangaCommentListItem> {
        val offset = params.key ?: 0
        return try {
            val model = copyMangaApi.comicComments(comicID = uuid, offset = offset)
            LoadResult.Page(
                data = model.results.list,
                prevKey = null,
                nextKey = if (model.results.offset > model.results.total) null else offset + 20
            )
        } catch (e: Exception) {
            e.printStackTrace()
            LoadResult.Error(e)
        }
    }
}