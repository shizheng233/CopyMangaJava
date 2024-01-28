package com.shicheeng.copymanga.resposity

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.shicheeng.copymanga.domin.CopyMangaApi
import com.shicheeng.copymanga.pagingsource.ComicCommentPagingSource
import com.shicheeng.copymanga.util.SendUIState
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ComicCommentRepository @Inject constructor(
    private val copyMangaApi: CopyMangaApi,
) {

    fun loadComment(uuid: String) = Pager(
        config = PagingConfig(1)
    ) {
        ComicCommentPagingSource(uuid, copyMangaApi)
    }.flow

    fun push(comic: String, comment: String) = flow {
        emit(SendUIState.Loading)
        try {
            val data = copyMangaApi.commentPush(comic, comment)
            emit(SendUIState.Success(data))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(SendUIState.Error(e))
        } finally {
            kotlinx.coroutines.delay(3000)
            emit(SendUIState.Idle)
        }
    }

}