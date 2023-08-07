package com.shicheeng.copymanga.data.sorttag


import com.squareup.moshi.Json
import androidx.annotation.Keep

@Keep
data class Ordering(
    @Json(name = "datetime_updated")
    val datetimeUpdated: String,
    @Json(name = "popular")
    val popular: String
)