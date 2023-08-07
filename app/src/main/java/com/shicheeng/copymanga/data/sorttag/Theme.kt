package com.shicheeng.copymanga.data.sorttag


import android.util.Log
import androidx.annotation.Keep
import com.shicheeng.copymanga.data.MangaSortBean
import com.squareup.moshi.Json

@Keep
data class Theme(
    @Json(name = "count")
    val count: Int,
    @Json(name = "initials")
    val initials: Int,
    @Json(name = "name")
    val name: String,
    @Json(name = "path_word")
    val pathWord: String,
) {
    init {
        Log.d("TAG", "THEME: $pathWord")
    }
    fun toMangaSortBean() = MangaSortBean(
        /* pathName = */ name,
        /* pathWord = */ pathWord
    )
}