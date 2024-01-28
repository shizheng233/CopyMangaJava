package com.shicheeng.copymanga.resposity

import com.shicheeng.copymanga.data.MangaHistoryDataModel
import com.shicheeng.copymanga.data.MangaReaderPage
import com.shicheeng.copymanga.data.MangaSortBean
import com.shicheeng.copymanga.data.chapter.toLocalChapter
import com.shicheeng.copymanga.data.info.MangaInfoDataModel
import com.shicheeng.copymanga.data.local.LocalChapter
import com.shicheeng.copymanga.domin.CopyMangaApi
import com.shicheeng.copymanga.domin.DownloadFileDetectUtil
import com.shicheeng.copymanga.fm.reader.ReaderMode
import com.shicheeng.copymanga.ui.screen.setting.SettingPref
import com.shicheeng.copymanga.util.formNumberToRead
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MangaInfoRepository @Inject constructor(
    private val detectUtil: DownloadFileDetectUtil,
    private val copyMangaApi: CopyMangaApi,
    private val mangaHistoryRepository: MangaHistoryRepository,
    private val settingPref: SettingPref,
) {

    suspend fun fetchMangaChapters(pathWord: String): List<LocalChapter> {
        return mangaHistoryRepository.fetchMangaChapterByPathWord(pathWord)
            ?.takeIf { it.isNotEmpty() }
            ?: copyMangaApi.fetchChapters(pathWord = pathWord).let {
                it.results.list.map { remoteChapter ->
                    remoteChapter.toLocalChapter(
                        readIndex = 0,
                        isReadInProgress = false,
                        isDownloaded = detectUtil.detectChapterDownloadedByUUID(
                            pathWord,
                            remoteChapter.uuid
                        ),
                        isReadFinish = false
                    )
                }
            }.also {
                mangaHistoryRepository.updateLocalChapter(it)
            }
    }

    suspend fun fetchMangaChaptersForce(pathWord: String): List<LocalChapter> {
        val mangaLocalChapters = mangaHistoryRepository.fetchMangaChapterByPathWord(pathWord)
        return copyMangaApi.fetchChapters(pathWord = pathWord).let {
            it.results.list.map { remoteChapter ->
                remoteChapter.toLocalChapter(
                    readIndex = mangaLocalChapters?.find { x ->
                        x.uuid == remoteChapter.uuid
                    }?.readIndex ?: 0,
                    isReadInProgress = mangaLocalChapters?.find { x ->
                        x.uuid == remoteChapter.uuid
                    }?.isReadProgress ?: false,
                    isDownloaded = detectUtil.detectChapterDownloadedByUUID(
                        pathWord,
                        remoteChapter.uuid
                    ),
                    isReadFinish = mangaLocalChapters?.find { x ->
                        x.uuid == remoteChapter.uuid
                    }?.isReadFinish ?: false
                )
            }
        }.also {
            mangaHistoryRepository.updateLocalChapter(it)
        }
    }

    suspend fun collect(comicId: String, isCollect: Boolean): Boolean {
        return try {
            copyMangaApi.comicCollect(comicId, isCollect = if (isCollect) 1 else 0)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun fetchMangaInfo(pathWord: String): MangaHistoryDataModel {
        return mangaHistoryRepository.getHistoryByMangaPathWord(pathWord)
            ?: copyMangaApi.getMangaInfo(pathWord = pathWord).toMangaLocalInfo(
                readerMode = ReaderMode.valueOf(settingPref.readerMode)
            ).also {
                mangaHistoryRepository.update(it)
            }
    }

    suspend fun fetchMangaInfoForce(pathWord: String): MangaHistoryDataModel {
        val oldHistory = mangaHistoryRepository.getHistoryByMangaPathWord(pathWord)
        return copyMangaApi.getMangaInfo(pathWord = pathWord).toMangaLocalInfo(
            readerMode = ReaderMode.idOf(oldHistory?.readerModeId)
                ?: ReaderMode.valueOf(settingPref.readerMode),
            isSubscribe = oldHistory?.isSubscribe ?: false
        ).also {
            mangaHistoryRepository.update(it)
        }
    }


    suspend fun fetchContent(
        pathWord: String,
        uuid: String,
    ): List<MangaReaderPage> {
        val url = copyMangaApi.fetchMangaContentPicture(pathWord, uuid).results.chapter
        return buildList {
            url.contents.forEachIndexed { index, c ->
                add(
                    MangaReaderPage(
                        url = c.url,
                        index = url.words[index],
                        uuid = url.uuid
                    )
                )
            }
        }.sortedBy {
            it.index
        }
    }

    fun fetchComicWebHistory(pathWord: String) = flow {
        val dataModel = copyMangaApi.comicWebHistory(pathWord)
        emit(dataModel)
    }

    suspend fun fetchContentMayLocal(
        localList: List<MangaReaderPage>? = null,
        pathWord: String,
        uuid: String,
    ): List<MangaReaderPage> = withContext(Dispatchers.Default) {
        if (localList != null) {
            val sortedList = localList.sortedWith { text1, text2 ->
                text1.url
                    .split("/")
                    .last()
                    .split("_")
                    .last()
                    .split(".")
                    .first()
                    .toInt()
                    .compareTo(
                        text2.url
                            .split("/")
                            .last()
                            .split("_")
                            .last()
                            .split(".")
                            .first()
                            .toInt()
                    )
            }
            val newList = buildList {
                for (i in sortedList.indices) {
                    add(sortedList[i].copy(index = i))
                }
            }
            newList
        } else {
            try {
                val url = copyMangaApi.fetchMangaContentPicture(pathWord, uuid).results.chapter
                buildList {
                    url.contents.forEachIndexed { index, c ->
                        add(
                            MangaReaderPage(
                                url = c.url,
                                index = url.words[index],
                                uuid = url.uuid
                            )
                        )
                    }
                }.sortedBy {
                    it.index
                }
            } catch (e: Exception) {
                e.printStackTrace()
                emptyList()
            }

        }
    }


}

fun MangaInfoDataModel.toMangaLocalInfo(
    readerMode: ReaderMode,
    isSubscribe: Boolean = false,
): MangaHistoryDataModel {
    return MangaHistoryDataModel(
        name = results.comic.name,
        time = System.currentTimeMillis(),
        url = results.comic.cover,
        pathWord = results.comic.pathWord,
        nameChapter = results.comic.lastChapter.name,
        positionChapter = 0,
        positionPage = 0,
        readerModeId = readerMode.id,
        mangaDetail = results.comic.brief,
        mangaLastUpdate = results.comic.datetimeUpdated ?: "未知",
        mangaPopularNumber = results.popular.toLong().formNumberToRead(),
        mangaRegion = results.comic.region.display,
        mangaStatus = results.comic.status.display,
        mangaStatusId = results.comic.status.value,
        themeList = results.comic.theme.map { MangaSortBean(it.name, it.pathWord) },
        authorList = results.comic.author,
        alias = results.comic.alias,
        isSubscribe = isSubscribe,
        comicUUID = results.comic.uuid
    )
}