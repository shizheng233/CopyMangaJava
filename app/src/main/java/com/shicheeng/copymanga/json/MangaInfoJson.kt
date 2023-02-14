package com.shicheeng.copymanga.json

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.shicheeng.copymanga.MyApp
import com.shicheeng.copymanga.R
import com.shicheeng.copymanga.data.*
import com.shicheeng.copymanga.util.ApiName
import com.shicheeng.copymanga.util.KeyWordSwap
import com.shicheeng.copymanga.util.formNumberToRead
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Headers
import okhttp3.OkHttpClient
import okhttp3.Request

object MangaInfoJson {

    private val setting = (MyApp.appContext as MyApp).appPreference

    val headers: Headers = Headers.Builder()
        .add("region", if (setting.getBoolean("pref_is_use_foreign_api", false)) "0" else "1")
        .add("webp", "0")
        .add("platform", "1")
        .add("version", "2022.12.02")
        .add("referer", "https://www.copymanga.site/")
        .add(KeyWordSwap.USER_AGENT_WORD, KeyWordSwap.FAKE_USER_AGENT)
        .build()

    /**
     *
     * @param path_word 漫画的path word
     * @return 带有信息的数据模型
     */
    fun getMangaInfo(path_word: String?): MangaInfoData {
        val url = ApiName.mangaInfoAdapter(path_word)
        val client = OkHttpClient()
        val request: Request = Request.Builder()
            .url(url)
            .removeHeader(KeyWordSwap.USER_AGENT_WORD)
            .addHeader(KeyWordSwap.USER_AGENT_WORD, KeyWordSwap.FAKE_USER_AGENT)
            .headers(headers)
            .build()


        client.newCall(request).execute().use { response ->
            val result = response.body?.string()!!
            val jsonObject1 = JsonParser.parseString(result).asJsonObject
                .getAsJsonObject("results").getAsJsonObject("comic")
            val array1 = jsonObject1.getAsJsonArray("author")
            val array2 = jsonObject1.getAsJsonArray("theme")
            val mangaName = jsonObject1["name"].asString
            val mangaAlias: String = if (jsonObject1["alias"].isJsonNull) {
                "无别名"
            } else {
                jsonObject1["alias"].asString
            }
            val mangaThemes: MutableList<ChipTextBean> = ArrayList()

            val mangaAuthors = buildString {
                array1.forEachIndexed { index, jsonElement ->
                    append(jsonElement.asJsonObject["name"].asString)
                    if (index != array1.size() - 1) {
                        append("，")
                    }
                }
            }

            array2.forEach { theme ->
                val bean = ChipTextBean(
                    theme.asJsonObject["name"].asString,
                    theme.asJsonObject["path_word"].asString,
                    R.drawable.ic_manga_tag
                )
                mangaThemes.add(bean)
            }
            val mangaDetail = jsonObject1["brief"].asString
            val mangaCoverUrl = jsonObject1["cover"].asString
            val mangaStatus = jsonObject1["status"].asJsonObject["display"].asString
            val mangaStatusId = jsonObject1["status"].asJsonObject["value"].asInt
            val mangaRegion = jsonObject1["region"].asJsonObject["display"].asString
            val mangaLastUpdate = jsonObject1["datetime_updated"].asString
            val mangaPopularNum = jsonObject1["popular"].asLong.formNumberToRead()
            val mangaBuUid = jsonObject1["uuid"].asString
            return MangaInfoData(
                title = mangaName,
                alias = mangaAlias,
                mangaDetail = mangaDetail,
                mangaStatus = mangaStatus,
                authorList = mangaAuthors,
                themeList = mangaThemes,
                mangaCoverUrl = mangaCoverUrl,
                mangaUUID = mangaBuUid,
                mangaStatusId = mangaStatusId,
                mangaRegion = mangaRegion,
                mangaLastUpdate = mangaLastUpdate,
                mangaPopularNumber = mangaPopularNum
            )
        }
    }

    fun getMangaContent(path_word: String?): JsonObject {
        val url = ApiName.mangaChapterApi(path_word)
        val client = OkHttpClient()
        val request: Request = Request.Builder()
            .url(url)
            .removeHeader(KeyWordSwap.USER_AGENT_WORD)
            .addHeader(KeyWordSwap.USER_AGENT_WORD, KeyWordSwap.FAKE_USER_AGENT)
            .headers(headers)
            .build()
        client.newCall(request).execute().use { response ->
            return JsonParser.parseString(response.body!!.string())
                .asJsonObject
                .getAsJsonObject("results")
        }
    }

    suspend fun parserMangaDownloadChapters(
        pathWord: String?,
        uuid: String,
    ): MangaDownloads =
        withContext(Dispatchers.Default) {
            val url = ApiName.mangaPhotoApi(pathWord, uuid)
            val client = OkHttpClient()
            val request: Request = Request.Builder().removeHeader(KeyWordSwap.USER_AGENT_WORD)
                .addHeader(KeyWordSwap.USER_AGENT_WORD, KeyWordSwap.FAKE_USER_AGENT)
                .headers(headers)
                .url(url)
                .build()
            client.newCall(request).execute().use { response ->
                val json = response.body?.string()
                val jsonObject =
                    JsonParser.parseString(json).asJsonObject.getAsJsonObject("results")
                val jsonChapter = jsonObject["chapter"].asJsonObject
                val arrayOfUrl = buildList {
                    jsonChapter.getAsJsonArray("contents").forEach {
                        add(it.asJsonObject["url"].asString)
                    }
                }
                val arrayOfWord = buildList {
                    jsonChapter.getAsJsonArray("words").forEach {
                        add(it.asInt)
                    }
                }

                val mangaDownloads =
                    MangaDownloads(
                        urlList = arrayOfUrl,
                        wordsList = arrayOfWord
                    )
                mangaDownloads
            }


        }
}