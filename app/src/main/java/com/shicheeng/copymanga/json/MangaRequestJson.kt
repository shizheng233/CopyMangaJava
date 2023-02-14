package com.shicheeng.copymanga.json

import com.google.gson.JsonParser
import com.shicheeng.copymanga.data.ListBeanManga
import com.shicheeng.copymanga.util.ApiName
import com.shicheeng.copymanga.util.KeyWordSwap
import com.shicheeng.copymanga.util.authorNameReformation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

object MangaRequestJson {
    /**
     * 获取最新的漫画列表
     *
     * @param offset 偏移量
     * @return list 漫画的列表
     * @throws IOException 错误
     */
    suspend fun getNewestMangaTotal(offset: Int): List<ListBeanManga> =
        withContext(Dispatchers.Default) {
            val list: ArrayList<ListBeanManga> = ArrayList()
            val url = ApiName.mangaNewestApi(offset)
            val client = OkHttpClient()
            val request: Request = Request.Builder().url(url)
                .removeHeader(KeyWordSwap.USER_AGENT_WORD)
                .addHeader(KeyWordSwap.USER_AGENT_WORD, KeyWordSwap.FAKE_USER_AGENT)
                .headers(MangaInfoJson.headers)
                .build()
            client.newCall(request).execute().use { response ->
                val jsonOrigin = response.body?.string()
                val jsonObject1 = JsonParser.parseString(jsonOrigin).asJsonObject
                val array = jsonObject1["results"].asJsonObject["list"]
                    .asJsonArray
                for (i in array) {
                    val mangas = ListBeanManga()
                    val object2 = i.asJsonObject.getAsJsonObject("comic")
                    val array2 = object2.getAsJsonArray("author")
                    mangas.nameManga = object2["name"].asString
                    mangas.pathWordManga = object2["path_word"].asString
                    mangas.urlCoverManga = object2["cover"].asString
                    mangas.authorManga = array2.authorNameReformation()
                    list.add(mangas)
                }
                list
            }
        }

    /**
     * 获取所有完结的列表
     *
     * @param offset 页数
     * @return List
     * @throws IOException 错误
     */
    suspend fun getFinishMangaTotal(offset: Int): List<ListBeanManga> =
        withContext(Dispatchers.Default) {
            val list: ArrayList<ListBeanManga> = ArrayList()
            val url = ApiName.mangaFinishApi(offset)
            val client = OkHttpClient()
            val request: Request = Request.Builder().url(url)
                .removeHeader(KeyWordSwap.USER_AGENT_WORD)
                .addHeader(KeyWordSwap.USER_AGENT_WORD, KeyWordSwap.FAKE_USER_AGENT)
                .headers(MangaInfoJson.headers)
                .build()
            client.newCall(request).execute().use { response ->
                val jsonOrigin = response.body?.string()
                val jsonObject1 = JsonParser.parseString(jsonOrigin).asJsonObject
                val array = jsonObject1["results"].asJsonObject["list"]
                    .asJsonArray
                for (i in array) {
                    val mangas = ListBeanManga()
                    val object2 = i.asJsonObject
                    val array2 = object2.getAsJsonArray("author")
                    mangas.nameManga = object2["name"].asString
                    mangas.pathWordManga = object2["path_word"].asString
                    mangas.urlCoverManga = object2["cover"].asString
                    mangas.authorManga = array2.authorNameReformation()
                    list.add(mangas)
                }
                list
            }
        }

    /**
     * 获取漫画的推荐数据
     * I use one in [MangaRecommendJson]. This is expect one.
     *
     * @param offset 偏移
     * @return 数据
     * @throws IOException 错误一
     */
    fun listDataRec(offset: Int): List<ListBeanManga> {
        val mangas: ArrayList<ListBeanManga> = ArrayList()
        val client = OkHttpClient()
        val request: Request =
            Request.Builder().url(ApiName.mangaRecommendApi(offset.toString()))
                .removeHeader(KeyWordSwap.USER_AGENT_WORD)
                .addHeader(KeyWordSwap.USER_AGENT_WORD, KeyWordSwap.FAKE_USER_AGENT)
                .build()
        client.newCall(request).execute().use { response ->
            assert(response.body != null)
            val jsonMangaResult = response.body!!.string()
            val jsonObjectManga1 = JsonParser
                .parseString(jsonMangaResult)
                .asJsonObject
                .getAsJsonObject("results")
            val mangaList1 = jsonObjectManga1.getAsJsonArray("list")
            for (i in 0 until mangaList1.size()) {
                val beanManga1 = ListBeanManga()
                val jsonObjectManga2 = mangaList1[i]
                    .asJsonObject
                    .getAsJsonObject("comic")
                beanManga1.nameManga = jsonObjectManga2["name"].asString
                beanManga1.urlCoverManga = jsonObjectManga2["cover"].asString
                beanManga1.pathWordManga = jsonObjectManga2["path_word"].asString
                val mangaAuthorList = jsonObjectManga2["author"].asJsonArray
                beanManga1.authorManga = mangaAuthorList.authorNameReformation()
                mangas.add(beanManga1)
            }
            return mangas
        }
    }
}