package com.shicheeng.copymanga.data.searchrecommend


import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@Keep
@JsonClass(generateAdapter = true)
data class Data(
    @Json(name = "title")
    val title: String,
)