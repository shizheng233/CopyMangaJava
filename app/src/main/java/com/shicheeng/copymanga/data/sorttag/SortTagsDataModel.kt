package com.shicheeng.copymanga.data.sorttag


import com.squareup.moshi.Json
import androidx.annotation.Keep

@Keep
data class SortTagsDataModel(
    @Json(name = "code")
    val code: Int,
    @Json(name = "message")
    val message: String,
    @Json(name = "results")
    val results: Results
)