package com.shicheeng.copymanga.pagingsource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.shicheeng.copymanga.data.authormanga.AuthorMangaItem
import com.shicheeng.copymanga.domin.CopyMangaApi
import kotlin.coroutines.Continuation

class AuthorsMangaPagingSource(
    private val pathWord: String,
    private val copyMangaApi: CopyMangaApi,
) : PagingSource<Int, AuthorMangaItem>() {
    override fun getRefreshKey(state: PagingState<Int, AuthorMangaItem>): Int? {
        return null
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, AuthorMangaItem> {
        val offset = params.key ?: 0

        return try {
            val data = copyMangaApi.comicAuthors(author = pathWord, offset = offset)
            LoadResult.Page(
                data = data.results.list,
                nextKey = if (data.results.offset > data.results.total) null else offset + 21,
                prevKey = null
            )
        } catch (e: Exception) {
            e.printStackTrace()
            LoadResult.Error(e)
        }
    }
}