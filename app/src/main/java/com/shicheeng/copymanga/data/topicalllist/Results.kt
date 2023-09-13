package com.shicheeng.copymanga.data.topicalllist


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import androidx.annotation.Keep

@Keep
@JsonClass(generateAdapter = true)
data class Results(
    @Json(name = "limit")
    val limit: Int,
    @Json(name = "list")
    val list: List<TopicAllListItem>,
    @Json(name = "offset")
    val offset: Int,
    @Json(name = "total")
    val total: Int
)