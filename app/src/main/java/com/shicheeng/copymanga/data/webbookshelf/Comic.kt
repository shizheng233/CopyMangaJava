package com.shicheeng.copymanga.data.webbookshelf


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import androidx.annotation.Keep

@Keep
@JsonClass(generateAdapter = true)
data class Comic(
    @Json(name = "author")
    val author: List<Author>,
    @Json(name = "b_display")
    val bDisplay: Boolean,
    @Json(name = "browse")
    val browse: Browse?,
    @Json(name = "cover")
    val cover: String,
    @Json(name = "datetime_updated")
    val datetimeUpdated: String,
    @Json(name = "females")
    val females: List<Any>,
    @Json(name = "last_chapter_id")
    val lastChapterId: String,
    @Json(name = "last_chapter_name")
    val lastChapterName: String,
    @Json(name = "males")
    val males: List<Any>,
    @Json(name = "name")
    val name: String,
    @Json(name = "path_word")
    val pathWord: String,
    @Json(name = "popular")
    val popular: Int,
    @Json(name = "status")
    val status: Int,
    @Json(name = "theme")
    val theme: List<Any>,
    @Json(name = "uuid")
    val uuid: String
)