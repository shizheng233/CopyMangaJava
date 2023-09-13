package com.shicheeng.copymanga.json

import android.content.SharedPreferences
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.shicheeng.copymanga.data.BannerList
import com.shicheeng.copymanga.util.parserToJson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MainBannerJson @Inject constructor(
    private val sharedPreferences: SharedPreferences,
) {

    private val apiHeader
        get() = sharedPreferences.getString("key_api_header_select", "copymanga.net")
            ?: "copymanga.net"
    private val mainPageUrl =
        "https://api.$apiHeader/api/v3/h5/homeIndex?platform=3&amp;format=json"

    @Inject
    lateinit var okHttpClient: OkHttpClient

    suspend fun fetchMainListData(): JsonObject = withContext(Dispatchers.Default) {
        val request: Request = Request.Builder()
            .url(mainPageUrl)
            .build()
        okHttpClient.newCall(request).execute().use { response ->
            val jsonData = requireNotNull(response.body).string()
            jsonData.parserToJson().asJsonObject.getAsJsonObject("results")
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

    fun getRecTopic(jsonObject: JsonObject): JsonArray {
        return jsonObject["topics"].asJsonObject["list"].asJsonArray
    }

    fun getDayRankMain(jsonObject: JsonObject): HashMap<Int, JsonArray> {
        val map = HashMap<Int, JsonArray>()
        map[0] = jsonObject["rankDayComics"].asJsonObject.getAsJsonArray("list")
        map[1] = jsonObject["rankWeekComics"].asJsonObject.getAsJsonArray("list")
        map[2] = jsonObject["rankMonthComics"].asJsonObject.getAsJsonArray("list")
        return map
    }
}