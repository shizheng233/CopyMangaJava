package com.shicheeng.copymanga.server.download.domin

import com.shicheeng.copymanga.data.local.LocalChapter
import com.shicheeng.copymanga.data.local.LocalSavableMangaModel
import com.shicheeng.copymanga.fm.domain.makeDirIfNoExist
import com.shicheeng.copymanga.util.KeyWordSwap
import kotlinx.coroutines.runInterruptible
import java.io.File

class DownloaderOutPutter(
    private val rootFile: File,
    private val localSavableMangaModel: LocalSavableMangaModel,
) {

    private val rootFileDir = rootFile.makeDirIfNoExist()
    private val downloaderIndexer = DownloaderLocalIndex {
        File(rootFileDir, KeyWordSwap.LOCAL_SAVABLE_INDEX_JSON)
            .also {
                it.createNewFile()
            }
            .takeIf { x -> x.exists() && x.canRead() }
            ?.readText()
    }

    init {
        downloaderIndexer.setMangaData(localSavableMangaModel, append = true)
    }

    suspend fun addCover(file: File, ext: String) {
        val name = buildString {
            append("cover")
            if (ext.isNotEmpty() && ext.length < 4) {
                append(".")
                append(ext)
            }
        }
        runInterruptible {
            file.copyTo(File(rootFile, name), overwrite = true)
        }
        downloaderIndexer.setCoverEntry(name)
        completedIndex()
    }

    suspend fun addPager(
        localChapter: LocalChapter,
        file: File,
        pagerNumber: Int,
        ext: String,
    ) {
        val name = buildString {
            append("/")
            append(localChapter.name)
            append("/")
            append("${localChapter.name}_")
            append(pagerNumber)
            if (ext.isNotEmpty() && ext.length < 4) {
                append(".")
                append(ext)
            }
        }
        runInterruptible {
            file.copyTo(File(rootFile, name), overwrite = true)
        }
        downloaderIndexer.addChapter(localChapter, name)
    }

    fun getDownloadChapters(array: Array<String>) = downloaderIndexer.getChapters(
        uuid = array,
        localSavableMangaModel = localSavableMangaModel
    )

    fun createNewLocalData(uuids: Array<String>): LocalSavableMangaModel {
        return downloaderIndexer.getMangaData()?.let {
            LocalSavableMangaModel(it, getDownloadChapters(uuids))
        } ?: error("出错")
    }

    private suspend fun completedIndex() = runInterruptible {
        File(rootFile, KeyWordSwap.LOCAL_SAVABLE_INDEX_JSON).writeText(downloaderIndexer.toString())
    }

    suspend fun cleanUp() {
        completedIndex()
    }

}