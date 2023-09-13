package com.shicheeng.copymanga.resposity

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.shicheeng.copymanga.data.MangaSortBean
import com.shicheeng.copymanga.data.finished.Item
import com.shicheeng.copymanga.domin.CopyMangaApi
import com.shicheeng.copymanga.pagingsource.ExplorePagingSource
import com.shicheeng.copymanga.util.await
import com.shicheeng.copymanga.util.parserAsJson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MangaFilterRepository @Inject constructor(
    private val copyMangaApi: CopyMangaApi,
    private val okHttpClient: OkHttpClient,
    private val retrofit: Retrofit,
) {

    /**
     * 需要自己手动解析
     */
    suspend fun theme(): List<MangaSortBean> = withContext(Dispatchers.Default) {
        val request =
            Request.Builder()
                .url("https://" + retrofit.baseUrl().host + "/api/v3/h5/filterIndex/comic/tags")
                .build()
        val call = okHttpClient.newCall(request).await()
        call.body?.string()?.let {
            buildList {
                add(MangaSortBean("无", ""))
                it.parserAsJson().asJsonObject["results"].asJsonObject["theme"].asJsonArray.forEach {
                    val name = it.asJsonObject["name"].asString
                    val pathWord = it.asJsonObject["path_word"].asString
                    add(MangaSortBean(name, pathWord))
                }
            }
        } ?: emptyList()
    }

    fun filterMangas(
        top: String? = null,
        theme: String? = null,
        ordering: String? = null,
    ): Flow<PagingData<Item>> {
        return Pager(
            config = PagingConfig(pageSize = 21),
            pagingSourceFactory = {
                ExplorePagingSource(copyMangaApi, ordering, theme, top)
            }
        ).flow
    }


}

fun Any.logD(tag: String = "com.shihcheeng.logd") {
    Log.d(tag, "$this")
}