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
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.shicheeng.copymanga.R
import com.shicheeng.copymanga.data.LastMangaDownload
import com.shicheeng.copymanga.util.*
import com.shicheeng.copymanga.util.KeyWordSwap.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

private const val DOWNLOAD_CHANNEL_ID = "DOWNLOAD_CHANNEL"

class DownloadService :
    LifecycleService() {

    private lateinit var notification: DownloadNotification
    private lateinit var fileUtil: FileUtil
    private val jobs = LinkedHashMap<Int, DownloadJob<DownloadStateChapter>>()
    private val jobCounter = MutableStateFlow(0)
    private lateinit var notificationManager: NotificationManager

    private val controlReceiver = ControlReceiver()

    override fun onCreate() {

        notificationManager = this.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        bindNotification()
        fileUtil = FileUtil(this, lifecycleScope)
        notification = DownloadNotification(
            this,
            DOWNLOAD_CHANNEL_ID,
            notificationManager
        )
        val intentFilter = IntentFilter().apply {
            addAction(RECEIVER_CANCEL)
        }
        notification.show()
        registerReceiver(controlReceiver, intentFilter)
        super.onCreate()
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        val mangaDownload = intent?.getParcelableExtraCompat<LastMangaDownload>(CHAPTER_TYPE)

        return if (mangaDownload != null) {
            val job = downloadManga(mangaDownload, startId)
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
        lastMangaDownload: LastMangaDownload,
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
    }

}