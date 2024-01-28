package com.shicheeng.copymanga.server.download.woker

import android.app.NotificationManager
import android.content.Context
import android.content.pm.ServiceInfo
import android.os.Build
import android.webkit.MimeTypeMap
import androidx.core.content.ContextCompat
import androidx.hilt.work.HiltWorker
import androidx.lifecycle.asFlow
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.ForegroundInfo
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.await
import com.shicheeng.copymanga.data.LocalManga
import com.shicheeng.copymanga.domin.DownloadFileDetectUtil
import com.shicheeng.copymanga.fm.domain.PagerCache
import com.shicheeng.copymanga.fm.domain.makeDirIfNoExist
import com.shicheeng.copymanga.resposity.MangaHistoryRepository
import com.shicheeng.copymanga.resposity.MangaInfoRepository
import com.shicheeng.copymanga.resposity.logD
import com.shicheeng.copymanga.server.download.domin.DownloadState
import com.shicheeng.copymanga.server.download.domin.DownloaderOutPutter
import com.shicheeng.copymanga.server.download.domin.PausingHandle
import com.shicheeng.copymanga.server.download.domin.PausingHandler
import com.shicheeng.copymanga.ui.screen.setting.SettingPref
import com.shicheeng.copymanga.util.Throttler
import com.shicheeng.copymanga.util.await
import com.shicheeng.copymanga.util.messageNoNull
import com.shicheeng.copymanga.util.progress.TimeLeftEstimator
import com.shicheeng.copymanga.util.useWithContext
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.buffer
import okio.sink
import java.io.File
import java.io.IOException
import java.util.UUID
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltWorker
class DownloadedWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val mangaHistoryRepository: MangaHistoryRepository,
    private val pagerCache: PagerCache,
    private val mangaInfoRepository: MangaInfoRepository,
    private val detectUtil: DownloadFileDetectUtil,
    private val okHttpClient: OkHttpClient,
    downloadNotificationFactory: DownloadNotificationFactory.Injket,
) : CoroutineWorker(appContext, params) {

    private val notificationFactory = downloadNotificationFactory.create(params.id)
    private val notificationManager = appContext.getSystemService(NotificationManager::class.java)
    private val mutex = Mutex()
    private val throttler = Throttler(400)
    private val pausingHandle = PausingHandle()
    private val pausingHandler = PausingHandler(params.id, pausingHandle)
    private val timeLeftEstimator = TimeLeftEstimator()

    @Volatile
    private var _lastState: DownloadState? = null

    private val lastState: DownloadState
        get() = checkNotNull(_lastState)


    override suspend fun doWork(): Result {
        setForeground(getForegroundInfo())
        val mangaPathWord = inputData.getString(MANGA_PATH_WORD)
            ?: return Result.failure()
        val manga = mangaHistoryRepository.getMangaByPathWord(mangaPathWord)
            ?: return Result.failure()
        val downloadedChapters = getDoneChapters()
        val mangaDownloadUUIDs = inputData.getStringArray(MANGA_DOWNLOAD_UUIDS)
            ?.takeUnless { it.isEmpty() }
        _lastState = DownloadState(
            localSavableMangaModel = manga,
            isIndeterminate = true
        )

        return try {
            downloadMangaImpl(
                downloadUUID = mangaDownloadUUIDs,
                downloadedUUID = downloadedChapters
            )
            Result.success()
        } catch (e: CancellationException) {
            withContext(NonCancellable) {
                val notification =
                    notificationFactory.buildNotification(lastState.copy(isStopped = true))
                notificationManager.notify(id.hashCode(), notification)
            }
            throw e
        } catch (e: IOException) {
            e.printStackTrace()
            Result.retry()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(
                lastState.copy(
                    error = e.message,
                ).transformToWorkData()
            )
        } finally {
            notificationManager.cancel(id.hashCode())
        }
    }

    private suspend fun downloadMangaImpl(
        downloadUUID: Array<String>?,
        downloadedUUID: Array<String>,
    ) {
        requireNotNull(downloadUUID) {
            "下载的章节不可以为空"
        }
        val manga = lastState.localSavableMangaModel
        val chapterToSkip = downloadedUUID.toMutableList()
        mutex.withLock {
            ContextCompat.registerReceiver(
                applicationContext,
                pausingHandler,
                PausingHandler.createIntentFilter(id),
                ContextCompat.RECEIVER_NOT_EXPORTED
            )
            val filePath = detectUtil.getRootFile(manga)
            val tmpFile = "${manga.mangaHistoryDataModel.name}_$id.tmp"
            val outPut: DownloaderOutPutter?
            try {
                outPut = DownloaderOutPutter(filePath, manga)
                val coverFile = manga.mangaHistoryDataModel.url
                downloadFile(url = coverFile, path = filePath, tmpFile = tmpFile).let {
                    outPut.addCover(file = it, MimeTypeMap.getFileExtensionFromUrl(coverFile))
                }
                val chapters = manga.list.filter {
                    downloadUUID.contains(it.uuid)
                }
                for ((chapterIndex, chapter) in chapters.withIndex()) {
                    if (chapterToSkip.remove(chapter.uuid)) {
                        pushState(
                            lastState.copy(downloadedChapters = lastState.downloadedChapters + chapter.uuid)
                        )
                        continue
                    }
                    val pagerInfo = runDownloadPausingDetect(pausingHandle) {
                        mangaInfoRepository.fetchContentMayLocal(
                            localList = null,
                            pathWord = chapter.comicPathWord,
                            uuid = chapter.uuid
                        )
                    }
                    for ((pagerIndex, pager) in pagerInfo.withIndex()) {
                        runDownloadPausingDetect(pausingHandle) {
                            val page = pagerCache.get(url = pager.url)
                                ?: downloadFile(url = pager.url, path = filePath, tmpFile)
                            outPut.addPager(
                                localChapter = chapter,
                                file = page,
                                pagerNumber = pager.index,
                                ext = MimeTypeMap.getFileExtensionFromUrl(pager.url)
                            )
                            pushState(
                                lastState.copy(
                                    totalChapters = chapters.size,
                                    currentChapter = chapterIndex,
                                    isIndeterminate = false,
                                    totalPages = pagerInfo.size,
                                    currentPage = pagerIndex
                                )
                            )
                        }
                    }
                    pushState(
                        lastState.copy(
                            downloadedChapters = lastState.downloadedChapters + chapter.uuid
                        )
                    )
                    mangaHistoryRepository.updateLocalChapter(
                        chapter.copy(isDownloaded = true)
                    )
                }
                pushState(
                    lastState.copy(isIndeterminate = true)
                )
                val localManga = outPut.createNewLocalData(downloadUUID)
                pushState(
                    lastState.copy(localManga = LocalManga(localManga, filePath))
                )
                outPut.cleanUp()
            } catch (e: Exception) {
                if (e !is CancellationException) {
                    pushState(
                        lastState.copy(error = e.message)
                    )
                }
                throw e
            } finally {
                withContext(NonCancellable) {
                    applicationContext.unregisterReceiver(pausingHandler)
                    File(filePath, tmpFile).apply {
                        withContext(Dispatchers.IO) {
                            delete() || deleteRecursively()
                        }
                    }
                }
            }
        }
    }

    private suspend fun downloadFile(
        url: String,
        path: File,
        tmpFile: String,
    ): File {
        val request: Request = Request.Builder().url(url).build()
        val call = okHttpClient.newCall(request)
        val response = call.clone().await()
        val file = File(path, tmpFile).also {
            withContext(Dispatchers.IO) {
                it.createNewFile()
            }
        }
        checkNotNull(response.body).use { body ->
            file.sink(append = false).buffer().useWithContext(Dispatchers.IO) {
                it.writeAll(body.source())
            }
        }
        return file
    }

    override suspend fun getForegroundInfo(): ForegroundInfo {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ForegroundInfo(
                id.hashCode(),
                notificationFactory.buildNotification(_lastState),
                ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC,
            )
        } else {
            ForegroundInfo(
                id.hashCode(),
                notificationFactory.buildNotification(_lastState),
            )
        }
    }

    private suspend fun getDoneChapters(): Array<String> {
        val work = WorkManager.getInstance(applicationContext).getWorkInfoById(id).await()
            ?: return emptyArray()
        return DownloadState downloadChaptersIn work.progress
    }

    private suspend fun pushState(state: DownloadState) {
        val previous = lastState
        _lastState = state
        if (previous.isParticularProgress && state.isParticularProgress) {
            timeLeftEstimator.tick(state.progress, state.max)
        } else {
            timeLeftEstimator.emptyTick()
            throttler.reset()
        }
        val notification = notificationFactory.buildNotification(state)
        if (state.isFinalState) {
            notificationManager.notify(id.toString(), id.hashCode(), notification)
        } else if (throttler.throttle()) {
            notificationManager.notify(id.hashCode(), notification)
        } else {
            return
        }
        setProgress(data = state.transformToWorkData())
    }

    private suspend fun <R> runDownloadPausingDetect(
        pausingHandle: PausingHandle,
        block: suspend () -> R,
    ): R {
        if (pausingHandle.isPaused) {
            pushState(lastState.copy(isPaused = true))
            pausingHandle.awaitResumed()
            pushState(lastState.copy(isPaused = false))
        }
        var countDown = MAX_FAILSAFE_ATTEMPTS
        detect@ while (true) {
            try {
                return block()
            } catch (e: IOException) {
                if (countDown <= 0) {
                    pushState(lastState.copy(isPaused = true, error = e.messageNoNull))
                    countDown = MAX_FAILSAFE_ATTEMPTS
                    pausingHandle.pause()
                    pausingHandle.awaitResumed()
                    pushState(lastState.copy(isPaused = false, error = null))
                } else {
                    countDown--
                    delay(200L)
                }
            }
        }
    }


    class Caller @Inject constructor(
        @ApplicationContext private val context: Context,
        private val workManager: WorkManager,
        private val setting: SettingPref,
    ) {

        suspend fun download(pathWord: String, downloadUUIDs: Array<String>) {
            if (downloadUUIDs.isEmpty()) return
            val data = Data.Builder()
                .putString(MANGA_PATH_WORD, pathWord)
                .putStringArray(MANGA_DOWNLOAD_UUIDS, downloadUUIDs)
                .build()
            scheduleImpl(listOf(data))
        }

        fun observerWorker() = workManager.getWorkInfosByTagLiveData(TAG)
            .asFlow()

        suspend fun cancel(uuid: UUID) {
            workManager.cancelWorkById(uuid).await()
        }

        private suspend fun scheduleImpl(data: Collection<Data>) {
            if (data.isEmpty()) {
                return
            }
            val constraints = createConstraints()
            val requests = data.map { inputData ->
                OneTimeWorkRequestBuilder<DownloadedWorker>()
                    .setConstraints(constraints)
                    .addTag(TAG)
                    .keepResultsForAtLeast(30, TimeUnit.DAYS)
                    .setBackoffCriteria(BackoffPolicy.LINEAR, 10, TimeUnit.SECONDS)
                    .setInputData(inputData)
                    .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                    .build()
            }
            workManager.enqueue(requests).await()
        }

        fun pause(id: UUID) {
            val intent = PausingHandler.getPauseIntent(context, id)
            context.sendBroadcast(intent)
        }

        fun resume(id: UUID) {
            val intent = PausingHandler.getResumeIntent(context, id)
            context.sendBroadcast(intent)
        }

        suspend fun updateConstraints() {
            val constraints = createConstraints()
            val works = workManager.getWorkInfosByTag(TAG).await()
            for (work in works) {
                if (work.state.isFinished) {
                    continue
                }
                val request = OneTimeWorkRequestBuilder<DownloadedWorker>()
                    .setConstraints(constraints)
                    .addTag(TAG)
                    .setId(work.id)
                    .build()
                workManager.updateWork(request).await()
            }
        }

        private fun createConstraints() = Constraints.Builder()
            .setRequiredNetworkType(if (setting.downloadOnlyOnWifi) NetworkType.UNMETERED else NetworkType.CONNECTED)
            .build()

    }

    companion object {
        private const val MANGA_PATH_WORD = "MANGA_PATH_WORD"
        private const val MANGA_DOWNLOAD_UUIDS = "MANGA_DOWNLOAD_UUIDS"

        const val MAX_FAILSAFE_ATTEMPTS = 2
        private const val TAG = "download_worker"
    }

}