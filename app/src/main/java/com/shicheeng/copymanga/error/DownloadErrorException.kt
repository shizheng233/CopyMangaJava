package com.shicheeng.copymanga.error

class DownloadErrorException(
    val comicPathWord: String,
    val chapterUUID: String,
) : Exception("下载错误")