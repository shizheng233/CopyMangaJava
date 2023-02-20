package com.shicheeng.copymanga.data

import android.net.Uri
import androidx.annotation.StringRes

data class PersonalDataModel(@StringRes val title: Int, val list: List<Any>)

data class PersonalInnerDataModel(val name: String, val url: Uri?, val pathWord: String?)

data class PersonalList(
    val listDownload: List<PersonalInnerDataModel>,
    val listHistory: List<MangaHistoryDataModel>,
)