package com.shicheeng.copymanga.json

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.shicheeng.copymanga.data.BannerList
import com.shicheeng.copymanga.json.MangaInfoJson.headers
import com.shicheeng.copymanga.util.ApiName
import com.shicheeng.copymanga.util.KeyWordSwap
import com.shicheeng.copymanga.util.OkhttpHelper
import okhttp3.OkHttpClient
import okhttp3.Request

object MainBannerJson {


    val mainList: JsonObject
        get() {
            val client = OkhttpHelper.getInstance()
            val request: Request = Request.Builder().url(ApiName.mangaMainPage)
                .removeHeader(KeyWordSwap.USER_AGENT_WORD)
                .addHeader(KeyWordSwap.USER_AGENT_WORD, KeyWordSwap.FAKE_USER_AGENT)
                .headers(headers)
                .build()
            client.newCall(request).execute().use { response ->
                assert(response.body != null)
                val jsonData = response.body!!.string()
                return JsonParser.parseString(jsonData).asJsonObject.getAsJsonObject("results")
            }
        }

    fun getBannerMain(jsonObject: JsonObject): ArrayList<BannerList> {
        val array1 = jsonObject["banners"].asJsonArray
        val bannerLists = ArrayList<BannerList>()
        for (ele in array1) {
            val element = ele.asJsonObject["type"]
            if (element.asInt == 1) {
                val list = BannerList()
                list.jsonObject = ele.asJsonObject
                bannerLists.add(list)
            }
        }
        return bannerLists
    }

    fun getRecMain(jsonObject: JsonObject): JsonArray =
        jsonObject["recComics"].asJsonObject.getAsJsonArray("list")

    fun getHotMain(jsonObject: JsonObject): JsonArray = jsonObject["hotComics"].asJsonArray

    fun getNewMain(jsonObject: JsonObject): JsonArray = jsonObject["newComics"].asJsonArray

    fun getFinishMain(jsonObject: JsonObject): JsonArray {
        return jsonObject.getAsJsonObject("finishComics").getAsJsonArray("list")
    }

    fun getDayRankMain(jsonObject: JsonObject): HashMap<Int, JsonArray> {
        val map = HashMap<Int, JsonArray>()
        map[0] = jsonObject["rankDayComics"].asJsonObject.getAsJsonArray("list")
        map[1] = jsonObject["rankWeekComics"].asJsonObject.getAsJsonArray("list")
        map[2] = jsonObject["rankMonthComics"].asJsonObject.getAsJsonArray("list")
        return map
    }
}