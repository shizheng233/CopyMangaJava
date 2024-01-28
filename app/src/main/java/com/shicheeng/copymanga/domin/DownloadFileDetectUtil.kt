package com.shicheeng.copymanga.domin

import android.content.Context
import android.os.Environment
import androidx.core.net.toUri
import com.shicheeng.copymanga.data.MangaReaderPage
import com.shicheeng.copymanga.data.PersonalInnerDataModel
import com.shicheeng.copymanga.data.local.LocalSavableMangaModel
import com.shicheeng.copymanga.resposity.MangaHistoryRepository
import com.shicheeng.copymanga.util.KeyWordSwap
import com.shicheeng.copymanga.util.asStringOrNull
import com.shicheeng.copymanga.util.getOrNull
import com.shicheeng.copymanga.util.nullWillBe
import com.shicheeng.copymanga.util.parserAsJson
import com.shicheeng.copymanga.util.transformToJsonObjectSafety
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runInterruptible
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileFilter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DownloadFileDetectUtil @Inject constructor(
    @ApplicationContext private val context: Context,
    private val mangaHistoryRepository: MangaHistoryRepository,
) {

    private val fileRootPath by lazy {
        File("${context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)}/${KeyWordSwap.SAVED_LOCAL_CHAPTER_NAME}")
    }

    private val fileRootPathV2 by lazy {
        context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
    }

    private val allDownloadFiles by lazy { fileRootPathV2?.walk() }

    /**
     * 获取下载的地址，一般是下载主文件夹名字和漫画名字
     * @param localSavableMangaModel 本地保存的漫画信息数据模型
     */
    fun getRootFile(localSavableMangaModel: LocalSavableMangaModel): File {
        return File(
            context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),
            "/${localSavableMangaModel.mangaHistoryDataModel.name}"
        )
    }

    /**
     * 通过[uuid]检测漫画章节是否下载。
     * @param pathWord 空安全的漫画PathWord。
     * @param uuid 空安全的漫画章节uuid。
     */
    // TODO: 章节检测不再使用本地暴力检测
    suspend fun detectChapterDownloadedByUUID(
        pathWord: String?,
        uuid: String?,
    ): Boolean = runInterruptible(Dispatchers.IO) {
        if (!fileRootPath.exists()) {
            return@runInterruptible false
        }
        val json = fileRootPath.readText().parserAsJson().asJsonArray.find { x ->
            x.asJsonObject["path_word"].asString == pathWord
        }?.asJsonObject
        if (json != null) {
            json.get("manga_downloaded")
                ?.asJsonArray
                ?.find { x -> x.asJsonObject["uuid"].asString == uuid }
                ?.isJsonNull == false
        } else {
            val jsonV2 = allDownloadFiles
                ?.filter { it.extension == "json" }
                ?.find {
                    it.readText().parserAsJson().transformToJsonObjectSafety()
                        ?.get("path_word")?.asString == pathWord
                }?.readText()?.parserAsJson()?.asJsonObject
            val chapterInJson = if (jsonV2?.has("chapters") == true) {
                jsonV2.get("chapters")?.asJsonObject
            } else null
            chapterInJson?.has(uuid) == true
        }
    }

    suspend fun detectMangaDownloadWithName(
        name: String,
        pathWord: String?,
        uuid: String,
    ): Boolean = withContext(Dispatchers.IO) {
        if (!fileRootPath.exists()) {
            return@withContext false
        }
        val json = fileRootPath.readText().parserAsJson().asJsonArray.find { x ->
            x.asJsonObject["path_word"].asString == pathWord
        }?.asJsonObject
        if (json != null) {
            json.get("manga_downloaded")
                ?.asJsonArray
                ?.find { x -> x.asJsonObject["uuid"].asString == uuid }
                ?.isJsonNull == false
        } else {
            val mangaPath = File(fileRootPathV2, "/$name/${KeyWordSwap.LOCAL_SAVABLE_INDEX_JSON}")
            if (!mangaPath.exists()) {
                return@withContext false
            } else {
                val jsonMangaIndex =
                    mangaPath.readText().parserAsJson().transformToJsonObjectSafety()
                return@withContext jsonMangaIndex?.has(uuid) == true
            }
        }
    }

    suspend fun detectMangaDownloadWithChapterName(
        name: String,
        pathWord: String?,
        uuid: String,
    ): Boolean = withContext(Dispatchers.IO) {
        if (!fileRootPath.exists()) {
            return@withContext false
        }
        val json = fileRootPath.readText().parserAsJson().asJsonArray.find { x ->
            x.asJsonObject["path_word"].asString == pathWord
        }?.asJsonObject
        if (json != null) {
            json.get("manga_downloaded")
                ?.asJsonArray
                ?.find { x -> x.asJsonObject["uuid"].asString == uuid }
                ?.isJsonNull == false
        } else {
            val mangaPath = File(fileRootPathV2, "/$name/${KeyWordSwap.LOCAL_SAVABLE_INDEX_JSON}")
            if (!mangaPath.exists()) {
                return@withContext false
            } else {
                val jsonMangaIndex =
                    mangaPath.readText().parserAsJson().transformToJsonObjectSafety()
                return@withContext jsonMangaIndex?.has(uuid) == true
            }
        }
    }

    /**
     * 找出下载过章节的漫画：通过读取[fileRootPath]和[fileRootPathV2]的文件。
     */
    fun findDownloadManga() = flow {
        val files = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
            ?.listFiles()
        if (files == null) {
            emit(emptyList())
            return@flow
        }
        val list = files
            .filter { it.isDirectory }
            .map { file ->
                val indexJson = File("${file.path}/${KeyWordSwap.LOCAL_SAVABLE_INDEX_JSON}").takeIf {
                    it.canRead()
                }?.readText()?.parserAsJson()?.transformToJsonObjectSafety()
                val coverPath = indexJson?.getOrNull("cover_entry")?.asString.nullWillBe {
                    "cover.png"
                }
                PersonalInnerDataModel(
                    name = file.name,
                    url = ("${file.path}/$coverPath").toUri(),
                    pathWord = findChapterPathWordWithName(file.name)
                        ?: indexJson?.getOrNull("path_word")?.asStringOrNull
                )
            }
        emit(list)
    }


    /**
     * 通过读取文件来获取漫画的pathWord。
     * @param name 既是漫画名字也是文件夹的名字。
     */
    private fun findChapterPathWordWithName(name: String): String? {
        if (!fileRootPath.exists()) {
            return null
        }
        val json = fileRootPath.readText().parserAsJson().asJsonArray.find { x ->
            x.asJsonObject["name"].asString == name
        }?.asJsonObject
        return json?.get("path_word")?.asString
    }

    suspend fun isChapterDownloadedWithStringList(
        pathWord: String?,
        uuid: String?,
    ): Boolean = runInterruptible(Dispatchers.IO) {
        if (!fileRootPath.exists()) {
            return@runInterruptible false
        }
        val json = fileRootPath.readText().parserAsJson().asJsonArray.find { x ->
            x.asJsonObject["path_word"].asString == pathWord
        }?.asJsonObject
        val jsonNewVersion = fileRootPathV2?.walk()?.filter {
            it.extension == "json"
        }?.find { x ->
            x.readText()
                .parserAsJson()
                .transformToJsonObjectSafety()?.get("path_word")?.asString == pathWord
        }?.readText()?.parserAsJson()
            ?.asJsonObject?.let {
                if (it.has("chapters")) it.get("chapters").asJsonObject else null
            }
        json?.get("manga_downloaded")
            ?.asJsonArray?.find { x -> x.asJsonObject["uuid"].asString == uuid }
            ?.isJsonNull == false || jsonNewVersion?.has(uuid) == true
    }

    //TODO : 完美的漫画本地检测
    suspend fun ifChapterDownloaded(
        pathWord: String,
        uuid: String?,
    ): List<MangaReaderPage>? = runInterruptible(Dispatchers.IO) {
        if (!fileRootPath.exists() || fileRootPathV2?.exists() == false) {
            return@runInterruptible null
        }
        val json = File(fileRootPath, KeyWordSwap.SAVED_LOCAL_CHAPTER_NAME)
            .takeIf { it.exists() }
            ?.readText()
            ?.parserAsJson()
            ?.asJsonArray?.find { x ->
                x.asJsonObject["path_word"].asString == pathWord
            }?.asJsonObject
        if (json != null) {
            val mangaName = json["name"]?.asString
            val chapterName = json["manga_downloaded"]?.asJsonArray?.find { x ->
                x.asJsonObject["uuid"].asString == uuid
            }?.asJsonObject?.get("chapter_name")?.asString
            val savePath =
                "${context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)}/${mangaName}/${chapterName}"
            val file = File(savePath)
            buildList {
                file.listFiles(chaptersFileFilter)?.forEachIndexed { index, file ->
                    add(MangaReaderPage(file.path, uuid, index))
                }
            }
        } else {
            val jsonInner = allDownloadFiles
                ?.filter { it.extension == "json" }
                ?.find {
                    it.readText().parserAsJson().transformToJsonObjectSafety()
                        ?.get("path_word")?.asString == pathWord
                }?.readText()?.parserAsJson()?.asJsonObject ?: return@runInterruptible null
            if (!jsonInner.has("chapters") && !jsonInner.get("chapters").asJsonObject.has(uuid)) {
                return@runInterruptible null
            } else {
                val mangaName = jsonInner.get("name").asString
                val chapterName = jsonInner
                    .get("chapters").asJsonObject
                    .get(uuid).asJsonObject["chapter_name"].asString
                val file = File(fileRootPathV2, "${mangaName}/$chapterName")
                buildList {
                    file.listFiles(chaptersFileFilter)?.forEachIndexed { index, fileInner ->
                        add(MangaReaderPage(fileInner.path, uuid, index))
                    }
                }
            }

        }

    }

    private val chaptersFileFilter = FileFilter {
        it.extension == "jpg"
                || it.extension == "webp"
                || it.extension == "jpg"
                || it.extension == "jpeg"
    }

}