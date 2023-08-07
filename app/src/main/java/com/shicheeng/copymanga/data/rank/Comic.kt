package com.shicheeng.copymanga.data.rank


import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@Keep
@JsonClass(generateAdapter = true)
data class Comic(
    @Json(name = "author")
    val author: List<Author>,
    @Json(name = "cover")
    val cover: String,
    @Json(name = "females")
    val females: List<Any>,
    @Json(name = "img_type")
    val imgType: Int,
    @Json(name = "males")
    val males: List<Any>,
    @Json(name = "name")
    val name: String,
    @Json(name = "path_word")
    val pathWord: String,
    @Json(name = "popular")
    val popular: Int,
    @Json(name = "theme")
    val theme: List<Any>,
) {

    fun authorThat() = buildString {
        author.forEachIndexed { index: Int, authorIn: Author ->
            append(authorIn.name)
            if (index != author.lastIndex) {
                append("，")
            }
        }
    }

}