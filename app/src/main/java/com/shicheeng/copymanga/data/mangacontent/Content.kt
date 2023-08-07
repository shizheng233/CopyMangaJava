package com.shicheeng.copymanga.data.mangacontent


import com.squareup.moshi.Json
import androidx.annotation.Keep
import com.squareup.moshi.JsonClass

@Keep
@JsonClass(generateAdapter = true)
data class Content(
    @Json(name = "url")
    val url: String
)