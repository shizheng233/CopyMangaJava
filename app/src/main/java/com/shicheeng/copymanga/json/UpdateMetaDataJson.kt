package com.shicheeng.copymanga.json

import com.shicheeng.copymanga.BuildConfig
import com.shicheeng.copymanga.data.VersionUnit
import com.shicheeng.copymanga.data.versionId
import com.shicheeng.copymanga.util.asArrayList
import com.shicheeng.copymanga.util.await
import com.shicheeng.copymanga.util.parserAsJson
import com.shicheeng.copymanga.util.timeStampConvert
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UpdateMetaDataJson @Inject constructor(
    private val okHttpClient: OkHttpClient,
) {

    private val updateMetadata =
        "https://api.github.com/repos/shizheng233/CopyMangaJava/releases?page=1&per_page=10"

    private val availableUpdate = MutableStateFlow<VersionUnit?>(null)

    fun availableUpdateVersion() = availableUpdate.asStateFlow()

    private suspend fun getUpdateInfoVersion(): List<VersionUnit> {
        val request = Request.Builder().url(updateMetadata).build()
        val call = okHttpClient.newCall(request)
        val res = call.await()
        return buildList {
            mapToList(res) {
                add(it)
            }
        }
    }


    suspend fun fetchUpdate(): VersionUnit? = withContext(Dispatchers.Default) {
        runCatching {
            val thisVersion = versionId(BuildConfig.VERSION_NAME)
            val allVersion = getUpdateInfoVersion().asArrayList()
            allVersion.sortBy { it.versionId }
            allVersion.maxByOrNull { it.versionId }?.takeIf {
                it.versionId > thisVersion
            }
        }.onFailure {
            it.printStackTrace()
        }.onSuccess {
            availableUpdate.emit(it)
        }.getOrNull()
    }

    private inline fun mapToList(
        response: Response,
        crossinline block: (VersionUnit) -> Unit,
    ) {
        val json = response.body?.string()?.parserAsJson()?.asJsonArray ?: return
        for (element in json) {
            val singleObj = element.asJsonObject
            val arrest = singleObj["assets"].asJsonArray[0].asJsonObject
            val versionName = singleObj["tag_name"].asString
            val url = arrest["browser_download_url"].asString
            val apkSize = arrest["size"].asLong
            val htmlUrl = singleObj["html_url"].asString
            val id = singleObj["id"].asLong
            val time = singleObj["published_at"].asString.timeStampConvert()
            val description = singleObj["body"].asString
            val versionUnit = VersionUnit(id, htmlUrl, versionName, url, apkSize, description, time)
            block(versionUnit)
        }
    }

}