package com.shicheeng.copymanga.data.logininfoshort


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import androidx.annotation.Keep

@Keep
@JsonClass(generateAdapter = true)
data class Results(
    @Json(name = "genders")
    val genders: List<Gender>,
    @Json(name = "info")
    val info: Info
)