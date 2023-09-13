package com.shicheeng.copymanga.data.info


import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@Keep
@JsonClass(generateAdapter = true)
data class Comic(
    @Json(name = "alias")
    val alias: String?,
    @Json(name = "author")
    val author: List<Author>,
    @Json(name = "brief")
    val brief: String,
    @Json(name = "clubs")
    val clubs: List<Any>,
    @Json(name = "cover")
    val cover: String,
    @Json(name = "datetime_updated")
    val datetimeUpdated: String?,
    @Json(name = "females")
    val females: List<Any>,
    @Json(name = "free_type")
    val freeType: FreeType,
    @Json(name = "img_type")
    val imgType: Int,
    @Json(name = "last_chapter")
    val lastChapter: LastChapter,
    @Json(name = "males")
    val males: List<Any>,
    @Json(name = "name")
    val name: String,
    @Json(name = "parodies")
    val parodies: List<Any>,
    @Json(name = "path_word")
    val pathWord: String,
    @Json(name = "popular")
    val popular: Int,
    @Json(name = "reclass")
    val reclass: Reclass,
    @Json(name = "region")
    val region: Region,
    @Json(name = "restrict")
    val restrict: Restrict,
    @Json(name = "seo_baidu")
    val seoBaidu: String,
    @Json(name = "status")
    val status: Status,
    @Json(name = "theme")
    val theme: List<Theme>,
    @Json(name = "uuid")
    val uuid: String,
){

    fun authorReformation() = buildString {
        author.forEachIndexed { index, a ->
            append(a.name)
            if (index != author.lastIndex) {
                append("，")
            }
        }
    }

}