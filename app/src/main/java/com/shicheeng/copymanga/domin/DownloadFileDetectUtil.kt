package com.shicheeng.copymanga.domin

import android.content.Context
import android.os.Environment
import androidx.core.net.toUri
import com.shicheeng.copymanga.data.PersonalInnerDataModel
import com.shicheeng.copymanga.util.FileUtil
import com.shicheeng.copymanga.util.KeyWordSwap
import com.shicheeng.copymanga.util.parserAsJson
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 将某些方法从[FileUtil]复制出来。用作单例。
 */
@Singleton
class DownloadFileDetectUtil @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    private val fileRootPath by lazy {
        File("${context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)}/${KeyWordSwap.SAVED_LOCAL_CHAPTER_NAME}")
    }

    /**
     * 通过[uuid]检测漫画章节是否下载。
     * @param pathWord 空安全的漫画PathWord。
     * @param uuid 空安全的漫画章节uuid。
     */
    fun detectChapterDownloadedByUUID(pathWord: String?, uuid: String?): Boolean {
        if (!fileRootPath.exists()) {
            return false
        }
        val json = fileRootPath.readText().parserAsJson().asJsonArray.find { x ->
            x.asJsonObject["path_word"].asString == pathWord
        }?.asJsonObject
        return json?.get("manga_downloaded")?.asJsonArray?.find { x -> x.asJsonObject["uuid"].asString == uuid }?.isJsonNull == false
    }

    /**
     * 找出下载过章节的漫画：通过读取[fileRootPath]的文件。
     */
    fun findDownloadManga(): Flow<List<PersonalInnerDataModel>> {
        val files = context
            .getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)?.listFiles() ?: return emptyFlow()
        val list = buildList {
            files.forEach {
                if (it.isDirectory) {
                    val model = PersonalInnerDataModel(
                        it.name,
                        ("${it.path}/cover.png").toUri(),
                        findChapterPathWordWithName(it.name)
                    )
                    add(model)
                }
            }
        }
        return flowOf(list)
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

}