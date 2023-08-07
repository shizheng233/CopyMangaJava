package com.shicheeng.copymanga.data.search


import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@Keep
@JsonClass(generateAdapter = true)
data class SearchResultDataModel(
    @Json(name = "alias")
    val alias: String?,
    @Json(name = "author")
    val author: List<Author>,
    @Json(name = "cover")
    val cover: String,
    @Json(name = "img_type")
    val imgType: Int,
    @Json(name = "name")
    val name: String,
    @Json(name = "path_word")
    val pathWord: String,
    @Json(name = "popular")
    val popular: Int,
) {

    fun authorReformation() = buildString {
        author.forEachIndexed { index, a ->
            append(a.name)
            if (index != author.lastIndex) {
                append("，")
            }
        }
    }

}