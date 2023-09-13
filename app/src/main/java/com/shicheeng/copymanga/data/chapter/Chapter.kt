package com.shicheeng.copymanga.data.chapter


import androidx.annotation.Keep
import com.shicheeng.copymanga.data.local.LocalChapter
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@Keep
@JsonClass(generateAdapter = true)
data class Chapter(
    @Json(name = "comic_id")
    val comicId: String,
    @Json(name = "comic_path_word")
    val comicPathWord: String,
    @Json(name = "count")
    val count: Int,
    var datetime_created: String,
    @Json(name = "group_id")
    val groupId: Any?,
    @Json(name = "group_path_word")
    val groupPathWord: String,
    @Json(name = "img_type")
    val imgType: Int,
    @Json(name = "index")
    val index: Int,
    @Json(name = "name")
    val name: String,
    @Json(name = "news")
    val news: String,
    @Json(name = "next")
    val next: String?,
    @Json(name = "ordered")
    val ordered: Int,
    @Json(name = "prev")
    val prev: String?,
    @Json(name = "size")
    val size: Int,
    @Json(name = "type")
    val type: Int,
    @Json(name = "uuid")
    val uuid: String,
)

fun Chapter.toLocalChapter(
    readIndex: Int,
    isReadInProgress: Boolean,
    isDownloaded: Boolean,
    isReadFinish: Boolean,
): LocalChapter {
    return LocalChapter(
        comicId = comicId,
        comicPathWord = comicPathWord,
        count = count,
        datetime_created = datetime_created,
        groupId = groupId as String?,
        groupPathWord = groupPathWord,
        imgType = imgType,
        index = index,
        readIndex = readIndex,
        isReadProgress = isReadInProgress,
        name = name,
        news = news,
        next = next,
        ordered = ordered,
        prev = prev,
        size = size,
        type = type,
        uuid = uuid,
        isDownloaded = isDownloaded,
        isReadFinish = isReadFinish
    )
}