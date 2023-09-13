package com.shicheeng.copymanga.resposity

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.shicheeng.copymanga.domin.CopyMangaApi
import com.shicheeng.copymanga.pagingsource.TopicDetailListPagingSource
import com.shicheeng.copymanga.util.UIState
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MangaTopicDetailRepository @Inject constructor(
    private val copyMangaApi: CopyMangaApi,
) {

    fun load(pathWord: String) = flow {
        emit(UIState.Loading)
        try {
            val data = copyMangaApi.getMangaTopicInfo(pathWord)
            emit(UIState.Success(data))
        } catch (e: Exception) {
            emit(UIState.Error(e))
        }
    }

    fun mangas(
        pathWord: String,
        type: Int,
    ) = Pager(
        config = PagingConfig(pageSize = 1)
    ) {
        TopicDetailListPagingSource(copyMangaApi, pathWord, type)
    }.flow


}