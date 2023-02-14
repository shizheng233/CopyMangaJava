package com.shicheeng.copymanga.data

data class MangaInfoData(
    val title: String,
    val alias: String,
    val mangaDetail: String,
    val mangaStatus: String,
    val authorList: String,
    val themeList: List<ChipTextBean>,
    val mangaCoverUrl: String,
    val mangaUUID: String,
    val mangaStatusId: Int,
    val mangaRegion: String,
    val mangaLastUpdate: String,
    val mangaPopularNumber: String,
)

data class MangaRankMiniModel(
    val name: String,
    val author: String,
    val urlCover: String,
    val popular: String,
    val riseHot: String,
    val pathWord: String,
)