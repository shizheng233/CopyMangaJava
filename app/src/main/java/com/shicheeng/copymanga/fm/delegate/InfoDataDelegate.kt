package com.shicheeng.copymanga.fm.delegate

import com.google.gson.JsonObject
import com.shicheeng.copymanga.data.MangaHistoryDataModel
import com.shicheeng.copymanga.data.MangaInfoChapterDataBean
import com.shicheeng.copymanga.util.FileUtil
import com.shicheeng.copymanga.viewmodel.MangaHistoryViewModel

class InfoDataDelegate(
    private val pathWord: String,
    private val fileUtil: FileUtil,
) {


    fun mapChapters(
        historyDataModel: MangaHistoryDataModel?,
        jsonObject: JsonObject?,
    ): List<MangaInfoChapterDataBean> {
        val array = jsonObject?.getAsJsonArray("list") ?: return emptyList()
        return buildList {
            for ((i, element) in array.withIndex()) {
                val jsonObject1 = element.asJsonObject
                val mangaChapterTitle = jsonObject1["name"].asString
                val mangaChapterTime = jsonObject1["datetime_created"].asString
                val mangaChapterUUid = jsonObject1["uuid"].asString
                val mangaChapterPathWord = jsonObject1["comic_path_word"].asString
                val bean = MangaInfoChapterDataBean(
                    chapterTitle = mangaChapterTitle,
                    chapterTime = mangaChapterTime,
                    uuidText = mangaChapterUUid,
                    pathWord = mangaChapterPathWord,
                    readerProgress = when (historyDataModel) {
                        null -> null
                        else -> if (i == historyDataModel.positionChapter) historyDataModel.positionPage
                        else null
                    },
                    isSaved = fileUtil.isChapterDownloadedWithStringList(
                        mangaChapterPathWord,
                        mangaChapterUUid
                    )
                )
                add(bean)
            }
        }

    }

    companion object {

        fun mapDownloadedChapters(
            historyDataModel: MangaHistoryDataModel?,
            list: List<MangaInfoChapterDataBean>?,
        ): List<MangaInfoChapterDataBean>? {
            if (historyDataModel == null) {
                return list
            }
            return buildList {
                list?.forEachIndexed { index, mangaInfoChapterDataBean ->
                    val dataBean = MangaInfoChapterDataBean(
                        chapterTitle = mangaInfoChapterDataBean.chapterTitle,
                        chapterTime = mangaInfoChapterDataBean.chapterTime,
                        uuidText = mangaInfoChapterDataBean.uuidText,
                        readerProgress = if (index == historyDataModel.positionChapter) historyDataModel.positionPage else null,
                        isDownloading = mangaInfoChapterDataBean.isDownloading,
                        pathWord = mangaInfoChapterDataBean.pathWord,
                        isSaved = mangaInfoChapterDataBean.isSaved
                    )
                    add(dataBean)
                }
            }

        }
    }

}