package com.shicheeng.copymanga.data.topiclist


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import androidx.annotation.Keep

@Keep
@JsonClass(generateAdapter = true)
data class TopicItem(
    @Json(name = "author")
    val author: List<Author>,
    @Json(name = "c_type")
    val cType: Int,
    @Json(name = "cover")
    val cover: String,
    @Json(name = "females")
    val females: List<Any>,
    @Json(name = "img_type")
    val imgType: Int,
    @Json(name = "males")
    val males: List<Any>,
    @Json(name = "name")
    val name: String,
    @Json(name = "parodies")
    val parodies: List<Any>,
    @Json(name = "path_word")
    val pathWord: String,
    @Json(name = "popular")
    val popular: Int,
    @Json(name = "theme")
    val theme: List<Theme>,
    @Json(name = "type")
    val type: Int
)