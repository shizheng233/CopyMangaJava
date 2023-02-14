package com.shicheeng.copymanga.pagingsource

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.shicheeng.copymanga.data.ListBeanManga
import com.shicheeng.copymanga.error.EmptyJsonArray
import com.shicheeng.copymanga.json.SearchMangaJson

class SearchResultPagingSource(private val word: String) : PagingSource<Int, ListBeanManga>() {

    override fun getRefreshKey(state: PagingState<Int, ListBeanManga>): Int? {
        return state.anchorPosition?.let {
            val anchorPage = state.closestPageToPosition(it)
            anchorPage?.nextKey?.minus(21) ?: anchorPage?.prevKey?.plus(21)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ListBeanManga> {
        return try {
            val nextPageNumber = params.key ?: 0
            val responseData = SearchMangaJson.toGetManga(word, nextPageNumber)
            Log.d("TAG", "load: next number is $nextPageNumber")
            LoadResult.Page(data = responseData, prevKey = null, nextKey = nextPageNumber + 21)
        } catch (e: Exception) {
            Log.e("TAG", "load: Error $e")
            LoadResult.Error(e)
        } catch (e: EmptyJsonArray) {
            LoadResult.Invalid()
        }
    }

}