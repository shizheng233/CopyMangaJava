package com.shicheeng.copymanga.data.mangacomment


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import androidx.annotation.Keep

@Keep
@JsonClass(generateAdapter = true)
data class MangaCommentListItem(
    @Json(name = "comment")
    val comment: String,
    @Json(name = "count")
    val count: Int,
    @Json(name = "create_at")
    val createAt: String,
    @Json(name = "id")
    val id: Int,
    @Json(name = "parent_id")
    val parentId: Any?,
    @Json(name = "parent_user_id")
    val parentUserId: Any?,
    @Json(name = "parent_user_name")
    val parentUserName: Any?,
    @Json(name = "user_avatar")
    val userAvatar: String,
    @Json(name = "user_id")
    val userId: String,
    @Json(name = "user_name")
    val userName: String
)