package com.shicheeng.copymanga.data.topicinfo


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import androidx.annotation.Keep

@Keep
@JsonClass(generateAdapter = true)
data class Series(
    @Json(name = "color")
    val color: String,
    @Json(name = "name")
    val name: String,
    @Json(name = "path_word")
    val pathWord: String
)