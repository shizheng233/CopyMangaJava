package com.shicheeng.copymanga.resposity

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.shicheeng.copymanga.data.authormanga.AuthorMangaItem
import com.shicheeng.copymanga.domin.CopyMangaApi
import com.shicheeng.copymanga.pagingsource.AuthorsMangaPagingSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthorsMangaRepository @Inject constructor(
    private val copyMangaApi: CopyMangaApi
) {
     fun fetchMangaByPathWord(pathWord:String): Flow<PagingData<AuthorMangaItem>> {
         return Pager(
             config = PagingConfig(pageSize = 1)
         ){
             AuthorsMangaPagingSource(pathWord, copyMangaApi)
         }.flow
     }
}