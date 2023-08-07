package com.shicheeng.copymanga.data

data class ReaderState(
    val mangaName:String?,
    val chapterName: String?,
    val subTime: String?,
    val uuid: String?,
    val totalPage: Int,
    val currentPage: Int,
    val chapterPosition: Int,
)