package com.shicheeng.copymanga.data

import android.net.Uri
import android.os.Parcelable
import androidx.annotation.StringRes
import kotlinx.parcelize.Parcelize

data class PersonalDataModel(@StringRes val title: Int, val list: List<Any>)

@Parcelize
data class PersonalInnerDataModel(
    val name: String,
    val url: Uri?,
    val pathWord: String?,
) : Parcelable

