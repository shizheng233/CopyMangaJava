package com.shicheeng.copymanga.data.topicinfo


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import androidx.annotation.Keep

@Keep
@JsonClass(generateAdapter = true)
data class Last(
    @Json(name = "path_word")
    val pathWord: String,
    @Json(name = "title")
    val title: String
)