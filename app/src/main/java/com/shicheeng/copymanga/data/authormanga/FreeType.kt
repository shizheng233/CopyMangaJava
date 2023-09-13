package com.shicheeng.copymanga.data.authormanga


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import androidx.annotation.Keep

@Keep
@JsonClass(generateAdapter = true)
data class FreeType(
    @Json(name = "display")
    val display: String,
    @Json(name = "value")
    val value: Int
)