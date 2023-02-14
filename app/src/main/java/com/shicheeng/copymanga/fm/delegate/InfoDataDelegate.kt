package com.shicheeng.copymanga.fm.delegate

import com.google.gson.JsonObject
import com.shicheeng.copymanga.data.MangaHistoryDataModel
import com.shicheeng.copymanga.data.MangaInfoChapterDataBean
import com.shicheeng.copymanga.util.FileUtil

class InfoDataDelegate(
    private val pathWord: String,
    private val fileUtil: FileUtil,
) {


    fun loadInfo() {

    }


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

}