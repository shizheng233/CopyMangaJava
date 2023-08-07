package com.shicheeng.copymanga.resposity

import com.shicheeng.copymanga.domin.CopyMangaApi
import javax.inject.Inject

class MangaRecommendRepository @Inject constructor(
    private val copyMangaApi: CopyMangaApi,
) {

    suspend fun fetchRecommendMangas(offset: Int) = copyMangaApi.getMangaRecommend(offset = offset)

}