package com.shicheeng.copymanga.data.mangacontent


import com.squareup.moshi.Json
import androidx.annotation.Keep
import com.squareup.moshi.JsonClass

@Keep
@JsonClass(generateAdapter = true)
data class Comic(
    @Json(name = "name")
    val name: String,
    @Json(name = "path_word")
    val pathWord: String,
    @Json(name = "restrict")
    val restrict: Restrict,
    @Json(name = "uuid")
    val uuid: String
)