package com.shicheeng.copymanga.data.webbookshelf


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import androidx.annotation.Keep

@Keep
@JsonClass(generateAdapter = true)
data class WebBookshelfItem(
    @Json(name = "b_folder")
    val bFolder: Boolean,
    @Json(name = "comic")
    val comic: Comic,
    @Json(name = "folder_id")
    val folderId: Any?,
    @Json(name = "last_browse")
    val lastBrowse: LastBrowse?,
    @Json(name = "name")
    val name: Any?,
    @Json(name = "uuid")
    val uuid: Int
)