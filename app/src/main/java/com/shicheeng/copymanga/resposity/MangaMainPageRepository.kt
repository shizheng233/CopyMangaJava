package com.shicheeng.copymanga.resposity

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.shicheeng.copymanga.data.DataBannerBean
import com.shicheeng.copymanga.data.ListBeanManga
import com.shicheeng.copymanga.data.MainPageDataModel
import com.shicheeng.copymanga.data.MainTopicDataModel
import com.shicheeng.copymanga.data.MangaRankMiniModel
import com.shicheeng.copymanga.json.MainBannerJson
import com.shicheeng.copymanga.util.authorNameReformation
import com.shicheeng.copymanga.util.formNumberToRead
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MangaMainPageRepository @Inject constructor(
    private val mainBannerJson: MainBannerJson,
) {

    private fun fetchMainBannerData(inputJsonObject: JsonObject): List<DataBannerBean> {
        val data = mainBannerJson.getBannerMain(inputJsonObject)
        return buildList {
            data.forEach {
                val bannerBean = DataBannerBean()
                val jsonObject1 = it.jsonObject //Banner组下面的各个jsonObject
                bannerBean.bannerBrief = jsonObject1["brief"].asString
                bannerBean.bannerImageUrl = jsonObject1["cover"].asString
                bannerBean.uuidManga = jsonObject1["comic"]
                    .asJsonObject["path_word"].asString
                add(bannerBean)
            }
        }
    }

    private fun fetchMainRowData(inputJsonArray: JsonArray): List<ListBeanManga> {
        return buildList {
            inputJsonArray.forEach { jsonElement ->
                //因为有第二个jsonObject，所以需要再次获取一次。
                val jsonObject1 = jsonElement.asJsonObject.getAsJsonObject("comic")
                val nameManga = jsonObject1["name"].asString
                val urlCoverManga = jsonObject1["cover"].asString
                val pathWordManga = jsonObject1["path_word"].asString
                val mangaAuthor = jsonObject1["author"].asJsonArray.takeIf { it.size() != 0 }
                    ?.authorNameReformation() ?: "未知"
                val beanManga = ListBeanManga(
                    nameManga = nameManga,
                    authorManga = mangaAuthor,
                    urlCoverManga = urlCoverManga,
                    pathWordManga = pathWordManga
                )
                add(beanManga)
            }
        }
    }

    private fun fetchMainRowData2(inputJsonArray: JsonArray): List<ListBeanManga> {
        return buildList {
            inputJsonArray.forEach { jsonElement ->
                val jsonObject1 = jsonElement.asJsonObject
                val nameManga = jsonObject1["name"].asString
                val urlCoverManga = jsonObject1["cover"].asString
                val pathWordManga = jsonObject1["path_word"].asString
                val mangaAuthorList = jsonObject1["author"].asJsonArray.authorNameReformation()
                val beanManga =
                    ListBeanManga(nameManga, mangaAuthorList, urlCoverManga, pathWordManga)
                add(beanManga)
            }
        }
    }

    private fun transformMainRecTopic(inputJsonArray: JsonArray): List<MainTopicDataModel> {
        return inputJsonArray.map { element ->
            element.asJsonObject.let {
                val title = it["title"].asString
                val journal = it["journal"].asString
                val coverUrl = it["cover"].asString
                val period = it["period"].asString
                val type = it["type"].asInt
                val brief = it["brief"].asString
                val pathWord = it["path_word"].asString
                val time = it["datetime_created"].asString
                MainTopicDataModel(
                    name = title,
                    journal = journal,
                    coverUrl = coverUrl,
                    period = period,
                    type = type,
                    brief = brief,
                    pathWord = pathWord,
                    datetimeCreated = time
                )
            }
        }
    }

    private fun parserJsonLeaderBoardData(array: JsonArray?): List<MangaRankMiniModel> {
        return buildList {
            array?.forEach { jsonElement ->
                val comic = jsonElement.asJsonObject["comic"].asJsonObject
                val popular = comic["popular"].asLong.formNumberToRead()
                val name = comic["name"].asString
                val pathWord = comic["path_word"].asString
                val author = comic["author"].asJsonArray.authorNameReformation()
                val cover = comic["cover"].asString
                val riseNum = jsonElement.asJsonObject["rise_num"].asLong.formNumberToRead()
                val data = MangaRankMiniModel(name, author, cover, popular, riseNum, pathWord)
                add(data)
            }
        }

    }

    /**
     * 主页数据
     */
    suspend fun fetchMainData() = withContext(Dispatchers.Default) {
        val mainData = mainBannerJson.fetchMainListData()
        val listBanner = fetchMainBannerData(mainData)
        val listRecommend = fetchMainRowData(mainBannerJson.getRecMain(mainData))
        val listNewest = fetchMainRowData(mainBannerJson.getNewMain(mainData))
        val listHot = fetchMainRowData(mainBannerJson.getHotMain(mainData))
        val listFinished = fetchMainRowData2(mainBannerJson.getFinishMain(mainData))
        val mapRankJsonArray = mainBannerJson.getDayRankMain(mainData)
        val listRankWeek = parserJsonLeaderBoardData(mapRankJsonArray[1])
        val listRankDay = parserJsonLeaderBoardData(mapRankJsonArray[0])
        val listRankMonth = parserJsonLeaderBoardData(mapRankJsonArray[2])
        val topic = transformMainRecTopic(mainBannerJson.getRecTopic(mainData))
        MainPageDataModel(
            listBanner = listBanner,
            listRecommend = listRecommend,
            listRankDay = listRankDay,
            listRankWeek = listRankWeek,
            listRankMonth = listRankMonth,
            listHot = listHot,
            listNewest = listNewest,
            listFinished = listFinished,
            topicList = topic
        )
    }

}

