package com.shicheeng.copymanga.data.webbookshelf


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import androidx.annotation.Keep

@Keep
@JsonClass(generateAdapter = true)
data class Browse(
    @Json(name = "chapter_name")
    val chapterName: String,
    @Json(name = "chapter_uuid")
    val chapterUuid: String,
    @Json(name = "comic_uuid")
    val comicUuid: String,
    @Json(name = "path_word")
    val pathWord: String
)