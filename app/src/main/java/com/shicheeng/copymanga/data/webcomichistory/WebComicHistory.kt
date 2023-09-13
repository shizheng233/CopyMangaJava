package com.shicheeng.copymanga.data.webcomichistory


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import androidx.annotation.Keep
import com.shicheeng.copymanga.data.local.LocalChapter

@Keep
@JsonClass(generateAdapter = true)
data class WebComicHistory(
    @Json(name = "code")
    val code: Int,
    @Json(name = "message")
    val message: String,
    @Json(name = "results")
    val results: Results
)