package com.shicheeng.copymanga.server

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Binder
import android.os.IBinder
import androidx.annotation.MainThread
import androidx.core.content.ContextCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.shicheeng.copymanga.R
import com.shicheeng.copymanga.data.local.LocalSavableMangaModel
import com.shicheeng.copymanga.resposity.MangaHistoryRepository
import com.shicheeng.copymanga.resposity.MangaInfoRepository
import com.shicheeng.copymanga.util.DownloadJob
import com.shicheeng.copymanga.util.FileUtil
import com.shicheeng.copymanga.util.KeyWordSwap.EXTRA_CANCEL_ID
import com.shicheeng.copymanga.util.KeyWordSwap.RECEIVER_CANCEL
import com.shicheeng.copymanga.util.throttle
import com.shicheeng.copymanga.util.whileActive
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.internal.filterList
import javax.inject.Inject

private const val DOWNLOAD_CHANNEL_ID = "DOWNLOAD_CHANNEL"

@AndroidEntryPoint
class DownloadService : LifecycleService() {


    @Inject
    lateinit var repository: MangaInfoRepository

    @Inject
    lateinit var historyRepository: MangaHistoryRepository

    private lateinit var notification: DownloadNotification

    @Inject
    lateinit var fileUtil: FileUtil

    private val jobs = LinkedHashMap<Int, DownloadJob<DownloadStateChapter>>()
    private val jobCounter = MutableStateFlow(0)
    private lateinit var notificationManager: NotificationManager
    private val controlReceiver = ControlReceiver()

    override fun onCreate() {
        super.onCreate()
        notificationManager = this.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        bindNotification()
        notification = DownloadNotification(
            this,
            DOWNLOAD_CHANNEL_ID,
            notificationManager
        )
        val intentFilter = IntentFilter().apply {
            addAction(RECEIVER_CANCEL)
        }
        notification.show()
        ContextCompat.registerReceiver(
            this,
            controlReceiver,
            intentFilter,
            ContextCompat.RECEIVER_NOT_EXPORTED
        )
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        val mangaPathWord = intent?.getStringExtra(PATH_WORD_INTENT)
        val mangaChapterUUId = intent?.getStringArrayExtra(UUID_INTENT)
        val mangaDownload by lazy {
            runBlocking {
                historyRepository.getMangaByPathWord(requireNotNull(mangaPathWord))
            }
        }
        val filterList = mangaDownload?.list?.filterList {
            mangaChapterUUId?.contains(uuid) == true
        } ?: emptyList()
        val newDownload = mangaDownload?.copy(list = filterList)

        return if (newDownload != null) {
            val job = downloadManga(newDownload, startId)
            jobs[startId] = job
            jobCounter.value = jobs.size
            START_REDELIVER_INTENT
        } else {
            dismissNotification()
            START_NOT_STICKY
        }
    }

    override fun onBind(intent: Intent): IBinder {
        super.onBind(intent)
        return DownloadBinder(service = this)
    }

    private fun downloadManga(
        lastMangaDownload: LocalSavableMangaModel,
        startId: Int,
    ): DownloadJob<DownloadStateChapter> {
        val job = fileUtil.downloadChapter(lastMangaDownload, startId)
        collectJob(job)
        return job
    }

    private fun collectJob(job: DownloadJob<DownloadStateChapter>) {
        lifecycleScope.launch {
            val id = job.progressValue.chapterID
            val notificationItem = notification.createNewItem(id)
            try {
                job.progressAsFlow()
                    .throttle { state -> if (state is DownloadStateChapter.DOWNLOADING) 400L else 0L }
                    .whileActive()
                    .collect { state ->
                        notificationItem.notify(state)
                    }
                job.join()
            } finally {
                if (job.isCancelled) {
                    notificationItem.dismiss()
                    if (jobs.remove(id) != null) {
                        jobCounter.value = jobs.size
                    }
                } else {
                    notificationItem.notify(job.progressValue)
                }
            }
        }.invokeOnCompletion {
            dismissNotification()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(controlReceiver)
    }

    private fun bindNotification() {
        val name = getString(R.string.download_channel_name)
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val mChannel = NotificationChannel(DOWNLOAD_CHANNEL_ID, name, importance)
        notificationManager.createNotificationChannel(mChannel)
    }

    @MainThread
    private fun dismissNotification() {
        if (jobs.any { (_, job) -> job.isActive }) {
            return
        }
        notification.detach()
        stopSelf()
    }

    inner class ControlReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                RECEIVER_CANCEL -> {
                    val id = intent.getIntExtra(EXTRA_CANCEL_ID, 0)
                    jobs[id]?.cancel()
                }
            }
        }


    }

    inner class DownloadBinder(service: DownloadService) : Binder(), DefaultLifecycleObserver {

        private var downloadsStateFlow =
            MutableStateFlow<List<DownloadJob<DownloadStateChapter>>>(emptyList())

        init {
            service.lifecycle.addObserver(this)
            service.jobCounter.onEach {
                downloadsStateFlow.value = service.jobs.values.toList()
            }.launchIn(service.lifecycleScope)
        }

        override fun onDestroy(owner: LifecycleOwner) {
            owner.lifecycle.removeObserver(this)
            downloadsStateFlow.value = emptyList()
            super.onDestroy(owner)
        }

        val downloads get() = downloadsStateFlow.asStateFlow()

    }

    companion object {

        fun getCancelIntent(id: Int) = Intent(RECEIVER_CANCEL)
            .putExtra(EXTRA_CANCEL_ID, id)

        private const val UUID_INTENT = "UUID_INTENT"
        private const val PATH_WORD_INTENT = "PATH_WORD_INTENT"
        fun startDownloadService(context: Context, pathWord: String, uuids: Array<String>) {
            val intent = Intent(context, DownloadService::class.java)
            intent.putExtra(PATH_WORD_INTENT, pathWord)
            intent.putExtra(UUID_INTENT, uuids)
            context.startService(intent)
        }

    }

}