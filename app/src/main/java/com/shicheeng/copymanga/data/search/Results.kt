package com.shicheeng.copymanga.data.search


import com.squareup.moshi.Json
import androidx.annotation.Keep
import com.squareup.moshi.JsonClass

@Keep
@JsonClass(generateAdapter = true)
data class Results(
    @Json(name = "limit")
    val limit: Int,
    @Json(name = "list")
    val list: List<SearchResultDataModel>,
    @Json(name = "offset")
    val offset: Int,
    @Json(name = "total")
    val total: Int
)