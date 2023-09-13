package com.shicheeng.copymanga.data.login

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.shicheeng.copymanga.data.lofininfo.LoginInfoDataModel

@Entity
data class LocalLoginDataModel(
    val avatarImageUrl: String,
    val nikeName: String,
    val userName: String,
    val token: String,
    @PrimaryKey val userID: String,
    val email: String,
    val selected: Boolean,
    val isExpired: Boolean,
)

fun LoginDataModel.toLoginDataModel(isSelected: Boolean = false) = LocalLoginDataModel(
    nikeName = results.nickname,
    userName = results.username,
    email = results.email,
    token = results.token,
    userID = results.userId,
    avatarImageUrl = results.avatar,
    selected = isSelected,
    isExpired = false
)

fun LoginInfoDataModel.toLoginDataModel(
    localLoginDataModel: LocalLoginDataModel,
    isSelected: Boolean = false,
    isExpired: Boolean,
) = LocalLoginDataModel(
    nikeName = results.nickname,
    userName = results.username,
    email = results.email,
    token = localLoginDataModel.token,
    userID = results.userId,
    avatarImageUrl = results.avatar,
    selected = isSelected,
    isExpired = isExpired
)