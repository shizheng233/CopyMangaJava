package com.shicheeng.copymanga.data.info


import com.squareup.moshi.Json
import androidx.annotation.Keep
import com.squareup.moshi.JsonClass

@Keep
@JsonClass(generateAdapter = true)
data class Results(
    @Json(name = "comic")
    val comic: Comic,
    @Json(name = "groups")
    val groups: Groups,
    @Json(name = "is_lock")
    val isLock: Boolean,
    @Json(name = "is_login")
    val isLogin: Boolean,
    @Json(name = "is_mobile_bind")
    val isMobileBind: Boolean,
    @Json(name = "is_vip")
    val isVip: Boolean,
    @Json(name = "popular")
    val popular: Int
)