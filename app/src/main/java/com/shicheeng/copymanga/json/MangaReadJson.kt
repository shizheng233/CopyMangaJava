package com.shicheeng.copymanga.json

import com.google.gson.JsonParser
import com.shicheeng.copymanga.data.MangaReadInformation
import com.shicheeng.copymanga.data.MangaReaderPage
import com.shicheeng.copymanga.util.ApiName
import com.shicheeng.copymanga.util.FileUtil
import com.shicheeng.copymanga.util.KeyWordSwap
import com.shicheeng.copymanga.util.OkhttpHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Request

object MangaReadJson {


    suspend fun getMangaInfo(
        pathWord: String?,
        uuid: String?,
        fileUtil: FileUtil,
    ): MangaReadInformation = withContext(Dispatchers.Default) {
        if (fileUtil.isChapterDownloadedWithStringList(pathWord, uuid)) {
            val json = fileUtil.findDownloadChapterInfo(pathWord, uuid)
            val time = json?.get("chapter_name")?.asString
            val listSize = fileUtil.ifChapterDownloaded(pathWord, uuid).size
            MangaReadInformation(null, time, listSize)
        } else {
            val url = ApiName.mangaPhotoApi(pathWord, uuid)
            val client = OkhttpHelper.getInstance()
            val request: Request = Request.Builder().removeHeader(KeyWordSwap.USER_AGENT_WORD)
                .addHeader(KeyWordSwap.USER_AGENT_WORD, KeyWordSwap.FAKE_USER_AGENT)
                .headers(MangaInfoJson.headers)
                .url(url)
                .build()
            client.newCall(request).execute().use { response ->
                val json = response.body?.string()
                val jsonObject =
                    JsonParser.parseString(json).asJsonObject.getAsJsonObject("results")
                val jsonChapter = jsonObject["chapter"].asJsonObject
                val time = jsonChapter["name"].asString //This is manga chapter name. I was wrong.
                val subtitle = jsonChapter["datetime_created"].asString
                val size = jsonChapter["size"].asInt
                MangaReadInformation(subtitle, time, size)
            }

        }
    }

    suspend fun getMangaPic(
        pathWord: String?,
        uuid: String?,
        fileUtil: FileUtil,
    ): List<MangaReaderPage> =
        withContext(Dispatchers.Default) {
            if (fileUtil.isChapterDownloadedWithStringList(pathWord, uuid)) {
                val list = fileUtil.ifChapterDownloaded(pathWord, uuid)
                val sortedList = list.sortedWith { text1, text2 ->
                    text1.url.split("/").last().split(".").first().toInt()
                        .compareTo(text2.url.split("/").last().split(".").first().toInt())
                }
                val newList = buildList {
                    for (i in sortedList.indices) {
                        add(sortedList[i].copy(index = i))
                    }
                }
                newList
            } else {
                val url = ApiName.mangaPhotoApi(pathWord, uuid)
                val client = OkhttpHelper.getInstance()
                val request: Request = Request.Builder().removeHeader(KeyWordSwap.USER_AGENT_WORD)
                    .addHeader(KeyWordSwap.USER_AGENT_WORD, KeyWordSwap.FAKE_USER_AGENT)
                    .headers(MangaInfoJson.headers)
                    .url(url)
                    .build()
                client.newCall(request).execute().use { response ->
                    val hashMap = HashMap<Int, String>()
                    val json = response.body?.string()
                    val jsonObject =
                        JsonParser.parseString(json).asJsonObject.getAsJsonObject("results")
                    val jsonChapter = jsonObject["chapter"].asJsonObject
                    val array = jsonChapter.getAsJsonArray("contents")
                    val array2 = jsonChapter.getAsJsonArray("words")
                    for (i in 0 until array.size()) {
                        val url2 = array[i].asJsonObject["url"].asString
                        val num = array2[i].asInt
                        hashMap[num] = url2
                    }
                    val listUrl = buildList {
                        for (i in 0 until hashMap.size) {
                            val urlImage = hashMap[i]
                            urlImage?.let {
                                add(MangaReaderPage(it, uuid, i))
                            }
                        }
                    }
                    listUrl
                }
            }

        }
}