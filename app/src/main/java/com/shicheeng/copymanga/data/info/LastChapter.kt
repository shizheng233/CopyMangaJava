package com.shicheeng.copymanga.data.info


import com.squareup.moshi.Json
import androidx.annotation.Keep
import com.squareup.moshi.JsonClass

@Keep
@JsonClass(generateAdapter = true)
data class LastChapter(
    @Json(name = "name")
    val name: String,
    @Json(name = "uuid")
    val uuid: String
)