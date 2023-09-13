package com.shicheeng.copymanga.data.logininfoshort


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import androidx.annotation.Keep

@Keep
@JsonClass(generateAdapter = true)
data class Info(
    @Json(name = "avatar")
    val avatar: String,
    @Json(name = "avatar_rp")
    val avatarRp: String,
    @Json(name = "gender")
    val gender: GenderX,
    @Json(name = "invite_code")
    val inviteCode: Any?,
    @Json(name = "mobile")
    val mobile: Any?,
    @Json(name = "mobile_region")
    val mobileRegion: Any?,
    @Json(name = "nickname")
    val nickname: String,
    @Json(name = "username")
    val username: String
)