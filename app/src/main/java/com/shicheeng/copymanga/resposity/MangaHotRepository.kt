package com.shicheeng.copymanga.resposity

import com.shicheeng.copymanga.domin.CopyMangaApi
import com.shicheeng.copymanga.json.MangaSortJson
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MangaHotRepository @Inject constructor(
    private val copyMangaApi: CopyMangaApi,
) {

    suspend fun fetchHotMangas(offset: Int) = copyMangaApi.fetchMangaFilter(
        ordering = MangaSortJson.order.find { x -> x.pathName == "最热" }?.pathWord,
        offset = offset
    )

}