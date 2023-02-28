package com.shicheeng.copymanga.json

import com.shicheeng.copymanga.BuildConfig
import com.shicheeng.copymanga.data.VersionUnit
import com.shicheeng.copymanga.data.isNormal
import com.shicheeng.copymanga.data.versionId
import com.shicheeng.copymanga.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import okhttp3.Request
import okhttp3.Response

class UpdateMetaDataJson {

    private val availableUpdate = MutableStateFlow<VersionUnit?>(null)

    fun availableUpdateVersion() = availableUpdate.asStateFlow()

    private suspend fun getUpdateInfoVersion(): List<VersionUnit> {
        val request = Request.Builder().url(ApiName.updateMetadata).build()
        val call = OkhttpHelper.getInstance().newCall(request)
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
            if (thisVersion.isNormal) {
                allVersion.retainAll { it.versionId.isNormal }
            }
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