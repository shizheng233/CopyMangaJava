package com.shicheeng.copymanga.resposity

import com.shicheeng.copymanga.domin.CopyMangaApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MangaNewestRepository @Inject constructor(
    private val copyMangaApi: CopyMangaApi,
) {

    suspend fun fetchNewestMangas(offset: Int) = copyMangaApi.getMangaNewest(offset = offset)

}