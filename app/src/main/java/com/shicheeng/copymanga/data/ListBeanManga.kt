package com.shicheeng.copymanga.data

data class ListBeanManga(
    var nameManga: String,
    var authorManga: String,
    var urlCoverManga: String,
    var pathWordManga: String,
) {
    constructor() : this(
        nameManga = "",
        authorManga = "",
        urlCoverManga = "",
        pathWordManga = ""
    )
}