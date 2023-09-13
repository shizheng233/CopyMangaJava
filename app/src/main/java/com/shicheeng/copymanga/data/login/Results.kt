package com.shicheeng.copymanga.data.login


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import androidx.annotation.Keep

@Keep
@JsonClass(generateAdapter = true)
data class Results(
    @Json(name = "ads_vip_end")
    val adsVipEnd: Any?,
    @Json(name = "avatar")
    val avatar: String,
    @Json(name = "b_sstv")
    val bSstv: Boolean,
    @Json(name = "b_verify_email")
    val bVerifyEmail: Boolean,
    @Json(name = "cartoon_vip")
    val cartoonVip: Int,
    @Json(name = "cartoon_vip_end")
    val cartoonVipEnd: Any?,
    @Json(name = "cartoon_vip_start")
    val cartoonVipStart: Any?,
    @Json(name = "close_report")
    val closeReport: Boolean,
    @Json(name = "comic_vip")
    val comicVip: Int,
    @Json(name = "comic_vip_end")
    val comicVipEnd: Any?,
    @Json(name = "comic_vip_start")
    val comicVipStart: Any?,
    @Json(name = "datetime_created")
    val datetimeCreated: String,
    @Json(name = "downloads")
    val downloads: Int,
    @Json(name = "email")
    val email: String,
    @Json(name = "invite_code")
    val inviteCode: Any?,
    @Json(name = "invited")
    val invited: Any?,
    @Json(name = "is_authenticated")
    val isAuthenticated: Boolean,
    @Json(name = "mobile")
    val mobile: Any?,
    @Json(name = "mobile_region")
    val mobileRegion: Any?,
    @Json(name = "nickname")
    val nickname: String,
    @Json(name = "point")
    val point: Int,
    @Json(name = "reward_downloads")
    val rewardDownloads: Int,
    @Json(name = "scy_answer")
    val scyAnswer: Boolean,
    @Json(name = "token")
    val token: String,
    @Json(name = "user_id")
    val userId: String,
    @Json(name = "username")
    val username: String,
    @Json(name = "vip_downloads")
    val vipDownloads: Int
)