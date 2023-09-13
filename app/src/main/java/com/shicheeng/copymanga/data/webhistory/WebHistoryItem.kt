package com.shicheeng.copymanga.data.webhistory


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import androidx.annotation.Keep

@Keep
@JsonClass(generateAdapter = true)
data class WebHistoryItem(
    @Json(name = "comic")
    val comic: Comic,
    @Json(name = "id")
    val id: Int,
    @Json(name = "last_chapter_id")
    val lastChapterId: String,
    @Json(name = "last_chapter_name")
    val lastChapterName: String
)