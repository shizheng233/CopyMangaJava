package com.shicheeng.copymanga.data.webbookshelf


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import androidx.annotation.Keep

@Keep
@JsonClass(generateAdapter = true)
data class Author(
    @Json(name = "name")
    val name: String,
    @Json(name = "path_word")
    val pathWord: String
)