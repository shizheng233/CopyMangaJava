package com.shicheeng.copymanga.data

data class MainPageDataModel(
    val listBanner: List<DataBannerBean>,
    val listRecommend: List<ListBeanManga>,
    val listRankDay: List<MangaRankMiniModel>,
    val listRankWeek: List<MangaRankMiniModel>,
    val listRankMonth: List<MangaRankMiniModel>,
    val listHot: List<ListBeanManga>,
    val listNewest: List<ListBeanManga>,
    val listFinished: List<ListBeanManga>,
)