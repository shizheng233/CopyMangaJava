package com.shicheeng.copymanga.util

import android.content.Context
import android.os.Environment
import android.os.SystemClock
import androidx.core.net.toUri
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.shicheeng.copymanga.data.*
import com.shicheeng.copymanga.data.local.LocalChapter
import com.shicheeng.copymanga.data.local.LocalSavableMangaModel
import com.shicheeng.copymanga.resposity.MangaHistoryRepository
import com.shicheeng.copymanga.resposity.MangaInfoRepository
import com.shicheeng.copymanga.server.DownloadStateChapter
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import okhttp3.*
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

/**
 * 下载文件的逻辑，逻辑简单易懂。
 *
 * 大部分来自Kotatsu
 */
class FileUtil(
    private val historyRepository: MangaHistoryRepository,
    private val repository: MangaInfoRepository,
    private val context: Context,
    private val coroutineScope: CoroutineScope,
) {

    private val semaphore = Semaphore(2)
    val file =
        File("${context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)}/${KeyWordSwap.SAVED_LOCAL_CHAPTER_NAME}")

    fun downloadChapter(
        contentModel: LocalSavableMangaModel,
        startId: Int,
    ): DownloadJob<DownloadStateChapter> {
        val state = MutableStateFlow<DownloadStateChapter>(
            DownloadStateChapter.WAITING(startId, contentModel)
        )
        val job = coroutineScope.launch(Dispatchers.Default) {
            try {
                downloadFileImpl(contentModel, state, startId)
            } catch (e: CancellationException) {
                val stateAs = state.value
                if (stateAs !is DownloadStateChapter.CANCEL) {
                    state.value = DownloadStateChapter.CANCEL(startId, contentModel)
                }
                throw e
            }

        }
        return DownloadJob(job, state)
    }


    private suspend fun downloadFileImpl(
        contentModel: LocalSavableMangaModel,
        stateFlow: MutableStateFlow<DownloadStateChapter>,
        startId: Int,
    ) {
        stateFlow.value = DownloadStateChapter.PREPARE(startId, contentModel)
        semaphore.withPermit {
            try {
                val chapters = contentModel.list
                for ((index, chapter) in chapters.withIndex()) {
                    val urlsAndWord = repository.fetchContent(
                        chapter.comicPathWord,
                        chapter.uuid
                    )
                    for ((wordPosition, url) in urlsAndWord.withIndex()) {
                        val indexName = urlsAndWord[wordPosition].index.toString()
                        downloadFile(
                            url.url,
                            contentModel.mangaHistoryDataModel.name,
                            chapters[index].name,
                            indexName
                        )
                        stateFlow.value = DownloadStateChapter.DOWNLOADING(
                            chapterID = contentModel.hashCode(),
                            chapter = contentModel,
                            totalChapters = chapters.size,
                            currentChapter = index,
                            totalPages = urlsAndWord.size,
                            currentPage = wordPosition,
                            currentLocalChapter = chapter
                        )
                    }
                    historyRepository.updateLocalChapter(chapter.copy(isDownloaded = true))
                }
                stateFlow.value =
                    DownloadStateChapter.PostBeforeDone(startId, contentModel)
                chapters.forEach { mangaChapter ->
                    updateOrCreate(mangaChapter, file, contentModel.mangaHistoryDataModel.name)
                }
                downloadCover(
                    contentModel.mangaHistoryDataModel.url,
                    contentModel.mangaHistoryDataModel.name
                )
                stateFlow.value = DownloadStateChapter.DONE(startId, contentModel)
            } catch (e: CancellationException) {
                stateFlow.value = DownloadStateChapter.CANCEL(startId, contentModel)
                throw e
            } catch (e: Exception) {
                stateFlow.value =
                    DownloadStateChapter.ERROR(startId, contentModel, e)
            }
        }

    }

    /**
     * 文件下载
     */
    private suspend fun downloadFile(
        url: String,
        mangaName: String,
        chapterName: String,
        indexName: String,
    ) =
        withContext(Dispatchers.IO) {
            val okHttpClient = OkhttpHelper.getInstance()
            val request: Request = Request.Builder().url(url).build()
            val call = okHttpClient.newCall(request)
            val response = call.clone().await()
            val inputStream: InputStream = checkNotNull(response.body).byteStream()
            val buf = ByteArray(2048)
            var len: Int
            val savePath =
                "${context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)}/${mangaName}/${chapterName}"
            val file = File("${savePath}/${indexName}.png")
            if (!file.exists()) {
                if (file.parentFile?.exists() != true) file.parentFile?.mkdirs()
                if (file.parentFile?.canWrite() != true) file.parentFile?.setWritable(true)
                file.createNewFile()
            }
            val fos = FileOutputStream(file)
            while (inputStream.read(buf).also { len = it } != -1) {
                fos.write(buf, 0, len)
            }
            fos.flush()
            inputStream.close()
            fos.close()
        }

    private suspend fun updateOrCreate(
        chapter: LocalChapter,
        file: File,
        title: String,
    ) = withContext(Dispatchers.IO) {
        if (file.exists()) {
            updateMangaSavedInformation(chapter, file, title)
        } else {
            createFileOfDownload(chapter, file, title)
        }
    }

    private suspend fun updateMangaSavedInformation(
        chapter: LocalChapter,
        file: File,
        title: String,
    ) = withContext(Dispatchers.IO) {
        val jsonFile = file.readText().parserAsJson().asJsonArray

        val ts = jsonFile.toList().find { x ->
            x.asJsonObject["path_word"].asString == chapter.comicPathWord
        }
        if (ts == null) {
            val ss = createJson(chapter, title)
            jsonFile.add(ss)

        } else {

            val ss = ts.asJsonObject
            val array = ss["manga_downloaded"].asJsonArray
            val arrayObject = JsonObject()
            arrayObject.apply {
                addProperty("chapter_name", chapter.name)
                addProperty("uuid", chapter.uuid)
            }
            array.add(arrayObject)
            jsonFile.add(ss)
            jsonFile.remove(ts)
        }

        val fileOut = FileOutputStream(file)
        val inputS = jsonFile.toString().byteInputStream()
        inputS.copyTo(fileOut)
        fileOut.flush()
        inputS.close()
        fileOut.close()
    }


    private suspend fun createFileOfDownload(
        chapter: LocalChapter,
        file: File,
        title: String,
    ) = withContext(Dispatchers.IO) {
        val jsonFile = JsonArray()
        val main = createJson(chapter, title)
        jsonFile.add(main)
        val fileOut = FileOutputStream(file)
        val inputS = jsonFile.toString().byteInputStream()
        inputS.copyTo(fileOut)
        fileOut.flush()
        inputS.close()
        fileOut.close()
    }

    private suspend fun downloadCover(url: String, mangaName: String) =
        withContext(Dispatchers.IO) {
            val savePath =
                "${context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)}/${mangaName}/cover.png"
            val fileOutputStream = FileOutputStream(savePath)
            withGet(url) {
                it.copyTo(fileOutputStream)
            }
        }

    private fun createJson(chapter: LocalChapter, title: String): JsonObject {
        val main = JsonObject()
        main.addProperty("path_word", chapter.comicPathWord)
        main.addProperty("name", title)
        val array = JsonArray()
        val arrayObject = JsonObject()
        arrayObject.apply {
            addProperty("chapter_name", chapter.name)
            addProperty("uuid", chapter.uuid)
        }
        array.add(arrayObject)
        main.add("manga_downloaded", array)
        return main
    }

    fun isChapterDownloadedWithStringList(pathWord: String?, uuid: String?): Boolean {
        if (!file.exists()) {
            return false
        }
        val json = file.readText().parserAsJson().asJsonArray.find { x ->
            x.asJsonObject["path_word"].asString == pathWord
        }?.asJsonObject
        return json?.get("manga_downloaded")?.asJsonArray?.find { x -> x.asJsonObject["uuid"].asString == uuid }?.isJsonNull == false
    }

    fun findDownloadChapterInfo(pathWord: String?, uuid: String?): JsonObject? {
        if (!file.exists()) {
            return null
        }
        val json = file.readText().parserAsJson().asJsonArray.find { x ->
            x.asJsonObject["path_word"].asString == pathWord
        }?.asJsonObject
        return json?.get("manga_downloaded")?.asJsonArray?.find { x -> x.asJsonObject["uuid"].asString == uuid }?.asJsonObject
    }

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


    private fun findChapterPathWordWithName(name: String): String? {
        if (!file.exists()) {
            return null
        }
        val json = file.readText().parserAsJson().asJsonArray.find { x ->
            x.asJsonObject["name"].asString == name
        }?.asJsonObject
        return json?.get("path_word")?.asString
    }

    fun findChaptersWithPathWord(data: PersonalInnerDataModel): List<MangaInfoChapterDataBean> {
        if (!file.exists()) {
            return emptyList()
        }
        val json = file.readText().parserAsJson().asJsonArray.find { x ->
            x.asJsonObject["path_word"].asString == data.pathWord
        }?.asJsonObject
        checkNotNull(json) { "NON FIND JSON OF THIS MANGA" }
        val chapters = json.get("manga_downloaded")?.asJsonArray ?: return emptyList()
        val list = buildList {
            chapters.forEach {
                val chapter = it.asJsonObject
                val name = chapter["chapter_name"].asString
                val uuid = chapter["uuid"].asString
                val time = "LOCAL"
                val bean = MangaInfoChapterDataBean(
                    chapterTitle = name,
                    chapterTime = time,
                    uuidText = uuid,
                    readerProgress = null,
                    pathWord = data.pathWord ?: return emptyList(),
                    isSaved = true
                )
                add(bean)
            }
        }
        return list
    }

    fun ifChapterDownloaded(pathWord: String?, uuid: String?): List<MangaReaderPage> {
        if (!file.exists()) {
            return emptyList()
        }
        val json = file.readText().parserAsJson().asJsonArray.find { x ->
            x.asJsonObject["path_word"].asString == pathWord
        }?.asJsonObject
        val mangaName = json?.get("name")?.asString
        val chapterName = json?.get("manga_downloaded")?.asJsonArray?.find { x ->
            x.asJsonObject["uuid"].asString == uuid
        }?.asJsonObject?.get("chapter_name")?.asString
        val savePath =
            "${context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)}/${mangaName}/${chapterName}"
        val file = File(savePath)
        return buildList {
            file.listFiles()?.forEachIndexed { index, file ->
                if (file.extension == "png") {
                    add(MangaReaderPage(file.path, uuid, index))
                }
            }
        }
    }

}


class DownloadJob<P>(private val job: Job, private val progress: StateFlow<P>) : Job by job {
    val progressValue: P
        get() = progress.value

    fun progressAsFlow(): Flow<P> = progress
}

@OptIn(ExperimentalCoroutinesApi::class)
fun <T> Flow<T>.throttle(timeoutMillis: (T) -> Long): Flow<T> {
    var lastEmittedAt = 0L
    return transformLatest { value ->
        val delay = timeoutMillis(value)
        val now = SystemClock.elapsedRealtime()
        if (delay > 0L) {
            if (lastEmittedAt + delay < now) {
                delay(lastEmittedAt + delay - now)
            }
        }
        emit(value)
        lastEmittedAt = now
    }
}

fun Flow<DownloadStateChapter>.whileActive(): Flow<DownloadStateChapter> =
    transformWhile { state ->
        emit(state)
        !state.isTerminal
    }

private val DownloadStateChapter.isTerminal: Boolean
    get() = this is DownloadStateChapter.DONE || this is DownloadStateChapter.CANCEL || (this is DownloadStateChapter.ERROR)