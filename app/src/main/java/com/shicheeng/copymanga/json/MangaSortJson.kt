package com.shicheeng.copymanga.json

import com.google.gson.JsonParser
import com.shicheeng.copymanga.data.ListBeanManga
import com.shicheeng.copymanga.data.MangaSortBean
import com.shicheeng.copymanga.util.ApiName
import com.shicheeng.copymanga.util.KeyWordSwap
import com.shicheeng.copymanga.util.authorNameReformation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request

object MangaSortJson {

    suspend fun getSort(): List<MangaSortBean> = withContext(Dispatchers.Default) {
        val list: MutableList<MangaSortBean> = ArrayList()
        val url = ApiName.mangaSortUrl
        val client = OkHttpClient()
        val request: Request = Request.Builder()
            .removeHeader(KeyWordSwap.USER_AGENT_WORD)
            .addHeader(KeyWordSwap.USER_AGENT_WORD, KeyWordSwap.FAKE_USER_AGENT)
            .headers(MangaInfoJson.headers)
            .url(url)
            .build()
        client.newCall(request).execute().use { response ->
            val json = response.body?.string()
            val jsonObject = JsonParser.parseString(json).asJsonObject
                .getAsJsonObject("results")
            val array = jsonObject["theme"].asJsonArray
            for (i in array) {
                val sortBean = MangaSortBean()
                val stringText = i.asJsonObject["name"].asString
                val stringPath = i.asJsonObject["path_word"].asString
                sortBean.pathName = stringText
                sortBean.pathWord = stringPath
                list.add(sortBean)
            }
            val bean = MangaSortBean()
            bean.pathWord = "all"
            bean.pathName = "全部"
            list.add(0, bean)
            list
        }
    }

    @JvmStatic
    val order: List<MangaSortBean>
        get() {
            val list: MutableList<MangaSortBean> = ArrayList()
            val bean1 = MangaSortBean()
            bean1.pathName = "最久更新"
            bean1.pathWord = "datetime_updated"
            list.add(bean1)
            val dateUpdateNearly = MangaSortBean()
            dateUpdateNearly.pathName = "最近更新"
            dateUpdateNearly.pathWord = "-datetime_updated"
            list.add(dateUpdateNearly)
            val bean2 = MangaSortBean()
            bean2.pathName = "最热"
            bean2.pathWord = "-popular"
            list.add(bean2)
            val unpopular = MangaSortBean()
            unpopular.pathName = "最冷"
            unpopular.pathWord = "popular"
            list.add(unpopular)
            return list
        }

    suspend fun getFilterData(
        offset: Int,
        theme: String? = "all",
        order: String? = "-popular",
    ): List<ListBeanManga> =
        withContext(Dispatchers.Default) {
            val url = ApiName.mangaFilterApi(offset, order, theme)
            val list: ArrayList<ListBeanManga> = ArrayList()
            val client = OkHttpClient()
            val request: Request = Request.Builder()
                .removeHeader(KeyWordSwap.USER_AGENT_WORD)
                .addHeader(KeyWordSwap.USER_AGENT_WORD, KeyWordSwap.FAKE_USER_AGENT)
                .headers(MangaInfoJson.headers)
                .url(url)
                .build()
            client.newCall(request).execute().use { response ->
                val json = response.body?.string()
                val jsonObject = JsonParser.parseString(json).asJsonObject
                    .getAsJsonObject("results")
                val array = jsonObject["list"].asJsonArray
                for (i in array) {
                    val comic = i.asJsonObject
                    val authorArray = comic["author"].asJsonArray
                    val listBeanManga = ListBeanManga()
                    listBeanManga.nameManga = comic["name"].asString
                    listBeanManga.urlCoverManga = comic["cover"].asString
                    listBeanManga.pathWordManga = comic["path_word"].asString
                    listBeanManga.authorManga = authorArray.authorNameReformation()
                    list.add(listBeanManga)
                }
                list
            }
        }
}