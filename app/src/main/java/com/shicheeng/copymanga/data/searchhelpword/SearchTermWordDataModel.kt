package com.shicheeng.copymanga.data.searchhelpword


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import androidx.annotation.Keep

@Keep
@JsonClass(generateAdapter = true)
data class SearchTermWordDataModel(
    @Json(name = "code")
    val code: Int,
    @Json(name = "data")
    val `data`: List<String>,
    @Json(name = "msg")
    val msg: String
)