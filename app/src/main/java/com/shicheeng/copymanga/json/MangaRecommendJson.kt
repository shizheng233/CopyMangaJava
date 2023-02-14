package com.shicheeng.copymanga.json

import com.google.gson.JsonParser
import com.shicheeng.copymanga.util.KeyWordSwap
import com.shicheeng.copymanga.data.ListBeanManga
import com.shicheeng.copymanga.util.ApiName
import com.shicheeng.copymanga.util.authorNameReformation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request

object MangaRecommendJson {


    /**
     * Get the recommend manga data.
     *
     * @param index the offset in manga Api
     * @return A list of recommend manga
     */
    suspend fun mangaRecommendList(index: Int): List<ListBeanManga> = withContext(Dispatchers.Default) {
        val url = ApiName.mangaRecommendApi(index.toString())
        val client = OkHttpClient()
        val request: Request = Request.Builder()
            .removeHeader(KeyWordSwap.USER_AGENT_WORD)
            .addHeader(KeyWordSwap.USER_AGENT_WORD, KeyWordSwap.FAKE_USER_AGENT)
            .headers(MangaInfoJson.headers)
            .url(url).build()
        client.newCall(request).execute().use { response ->
            val jsonRootArray =
                JsonParser.parseString(response.body!!.string()).asJsonObject["results"]
                    .asJsonObject
                    .getAsJsonArray("list")
            buildList {
                jsonRootArray.forEach { jsonElement ->
                    val comic = jsonElement.asJsonObject["comic"].asJsonObject
                    val name = comic["name"].asString
                    val author = comic["author"].asJsonArray.authorNameReformation()
                    val urlCover = comic["cover"].asString
                    val pathWord = comic["path_word"].asString
                    val listBeanManga = ListBeanManga(name, author, urlCover, pathWord)
                    add(listBeanManga)
                }
            }
        }
    }


}