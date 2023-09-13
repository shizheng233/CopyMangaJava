package com.shicheeng.copymanga.data.topicalllist


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import androidx.annotation.Keep

@Keep
@JsonClass(generateAdapter = true)
data class TopicAllListItem(
    @Json(name = "brief")
    val brief: String,
    @Json(name = "cover")
    val cover: String,
    @Json(name = "datetime_created")
    val datetimeCreated: String,
    @Json(name = "journal")
    val journal: String,
    @Json(name = "path_word")
    val pathWord: String,
    @Json(name = "period")
    val period: String,
    @Json(name = "series")
    val series: Series,
    @Json(name = "title")
    val title: String,
    @Json(name = "type")
    val type: Int
)