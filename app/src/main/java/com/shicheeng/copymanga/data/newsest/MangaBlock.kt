package com.shicheeng.copymanga.data.newsest


import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@Keep
@JsonClass(generateAdapter = true)
data class MangaBlock(
    @Json(name = "comic")
    val comic: Comic,
    @Json(name = "datetime_created")
    val datetimeCreated: String,
    @Json(name = "name")
    val name: String,
)