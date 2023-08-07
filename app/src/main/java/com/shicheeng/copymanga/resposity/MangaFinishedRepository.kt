package com.shicheeng.copymanga.resposity

import com.shicheeng.copymanga.domin.CopyMangaApi
import com.shicheeng.copymanga.json.MangaSortJson
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MangaFinishedRepository @Inject constructor(
    private val copyMangaApi: CopyMangaApi,
) {

    suspend fun fetchFinishManga(offset: Int) = copyMangaApi.fetchMangaFilter(
        top = MangaSortJson.topPath.find { x -> x.pathName == "已完结" }?.pathWord,
        offset = offset
    )

}