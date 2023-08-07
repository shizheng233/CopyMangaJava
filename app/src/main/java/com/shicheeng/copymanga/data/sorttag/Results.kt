package com.shicheeng.copymanga.data.sorttag


import androidx.annotation.Keep
import com.squareup.moshi.Json

@Keep
data class Results(
    @Json(name = "ordering")
    val ordering: Ordering,
    @Json(name = "theme")
    val theme: List<Theme>,
)