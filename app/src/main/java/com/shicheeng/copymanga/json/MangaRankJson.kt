package com.shicheeng.copymanga.json

import android.util.Log
import com.google.gson.JsonParser
import com.shicheeng.copymanga.data.ListBeanManga
import com.shicheeng.copymanga.util.ApiName
import com.shicheeng.copymanga.util.KeyWordSwap
import com.shicheeng.copymanga.util.authorNameReformation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request

object MangaRankJson {

    suspend fun rankGet(offset: Int, type: String): List<ListBeanManga> =
        withContext(Dispatchers.Default) {
            val url = ApiName.mangaRank(offset.toString(), type)
            val collections: ArrayList<ListBeanManga> = ArrayList()
            val client = OkHttpClient()
            val request: Request = Request.Builder()
                .removeHeader(KeyWordSwap.USER_AGENT_WORD)
                .addHeader(KeyWordSwap.USER_AGENT_WORD, KeyWordSwap.FAKE_USER_AGENT)
                .headers(MangaInfoJson.headers)
                .url(url)
                .build()
            client.newCall(request).execute().use { response ->
                val json = response.body?.string()
                val jsonObject =
                    JsonParser.parseString(json).asJsonObject.getAsJsonObject("results")
                val array = jsonObject.getAsJsonArray("list")
                Log.i("MRJ_ARRAY", array.toString() + "")
                for (element in array) {
                    val bean = ListBeanManga()
                    val jsonObject1 = element.asJsonObject.getAsJsonObject("comic")
                    val stringName = jsonObject1["name"].asString
                    val stringAuthor = jsonObject1["author"].asJsonArray.authorNameReformation()
                    // Log.i("MRJ",stringAuthor);
                    val urlImage = jsonObject1["cover"].asString
                    val pathWord = jsonObject1["path_word"].asString
                    bean.pathWordManga = pathWord
                    bean.urlCoverManga = urlImage
                    bean.authorManga = stringAuthor
                    bean.nameManga = stringName
                    collections.add(bean)
                }
                collections
            }
        }
}