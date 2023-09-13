package com.shicheeng.copymanga.data.authormanga


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import androidx.annotation.Keep

@Keep
@JsonClass(generateAdapter = true)
data class AuthorMangaItem(
    @Json(name = "author")
    val author: List<Author>,
    @Json(name = "cover")
    val cover: String,
    @Json(name = "datetime_updated")
    val datetimeUpdated: String,
    @Json(name = "females")
    val females: List<Any>,
    @Json(name = "free_type")
    val freeType: FreeType,
    @Json(name = "males")
    val males: List<Any>,
    @Json(name = "name")
    val name: String,
    @Json(name = "path_word")
    val pathWord: String,
    @Json(name = "popular")
    val popular: Int,
    @Json(name = "theme")
    val theme: List<Any>
)