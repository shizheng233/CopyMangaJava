package com.shicheeng.copymanga.domin

import androidx.annotation.Keep
import com.shicheeng.copymanga.data.chapter.ChapterDataModel
import com.shicheeng.copymanga.data.finished.FinishedMangaDataModel
import com.shicheeng.copymanga.data.info.MangaInfoDataModel
import com.shicheeng.copymanga.data.mangacontent.MangaContentDataModel
import com.shicheeng.copymanga.data.newsest.NewestListDataModel
import com.shicheeng.copymanga.data.rank.RankDataModel
import com.shicheeng.copymanga.data.recommend.RecommendDataModel
import com.shicheeng.copymanga.data.search.SearchDataModel
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

@Keep
interface CopyMangaApi {

    @GET("/api/v3/ranks")
    suspend fun getRank(
        @Query("limit")
        limit: Int = 21,
        @Query("offset")
        offset: Int,
        @Query("date_type")
        dateType: String,
    ): RankDataModel

    @GET("/api/v3/comic2/{path_word}")
    suspend fun getMangaInfo(
        @Path("path_word") pathWord: String,
        @Query("platform") platform: Int = 3,
        @Query("format") format: String = "json",
    ): MangaInfoDataModel

    @GET("/api/v3/recs")
    suspend fun getMangaRecommend(
        @Query("pos") pos: Int = 3200102,
        @Query("limit") limit: Int = 21,
        @Query("offset") offset: Int,
    ): RecommendDataModel

    @GET("/api/v3/update/newest")
    suspend fun getMangaNewest(
        @Query("limit") limit: Int = 21,
        @Query("offset") offset: Int,
    ): NewestListDataModel

    @GET("/api/v3/comics")
    suspend fun fetchMangaFilter(
        @Query("limit") limit: Int = 21,
        @Query("offset") offset: Int,
        @Query("top") top: String? = null,
        @Query("theme") theme: String? = null,
        @Query("ordering") ordering: String? = null,
    ): FinishedMangaDataModel

    @GET("/api/v3/comic/{path_word}/group/default/chapters")
    suspend fun fetchChapters(
        @Path("path_word") pathWord: String,
        @Query("limit") limit: Int = 500,
        @Query("offset") offset: Int = 0,
        @Query("platform") platform: Int = 3,
    ): ChapterDataModel

    @GET("/api/v3/search/comic")
    suspend fun search(
        @Query("format") format: String = "json",
        @Query("limit") limit: Int = 21,
        @Query("offset") offset: Int,
        @Query("platform") platform: Int = 3,
        @Query("q") q: String,
    ): SearchDataModel

    @GET("/api/v3/comic/{path_word}/chapter2/{uuid}")
    suspend fun fetchMangaContentPicture(
        @Path("path_word") pathWord: String,
        @Path("uuid") uuid: String,
        @Query("platform") platform: Int = 3,
    ): MangaContentDataModel


}

