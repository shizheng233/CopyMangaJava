package com.shicheeng.copymanga.data.webbookshelf


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import androidx.annotation.Keep

@Keep
@JsonClass(generateAdapter = true)
data class LastBrowse(
    @Json(name = "last_browse_id")
    val lastBrowseId: String,
    @Json(name = "last_browse_name")
    val lastBrowseName: String
)