package com.shicheeng.copymanga.server.download.woker

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.TaskStackBuilder
import androidx.core.graphics.drawable.toBitmap
import androidx.core.net.toUri
import androidx.work.WorkManager
import coil.ImageLoader
import coil.request.ErrorResult
import coil.request.ImageRequest
import coil.request.SuccessResult
import coil.size.Scale
import com.shicheeng.copymanga.MainActivity
import com.shicheeng.copymanga.R
import com.shicheeng.copymanga.data.local.LocalSavableMangaModel
import com.shicheeng.copymanga.server.download.domin.DownloadState
import com.shicheeng.copymanga.server.download.domin.PausingHandler
import com.shicheeng.copymanga.ui.screen.Router
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.UUID

private const val DOWNLOAD_CHANNEL_ID = "DOWNLOAD_CHANNEL"

class DownloadNotificationFactory @AssistedInject constructor(
    @ApplicationContext private val context: Context,
    private val workerManager: WorkManager,
    private val coil: ImageLoader,
    @Assisted uuid: UUID,
) {

    private val downloadGroupID = "DOWNLOAD_GROUP"
    private val builder = NotificationCompat.Builder(context, DOWNLOAD_CHANNEL_ID)
    private val covers = HashMap<LocalSavableMangaModel, Drawable>()
    private val mutex = Mutex()
    private val coverWidth = context.resources.getDimensionPixelSize(
        androidx.core.R.dimen.compat_notification_large_icon_max_width,
    )
    private val coverHeight = context.resources.getDimensionPixelSize(
        androidx.core.R.dimen.compat_notification_large_icon_max_height,
    )

    private val downloadPending = TaskStackBuilder.create(context).run {
        addNextIntentWithParentStack(
            Intent(
                Intent.ACTION_VIEW,
                Router.DOWNLOAD.deepLink.toUri(),
                context,
                MainActivity::class.java
            )
        )
        getPendingIntent(0, PendingIntent.FLAG_MUTABLE)
    }
    private val actionCancel by lazy {
        NotificationCompat.Action(
            com.google.android.material.R.drawable.material_ic_clear_black_24dp,
            context.getString(android.R.string.cancel),
            workerManager.createCancelPendingIntent(uuid),
        )
    }
    private val actionPause by lazy {
        NotificationCompat.Action(
            R.drawable.baseline_pause_24,
            context.getString(R.string.pause),
            PausingHandler.createPausePendingIntent(context, uuid),
        )
    }

    private val actionResume by lazy {
        NotificationCompat.Action(
            R.drawable.baseline_play_arrow_24,
            context.getString(R.string.resume),
            PausingHandler.createResumePendingIntent(context, uuid),
        )
    }


    init {
        bindNotification()
        builder.setSilent(true)
        builder.setDefaults(0)
        builder.setGroup(downloadGroupID)
        builder.setOnlyAlertOnce(true)
        builder.setGroupAlertBehavior(NotificationCompat.GROUP_ALERT_CHILDREN)
        builder.foregroundServiceBehavior = NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE
        builder.setGroupSummary(true)
        builder.setContentTitle(context.getString(R.string.downloading))
    }

    suspend fun buildNotification(state: DownloadState?): Notification = mutex.withLock {
        if (state == null) {
            builder.setContentText(context.getString(R.string.preparing))
            builder.setContentTitle(context.getString(R.string.downloading))
        } else {
            builder.setContentTitle(state.localSavableMangaModel.mangaHistoryDataModel.name)
            builder.setContentText(context.getString(R.string.downloading))
        }
        builder.setProgress(1, 0, true)
        builder.setSmallIcon(android.R.drawable.stat_sys_download)
        builder.setContentIntent(downloadPending)
        builder.setStyle(null)
        builder.setLargeIcon(if (state != null) getCover(state.localSavableMangaModel)?.toBitmap() else null)
        builder.clearActions()
        builder.setSubText(null)
        builder.setShowWhen(false)
        builder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
        when {
            state == null -> Unit
            state.localManga != null -> {
                builder.setProgress(0, 0, false)
                builder.setContentText(context.getString(R.string.completed))
                builder.setContentIntent(null)
                builder.setAutoCancel(true)
                builder.setSmallIcon(android.R.drawable.stat_sys_download_done)
                builder.setCategory(null)
                builder.setStyle(null)
                builder.setOngoing(false)
                builder.setShowWhen(true)
                builder.setWhen(System.currentTimeMillis())
            }

            state.isStopped -> {
                builder.setProgress(0, 0, false)
                builder.setContentText(context.getString(R.string.waiting))
                builder.setCategory(NotificationCompat.CATEGORY_PROGRESS)
                builder.setStyle(null)
                builder.setOngoing(true)
                builder.setSmallIcon(R.drawable.ic_stat_name)
                builder.addAction(actionCancel)
            }

            state.isPaused -> {
                builder.setProgress(state.max, state.progress, false)
                val percent = if (state.percent >= 0) {
                    reformatPercentString(percent = state.percent)
                } else {
                    null
                }
                if (state.error != null) {
                    builder.setContentText("$percent • ${state.error}")
                } else {
                    builder.setContentText(percent)
                }
                builder.setCategory(NotificationCompat.CATEGORY_PROGRESS)
                builder.setStyle(null)
                builder.setOngoing(true)
                builder.setSmallIcon(R.drawable.baseline_pause_24)
                builder.addAction(actionCancel)
                builder.addAction(actionResume)
            }

            state.error != null -> { // error, final state
                builder.setProgress(0, 0, false)
                builder.setSmallIcon(android.R.drawable.stat_notify_error)
                builder.setSubText(context.getString(R.string.error))
                builder.setContentText(state.error)
                builder.setAutoCancel(true)
                builder.setOngoing(false)
                builder.setCategory(NotificationCompat.CATEGORY_ERROR)
                builder.setShowWhen(true)
                builder.setWhen(System.currentTimeMillis())
                builder.setStyle(NotificationCompat.BigTextStyle().bigText(state.error))
            }

            else -> {
                builder.setProgress(state.max, state.progress, false)
                builder.setContentText(reformatPercentString(state.percent))
                builder.setCategory(NotificationCompat.CATEGORY_PROGRESS)
                builder.setStyle(null)
                builder.setOngoing(true)
                builder.addAction(actionCancel)
                builder.addAction(actionPause)
            }
        }
        return builder.build()
    }

    private fun reformatPercentString(percent: Float): String {
        return "%.2f%%".format((percent * 100))
    }

    private suspend fun getCover(localSavableMangaModel: LocalSavableMangaModel) =
        covers[localSavableMangaModel] ?: run {
            runCatching {
                coil.execute(
                    ImageRequest.Builder(context)
                        .data(localSavableMangaModel.mangaHistoryDataModel.url)
                        .allowHardware(false)
                        .tag(localSavableMangaModel.mangaHistoryDataModel.comicUUID)
                        .size(coverWidth, coverHeight)
                        .scale(Scale.FILL)
                        .build()
                ).let {
                    when (it) {
                        is ErrorResult -> throw it.throwable
                        is SuccessResult -> it.drawable
                    }
                }
            }.onSuccess {
                covers[localSavableMangaModel] = it
            }.onFailure {
                it.printStackTrace()
            }.getOrNull()
        }

    private fun bindNotification() {
        val notificationManager = NotificationManagerCompat.from(context)
        val name = context.getString(R.string.download_channel_name)
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val mChannel = NotificationChannelCompat.Builder(DOWNLOAD_CHANNEL_ID, importance)
            .setName(name)
            .setVibrationEnabled(false)
            .setLightsEnabled(false)
            .setSound(null, null)
            .build()
        notificationManager.createNotificationChannel(mChannel)
    }

    @AssistedFactory
    interface Injket {
        fun create(uuid: UUID): DownloadNotificationFactory
    }

}