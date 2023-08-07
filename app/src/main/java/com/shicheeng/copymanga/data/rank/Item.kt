package com.shicheeng.copymanga.data.rank


import com.squareup.moshi.Json
import androidx.annotation.Keep
import com.squareup.moshi.JsonClass

@Keep
@JsonClass(generateAdapter = true)
data class Item(
    @Json(name = "comic")
    val comic: Comic,
    @Json(name = "date_type")
    val dateType: Int,
    @Json(name = "popular")
    val popular: Int,
    @Json(name = "rise_num")
    val riseNum: Int,
    @Json(name = "rise_sort")
    val riseSort: Int,
    @Json(name = "sort")
    val sort: Int,
    @Json(name = "sort_last")
    val sortLast: Int
)