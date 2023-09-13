package com.shicheeng.copymanga.data.logininfoshort


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import androidx.annotation.Keep

@Keep
@JsonClass(generateAdapter = true)
data class Gender(
    @Json(name = "key")
    val key: Int,
    @Json(name = "value")
    val value: String
)