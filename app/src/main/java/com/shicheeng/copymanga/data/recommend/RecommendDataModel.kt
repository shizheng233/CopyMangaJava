package com.shicheeng.copymanga.data.recommend


import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@Keep
@JsonClass(generateAdapter = true)
data class RecommendDataModel(
    @Json(name = "code")
    val code: Int,
    @Json(name = "message")
    val message: String,
    @Json(name = "results")
    val results: Results,
) {
    @Keep
    @JsonClass(generateAdapter = true)
    data class Results(
        @Json(name = "limit")
        val limit: Int,
        @Json(name = "list")
        val list: List<Item>,
        @Json(name = "offset")
        val offset: Int,
        @Json(name = "total")
        val total: Int,
    ) {
        @Keep
        @JsonClass(generateAdapter = true)
        data class Item(
            @Json(name = "comic")
            val comic: Comic,
            @Json(name = "type")
            val type: Int,
        ) {
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
                val theme: List<Theme>,
            ) {

                fun authorReformation() = buildString {
                    author.forEachIndexed { index, a ->
                        append(a.name)
                        if (index != author.lastIndex) {
                            append("，")
                        }
                    }
                }

                @Keep
                @JsonClass(generateAdapter = true)
                data class Author(
                    @Json(name = "name")
                    val name: String,
                    @Json(name = "path_word")
                    val pathWord: String,
                )

                @Keep
                @JsonClass(generateAdapter = true)
                data class Theme(
                    @Json(name = "name")
                    val name: String,
                    @Json(name = "path_word")
                    val pathWord: String,
                )
            }
        }
    }
}