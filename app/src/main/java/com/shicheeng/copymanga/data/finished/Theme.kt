package com.shicheeng.copymanga.data.finished


import com.squareup.moshi.Json
import androidx.annotation.Keep
import com.squareup.moshi.JsonClass

@Keep
@JsonClass(generateAdapter = true)
data class Theme(
    @Json(name = "name")
    val name: String,
    @Json(name = "path_word")
    val pathWord: String
)