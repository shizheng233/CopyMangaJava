package com.shicheeng.copymanga.util

import android.content.SharedPreferences
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Request.Builder
import okhttp3.RequestBody
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 各api网址
 */
@Singleton
class ApiName @Inject constructor() {

    @Inject
    lateinit var sharedPreferences: SharedPreferences
    val medKeyWord = sharedPreferences.getString("key_api_header_select", "copymanga.net")

    val headerTheKey = "https://api."
    val mangaSortUrl =
        "$headerTheKey$medKeyWord/api/v3/h5/filterIndex/comic/tags?platform=3&amp;format=json"
    private val mangaSearch =
        "$headerTheKey$medKeyWord/api/v3/search/comic?format=json&limit=21&offset=%s&platform=3&q=%s"
    private val mangaChapter =
        "$headerTheKey$medKeyWord/api/v3/comic/%s/group/default/chapters?limit=500&offset=0&platform=3"
    private val mangaPhoto =
        "$headerTheKey$medKeyWord/api/v3/comic/%s/chapter2/%s?platform=3"
    private val mangaCollect =
        " https://copymanga.net/api/v3/member/collect/comics?limit=50&offset=0&free_type=1&ordering=-datetime_modifier"
    private val mangaRecommend =
        "$headerTheKey$medKeyWord/api/v3/recs?pos=3200102&limit=21&offset=%s"
    private val mangaTopRank =
        "$headerTheKey$medKeyWord/api/v3/ranks?limit=21&offset=%s&date_type=%s"
    private val mangaInfoUrl =
        "$headerTheKey$medKeyWord/api/v3/comic2/%s?platform=3&amp;format=json"

    //&theme=%s&platform=3&format=json
    private val mangaFilterUrl =
        "$headerTheKey$medKeyWord/api/v3/comics?limit=21&offset=%s&ordering=%s"
    private val mangaNewest =
        "$headerTheKey$medKeyWord/api/v3/update/newest?limit=21&offset=%s"
    private val mangaFinish =
        "$headerTheKey$medKeyWord/api/v3/comics?limit=21&offset=%s&top=finish"
    private val mangaUserInfo = "https://copymanga.com/api/v2/web/user/info"
    private val mangaUser4Collect = "https://copymanga.net/api/v2/web/collect"
    private val mangaBrowses =
        "https://copymanga.com/api/kb/web/browses?limit=12&offset=0&free_type=1&format=json"


    /**
     * 漫画搜索的api
     *
     * @param name 漫画名称
     * @return 总api
     */
    fun mangaSearchApi(name: String?, offset: Int): String {
        return String.format(mangaSearch, offset, name)
    }

    /**
     * 获取章节
     *
     * @param chapter 其实是传入path word
     * @return String
     */
    fun mangaChapterApi(chapter: String?): String {
        return String.format(mangaChapter, chapter)
    }

    /**
     * 漫画内容获取
     *
     * @param path_word 漫画的path word
     * @param uuid      漫画的uuid
     * @return 总API
     */
    fun mangaPhotoApi(path_word: String?, uuid: String?): String {
        return String.format(mangaPhoto, path_word, uuid)
    }

    /**
     * 获取推荐的漫画列表
     *
     * @param offset 页数
     * @return 总api
     */
    fun mangaRecommendApi(offset: String?): String {
        return String.format(mangaRecommend, offset)
    }

    fun mangaRank(offset: String?, type: String?): String {
        return String.format(mangaTopRank, offset, type)
    }

    /**
     * 获取漫画的详细信息
     *
     * @param path_word 漫画的pathWord
     * @return 所有api
     */
    fun mangaInfoAdapter(path_word: String?): String {
        return String.format(mangaInfoUrl, path_word)
    }

    /**
     * 获取最新漫画
     *
     * @param offset 页数
     * @return 整个api
     */
    fun mangaNewestApi(offset: Int): String {
        return String.format(mangaNewest, offset)
    }

    /**
     * 获取已完结漫画
     *
     * @param offset 页数
     * @return 整个api
     */
    fun mangaFinishApi(offset: Int): String {
        return String.format(mangaFinish, offset)
    }
    //&theme=%s&platform=3&format=json
    /**
     * 过滤漫画
     *
     * @param offset 页数
     * @param order  排列顺序
     * @param theme  类型
     * @return 整个api
     */
    fun mangaFilterApi(
        offset: Int,
        order: String,
        theme: String?,
        top: String?,
    ): String {
        val formatString = String.format(mangaFilterUrl, offset, order)
        if (theme == null && top != null) {
            return "$formatString&top=$top"
        } else if (top == null && theme != null) {
            return "$formatString&theme=$theme"
        } else if (top != null) {
            return "$formatString&theme=$theme&top=$top"
        }
        return formatString
    }

    /**
     * 用户收藏漫画获取（需要设置headers['authorization']）:
     *
     * @return 返回json
     * @String authorization 传入authorization
     */
    @Throws(IOException::class)
    fun mangaCollectApi(authorization: String): String {
        val okHttpClient = OkHttpClient()
        val request: Request =
            Builder().url(mangaCollect).removeHeader("User-Agent").addHeader(
                "User-Agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/98.0.4758.102 Safari/537.36"
            ).addHeader("authorization", authorization).build()
        okHttpClient.newCall(request).execute()
            .use { response -> return response.body!!.string() }
    }

    /**
     * 用户信息获取
     * 需要“authorization”
     *
     * @param authorization 传入authorization
     * @return 获取到的信息
     * @throws IOException 错误一抛出
     */
    fun mangaUserinfoGet(authorization: String): String {
        val okHttpClient = OkHttpClient()
        val request: Request = Builder().url(mangaUserInfo)
            .removeHeader(KeyWordSwap.USER_AGENT_WORD)
            .addHeader(KeyWordSwap.USER_AGENT_WORD, KeyWordSwap.FAKE_USER_AGENT)
            .addHeader("authorization", authorization)
            .build()
        okHttpClient.newCall(request).execute()
            .use { response -> return response.body!!.string() }
    }

    /**
     * 收藏漫画
     *
     * @param mangaUUID     漫画的uuid
     * @param whatIs        漫画收藏的状态，1为收藏，0为取消收藏
     * @param authorization 传入authorization
     * @return 数据
     */
    fun manga4Collect(mangaUUID: String?, whatIs: Int, authorization: String): String {
        if (mangaUUID == null) {
            throw IllegalArgumentException("不正确的UUid")
        }
        val okHttpClient = OkHttpClient()
        val requestBody: RequestBody =
            FormBody.Builder().add("comic_id", mangaUUID).add("is_collect", whatIs.toString())
                .build()
        val request: Request =
            Builder().url(mangaUser4Collect).removeHeader(KeyWordSwap.USER_AGENT_WORD)
                .addHeader(KeyWordSwap.USER_AGENT_WORD, KeyWordSwap.FAKE_USER_AGENT)
                .addHeader("authorization", authorization).post(requestBody).build()
        okHttpClient.newCall(request).execute()
            .use { response -> return response.body!!.string() }
    }

    /**
     * 获取在网页端浏览过的漫画
     *
     * @param authorization 传authorization
     * @return 返回json
     * @throws IOException 错误一
     */
    @Throws(IOException::class)
    fun mangaBrowsesGet(authorization: String): String {
        val okHttpClient = OkHttpClient()
        val request: Request = Builder().url(mangaBrowses).removeHeader("User-Agent").addHeader(
            "User-Agent",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/98.0.4758.102 Safari/537.36"
        ).addHeader("authorization", authorization).build()
        okHttpClient.newCall(request).execute()
            .use { response -> return response.body!!.string() }
    }

}