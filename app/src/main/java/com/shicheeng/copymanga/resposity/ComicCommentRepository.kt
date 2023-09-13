package com.shicheeng.copymanga.resposity

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.shicheeng.copymanga.domin.CopyMangaApi
import com.shicheeng.copymanga.pagingsource.ComicCommentPagingSource
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

}