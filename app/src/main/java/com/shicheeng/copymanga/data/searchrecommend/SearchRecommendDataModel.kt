package com.shicheeng.copymanga.data.searchrecommend


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import androidx.annotation.Keep

@Keep
@JsonClass(generateAdapter = true)
data class SearchRecommendDataModel(
    @Json(name = "code")
    val code: Int,
    @Json(name = "data")
    val `data`: List<Data>,
    @Json(name = "msg")
    val msg: String
)