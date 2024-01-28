package com.shicheeng.copymanga.domin

import androidx.annotation.Keep
import com.shicheeng.copymanga.data.authormanga.AuthorsMangaDataModel
import com.shicheeng.copymanga.data.chapter.ChapterDataModel
import com.shicheeng.copymanga.data.commentpush.CommentPushDataModel
import com.shicheeng.copymanga.data.finished.FinishedMangaDataModel
import com.shicheeng.copymanga.data.info.MangaInfoDataModel
import com.shicheeng.copymanga.data.lofininfo.LoginInfoDataModel
import com.shicheeng.copymanga.data.login.LoginDataModel
import com.shicheeng.copymanga.data.logininfoshort.LoginInfoShortDataModel
import com.shicheeng.copymanga.data.mangacomment.MangaCommentDataModel
import com.shicheeng.copymanga.data.mangacontent.MangaContentDataModel
import com.shicheeng.copymanga.data.newsest.NewestListDataModel
import com.shicheeng.copymanga.data.rank.RankDataModel
import com.shicheeng.copymanga.data.recommend.RecommendDataModel
import com.shicheeng.copymanga.data.search.SearchDataModel
import com.shicheeng.copymanga.data.topicalllist.TopicAllListDataModel
import com.shicheeng.copymanga.data.topicinfo.TopicInfoDataModelX
import com.shicheeng.copymanga.data.topiclist.TopicListDataModel
import com.shicheeng.copymanga.data.webbookshelf.WebBookshelf
import com.shicheeng.copymanga.data.webcomichistory.WebComicHistory
import com.shicheeng.copymanga.data.webhistory.WebHistoryDataModel
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
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
        @Query("free_type") freeType: Int = 1,
        @Query("limit") limit: Int = 21,
        @Query("offset") offset: Int,
        @Query("top") top: String? = null,
        @Query("theme") theme: String? = null,
        @Query("ordering", encoded = true) ordering: String? = null,
        @Query("_update") update: Boolean = true,
    ): FinishedMangaDataModel

    @GET("/api/v3/comic/{path_word}/group/default/chapters")
    suspend fun fetchChapters(
        @Path("path_word") pathWord: String,
        @Query("limit") limit: Int = 500,
        @Query("offset") offset: Int = 0,
        @Query("platform") platform: Int = 1,
    ): ChapterDataModel

    @GET("/api/v3/search/comic")
    suspend fun search(
        @Query("format") format: String = "json",
        @Query("limit") limit: Int = 21,
        @Query("offset") offset: Int,
        @Query("platform") platform: Int = 1,
        @Query("q") q: String,
    ): SearchDataModel

    @GET("/api/v3/comic/{path_word}/chapter2/{uuid}")
    suspend fun fetchMangaContentPicture(
        @Path("path_word") pathWord: String,
        @Path("uuid") uuid: String,
        @Query("platform") platform: Int = 1,
    ): MangaContentDataModel

    @GET("/api/v3/topic/{name}")
    suspend fun getMangaTopicInfo(
        @Path("name") name: String,
        @Query("platform") platform: Int = 1,
    ): TopicInfoDataModelX

    @GET("/api/v3/topic/{name}/contents")
    suspend fun getMangaTopicList(
        @Path("name") name: String,
        @Query("type") type: Int,
        @Query("limit") limit: Int = 21,
        @Query("offset") offset: Int,
        @Query("platform") platform: Int = 1,
    ): TopicListDataModel

    @GET("/api/v3/topics")
    suspend fun fetchAllTopicListItem(
        @Query("type") type: Int = 1,
        @Query("limit") limit: Int = 21,
        @Query("offset") offset: Int,
        @Query("_update") update: Boolean = true,
    ): TopicAllListDataModel

    @POST("/api/v3/login")
    @FormUrlEncoded
    suspend fun login(
        @Field("username") username: String,
        @Field("password") passwordB64: String,
        @Field("salt") salt: Int,
        @Field("source") source: String = "freeSite",
        @Field("version") version: String = "2023.08.14",
        @Field("platform") platform: Int = 1,
    ): LoginDataModel

    @GET("/api/v3/member/browse/comics")
    suspend fun browsedComics(
        @Query("free_type") freeType: Int = 1,
        @Query("offset") offset: Int,
        @Query("limit") limit: Int = 20,
        @Query("_update") update: Boolean = true,
    ): WebHistoryDataModel

    @GET("/api/v3/member/collect/comics")
    suspend fun bookshelfWeb(
        @Query("free_type") freeType: Int = 1,
        @Query("limit") limit: Int = 21,
        @Query("offset") offset: Int,
        @Query("_update") update: Boolean = true,
        @Query("ordering") ordering: String = "-datetime_modifier",
    ): WebBookshelf


    @GET("/api/v3/member/update/info")
    suspend fun shortInfo(
        @Query("nickname") nickname: String = "",
        @Query("avatar") avatar: String = "",
        @Query("gender") gender: String = "",
        @Query("birthday") birthday: String = "",
    ): LoginInfoShortDataModel

    @GET("/api/v3/comic2/{word}/query")
    suspend fun comicWebHistory(
        @Path("word") word: String,
        @Query("platform") platform: Int = 1,
        @Query("_update") update: Boolean = true,
    ): WebComicHistory

    @GET("/api/v3/comics")
    suspend fun comicAuthors(
        @Query("free_type") freeType: Int = 1,
        @Query("author") author: String,
        @Query("limit") limit: Int = 100,
        @Query("offset") offset: Int,
        @Query("ordering") ordering: String = "-datetime_updated",
    ): AuthorsMangaDataModel

    @GET("/api/v3/comments")
    suspend fun comicComments(
        @Query("comic_id") comicID: String,
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int,
    ): MangaCommentDataModel

    @GET("/api/v3/member/info")
    suspend fun loginInfo(): LoginInfoDataModel

    @FormUrlEncoded
    @POST("/api/v3/member/collect/comic")
    suspend fun comicCollect(
        @Field("comic_id") comicID: String,
        @Field("is_collect") isCollect: Int,
        @Field("_update") update: Boolean = true,
    )

    @FormUrlEncoded
    @POST("/api/v3/member/comment")
    suspend fun commentPush(
        @Field("comic_id") comicId: String,
        @Field("comment") comment: String,
        @Field("reply_id") replyId: String = "",
    ): CommentPushDataModel

}

