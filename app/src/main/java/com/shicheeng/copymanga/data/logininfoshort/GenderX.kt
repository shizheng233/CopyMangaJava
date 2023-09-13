package com.shicheeng.copymanga.data.logininfoshort


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import androidx.annotation.Keep

@Keep
@JsonClass(generateAdapter = true)
data class GenderX(
    @Json(name = "display")
    val display: String,
    @Json(name = "value")
    val value: Int
)