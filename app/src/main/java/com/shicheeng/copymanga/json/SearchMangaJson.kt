package com.shicheeng.copymanga.json

import com.google.gson.JsonParser
import com.shicheeng.copymanga.data.ListBeanManga
import com.shicheeng.copymanga.error.EmptyJsonArray
import com.shicheeng.copymanga.util.ApiName
import com.shicheeng.copymanga.util.KeyWordSwap
import com.shicheeng.copymanga.util.authorNameReformation
import com.shicheeng.copymanga.util.checkJsonIsEmpty
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request

object SearchMangaJson {

    suspend fun toGetManga(name: String, offset: Int): List<ListBeanManga> =
        withContext(Dispatchers.Default) {
            val url = ApiName.mangaSearchApi(name, offset)
            val client = OkHttpClient()
            val request: Request = Request.Builder().removeHeader(KeyWordSwap.USER_AGENT_WORD)
                .addHeader(KeyWordSwap.USER_AGENT_WORD, KeyWordSwap.FAKE_USER_AGENT)
                .headers(MangaInfoJson.headers)
                .url(url)
                .build()
            client.newCall(request).execute().use { response ->
                val mangas: ArrayList<ListBeanManga> = ArrayList()
                val json = response.body?.string()
                if (!json.isNullOrEmpty()) {
                    if (json.checkJsonIsEmpty()) {
                        throw EmptyJsonArray()
                    }
                }
                val object1 = JsonParser.parseString(json).asJsonObject["results"].asJsonObject
                val array1 = object1.getAsJsonArray("list")
                for (i in array1) {
                    val manga = ListBeanManga()
                    val object2 = i.asJsonObject
                    manga.nameManga = object2["name"].asString
                    manga.pathWordManga = object2["path_word"].asString
                    manga.urlCoverManga = object2["cover"].asString
                    val array = object2["author"].asJsonArray
                    manga.authorManga = array.authorNameReformation()
                    mangas.add(manga)
                }
                mangas
            }
        }
}