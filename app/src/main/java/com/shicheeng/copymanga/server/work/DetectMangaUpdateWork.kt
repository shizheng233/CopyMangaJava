package com.shicheeng.copymanga.server.work

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.shicheeng.copymanga.R
import com.shicheeng.copymanga.data.MangaHistoryDataModel
import com.shicheeng.copymanga.data.local.LocalChapter
import com.shicheeng.copymanga.resposity.MangaHistoryRepository
import com.shicheeng.copymanga.resposity.MangaInfoRepository
import com.shicheeng.copymanga.ui.screen.setting.IN_BATTERY_NOT_LOW
import com.shicheeng.copymanga.ui.screen.setting.IN_CHARGING
import com.shicheeng.copymanga.ui.screen.setting.IN_WIFI
import com.shicheeng.copymanga.ui.screen.setting.SettingPref
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.concurrent.TimeUnit

@HiltWorker
class DetectMangaUpdateWork @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted params: WorkerParameters,
    infoRepository: MangaInfoRepository,
    historyRepository: MangaHistoryRepository,
) : CoroutineWorker(appContext, params), IDetectManga.OnMangaDetectUpdate {

    private val iDetectManga = IDetectManga(historyRepository, infoRepository, this)
    private val notificationManager = appContext.getSystemService(NotificationManager::class.java)
    private val notification =
        NotificationCompat.Builder(appContext, DETECT_UPDATE_CHANELLE).apply {
            setContentTitle(appContext.getString(R.string.update_manga))
            setSmallIcon(R.drawable.ic_stat_name)
            setContentText(appContext.getString(R.string.preparing))
            setProgress(0, 0, true)
            setOngoing(true)
            setDefaults(0)
            setGroup(GROUP_ITEM_CHAPTERS)
            setGroupAlertBehavior(NotificationCompat.GROUP_ALERT_SUMMARY)
            setGroupSummary(true)
            setOnlyAlertOnce(true)
            priority = NotificationCompat.PRIORITY_HIGH
        }

    override suspend fun doWork(): Result {
        notificationManager.notify(
            DETECT_UPDATE_NOTIFICATION_ID,
            notification.build()
        )
        iDetectManga.fetchMangaUpdate()
        return Result.success()
    }

    override fun onReady() {
        notificationManager.notify(
            DETECT_UPDATE_NOTIFICATION_ID,
            notification.build()
        )
    }

    override fun onSubscribe(index: Int, size: Int, historyDataModel: MangaHistoryDataModel) {
        notification.setProgress(size, index + 1, false)
        notification.setContentText(historyDataModel.name)
        notificationManager.notify(
            DETECT_UPDATE_NOTIFICATION_ID,
            notification.build()
        )
    }

    override fun onError(
        eIndex: Int,
        historyDataModel: MangaHistoryDataModel,
        exception: Throwable,
    ) {
//        val notificationItem = NotificationCompat
//            .Builder(appContext, DETECT_UPDATE_CHANELLE)
//            .apply {
//                setContentTitle(historyDataModel.name)
//                setContentText(exception.message.toString())
//                setOngoing(false)
//                setSmallIcon(R.drawable.ic_outline_page)
//            }.build()
//        notificationManager.notify(historyDataModel.hashCode(), notificationItem)
        exception.printStackTrace()
    }

    override fun onSingleSuccess(
        index: Int,
        historyDataModel: MangaHistoryDataModel,
        newChapter: List<LocalChapter>,
    ) {
        if (newChapter.isNotEmpty()) {
            val notificationItem = NotificationCompat
                .Builder(appContext, DETECT_UPDATE_CHANELLE)
                .apply {
                    setContentTitle(historyDataModel.name)
                    setContentText(newChapter.joinToString { it.name })
                    setSmallIcon(R.drawable.ic_outline_page)
                    setOngoing(false)
                    setGroup(GROUP_ITEM_CHAPTERS)
                    setGroupAlertBehavior(NotificationCompat.GROUP_ALERT_SUMMARY)
                }.build()
            notificationManager.notify(historyDataModel.hashCode(), notificationItem)
        }
    }

    override fun onSuccess() {
        notification.setProgress(0, 0, false)
        notification.setContentText(appContext.getString(R.string.completed))
        notification.setOngoing(false)
        notification.setAutoCancel(true)
        notificationManager.notify(
            DETECT_UPDATE_NOTIFICATION_ID,
            notification.build()
        )
    }


    companion object {
        const val DETECT_UPDATE_CHANELLE = "DETECT_UPDATE_CHANELLE"
        private const val DETECT_UPDATE_NOTIFICATION_ID = 0x1a2f3c
        private const val GROUP_ITEM_CHAPTERS = "GROUP_ITEM_CHAPTERS"
        private const val Tag = "Manga Update Task"

        /**
         * 启动这个Worker
         */
        fun readyToStart(
            isEnable: Boolean,
            context: Context,
            settingPref: SettingPref,
            takeInterval: Int? = null,
        ) {
            if (isEnable) {
                val interval = takeInterval ?: settingPref.timeInterval.value
                if (interval > 0) {
                    val constraintsSetting = settingPref.updateConstant.value
                    val constraints = Constraints.Builder()
                        .setRequiresCharging(IN_CHARGING in constraintsSetting)
                        .setRequiredNetworkType(
                            if (IN_WIFI in constraintsSetting) NetworkType.UNMETERED else NetworkType.CONNECTED
                        )
                        .setRequiresBatteryNotLow(IN_BATTERY_NOT_LOW in constraintsSetting)
                        .build()
                    val work = PeriodicWorkRequestBuilder<DetectMangaUpdateWork>(
                        repeatInterval = interval.toLong(),
                        repeatIntervalTimeUnit = TimeUnit.HOURS,
                        flexTimeInterval = 10,
                        flexTimeIntervalUnit = TimeUnit.MINUTES
                    )
                        .addTag(Tag)
                        .setConstraints(constraints)
                        .build()
                    WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                        Tag,
                        ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
                        work
                    )
                }
            } else {
                WorkManager.getInstance(context).cancelAllWorkByTag(Tag)
            }
        }

    }

}
