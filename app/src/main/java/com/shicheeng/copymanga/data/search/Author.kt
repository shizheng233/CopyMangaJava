package com.shicheeng.copymanga.data.search


import com.squareup.moshi.Json
import androidx.annotation.Keep
import com.squareup.moshi.JsonClass

@Keep
@JsonClass(generateAdapter = true)
data class Author(
    @Json(name = "alias")
    val alias: String?,
    @Json(name = "name")
    val name: String,
    @Json(name = "path_word")
    val pathWord: String
)