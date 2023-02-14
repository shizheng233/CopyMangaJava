package com.shicheeng.copymanga.server

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.util.Log
import android.util.SparseArray
import androidx.core.app.NotificationCompat
import androidx.core.util.forEach
import androidx.core.util.isNotEmpty
import com.shicheeng.copymanga.R

class DownloadNotification(
    private val context: Context,
    private val channelId: String,
    private val notificationManager: NotificationManager,
) {

    private val chapterDownloadDoneID = 0x1A3E
    private val downloadGroupID = "DOWNLOAD_GROUP"
    private val downloadGroupIDINT = 0x121A
    private val states = SparseArray<DownloadStateChapter>()
    private val groupNotification = NotificationCompat.Builder(context, channelId)

    init {
        groupNotification.setSilent(true)
        groupNotification.setDefaults(0)
        groupNotification.setGroup(downloadGroupID)
        groupNotification.setOnlyAlertOnce(true)
        groupNotification.setGroupAlertBehavior(NotificationCompat.GROUP_ALERT_CHILDREN)
        groupNotification.setGroupSummary(true)
        groupNotification.setContentTitle(context.getString(R.string.downloading))
    }

    private fun buildNotification(): Notification {
        val style = NotificationCompat.InboxStyle(groupNotification)
        var progress = 0f
        var isAllDone = true
        var isInProgress = false
        states.forEach { _, state ->
            val summary = when (state) {
                is DownloadStateChapter.WAITING -> {
                    isAllDone = false
                    isInProgress = true
                    context.getString(R.string.waiting)
                }
                is DownloadStateChapter.PREPARE -> {
                    isAllDone = false
                    isInProgress = true
                    context.getString(R.string.manga_download_get_ready)
                }
                is DownloadStateChapter.DOWNLOADING -> {
                    isAllDone = false
                    isInProgress = true
                    progress += state.percent
                    "${(state.percent * 100).toInt()}%"
                }
                is DownloadStateChapter.ChapterChange -> {
                    isAllDone = false
                    isInProgress = true
                    state.chapter.mangaName
                }
                is DownloadStateChapter.DONE -> {
                    progress++
                    context.getString(R.string.all_done)
                }
                is DownloadStateChapter.ERROR -> {
                    isAllDone = false
                    context.getString(R.string.error_in_download)
                }
                is DownloadStateChapter.PostBeforeDone -> {
                    progress++
                    isInProgress = true
                    isAllDone = false
                    context.getString(R.string.post_before_done)
                }
                is DownloadStateChapter.CANCEL -> {
                    progress++
                    context.getString(R.string.cancel)
                }
            }
            style.addLine("${state.chapter.mangaName} $summary")
        }

        progress = if (isInProgress) {
            progress / states.size().toFloat()
        } else 1f

        style.setBigContentTitle(
            context.getString(if (isAllDone) R.string.all_done else R.string.downloading)
        )
        groupNotification.setContentText(
            context.getString(
                R.string.items_downloaded,
                states.size().toString()
            )
        )
        groupNotification.setNumber(states.size())
        groupNotification.setSmallIcon(
            if (isInProgress) android.R.drawable.stat_sys_download else android.R.drawable.stat_sys_download_done,
        )
        when (progress) {
            1f -> groupNotification.setProgress(0, 0, false)
            0f -> groupNotification.setProgress(1, 0, true)
            else -> groupNotification.setProgress(100, (progress * 100f).toInt(), false)
        }
        return groupNotification.build()
    }

    fun show() {
        val notification = buildNotification()
        notificationManager.notify(downloadGroupIDINT, notification)
    }

    fun createNewItem(id: Int) = NotificationItem(id)

    fun detach() {
        if (states.isNotEmpty()) {
            val notification = buildNotification()
            notificationManager.notify(chapterDownloadDoneID, notification)
        }
        notificationManager.cancel(downloadGroupIDINT)
    }

    private fun updateGroupNotification() {
        val notification = buildNotification()
        notificationManager.notify(downloadGroupIDINT, notification)
    }

    inner class NotificationItem(private val id: Int) {

        private val notification = NotificationCompat.Builder(context, channelId)

        private val cancelPendingIntent = PendingIntent.getBroadcast(
            context,
            id * 2,
            DownloadService.getCancelIntent(id),
            PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
        private val cancel = NotificationCompat.Action(
            com.google.android.material.R.drawable.material_ic_clear_black_24dp,
            context.getString(android.R.string.cancel),
            cancelPendingIntent
        )

        init {
            notification.setSilent(true)
            notification.setOngoing(true)
            notification.setOnlyAlertOnce(true)
            notification.setContentTitle(context.getString(R.string.download_channel_name))
            notification.setSmallIcon(android.R.drawable.stat_sys_download)
            notification.setGroup(downloadGroupID)
            notification.setGroupAlertBehavior(NotificationCompat.GROUP_ALERT_CHILDREN)
        }

        fun notify(state: DownloadStateChapter) {
            notification.setContentTitle(state.chapter.mangaName)
            notification.clearActions()
            when (state) {
                is DownloadStateChapter.WAITING -> {
                    notification.setOngoing(true)
                    notification.setContentText(context.getString(R.string.waiting))
                }
                is DownloadStateChapter.PREPARE -> {
                    notification.setOngoing(true)
                    notification.setProgress(1, 0, true)
                    notification.setSubText(context.getString(R.string.manga_download_get_ready))
                    notification.addAction(cancel)
                }
                is DownloadStateChapter.DOWNLOADING -> {
                    notification.setOngoing(true)
                    notification.setContentText("${(state.percent * 100).toInt()}%")
                    notification.setProgress(state.max, state.progress, false)
                    notification.addAction(cancel)
                }
                is DownloadStateChapter.ChapterChange -> {
                    notification.setOngoing(true)
                    notification.setSubText(state.chapterInDownload.chapterTitle)
                    notification.addAction(cancel)
                }
                is DownloadStateChapter.DONE -> {
                    notification.setAutoCancel(true)
                    notification.setOngoing(false)
                    notification.setContentText(context.getString(R.string.all_done))
                    notification.setProgress(0, 0, false)
                    notification.setSmallIcon(R.drawable.ic_done_all)
                    notification.clearActions()
                    notification.setShowWhen(true)
                    notification.setWhen(System.currentTimeMillis())
                }
                is DownloadStateChapter.ERROR -> {
                    Log.e("TAG_ERROR", "notify: ${state.error}", )
                    notification.setOngoing(false)
                    notification.setContentText(state.error.message)
                    notification.setProgress(0, 0, false)
                    notification.setSubText(context.getString(R.string.error_in_download))
                    notification.clearActions()
                    notification.setShowWhen(true)
                    notification.setWhen(System.currentTimeMillis())
                }
                is DownloadStateChapter.PostBeforeDone -> {
                    notification.setOngoing(true)
                    notification.setProgress(1, 0, true)
                    notification.setSubText(context.getString(R.string.perpare_to_finish))
                    notification.setContentText(context.getString(R.string.post_before_done))
                    notification.clearActions()
                }
                is DownloadStateChapter.CANCEL -> {
                    notification.setContentText(context.getString(R.string.cancel))
                    notification.setShowWhen(true)
                    notification.clearActions()
                    notification.setOngoing(false)
                    notification.setWhen(System.currentTimeMillis())
                }
            }
            val notification = notification.build()
            notificationManager.notify(id, notification)
            updateGroupNotification()
            states.append(state.chapter.hashCode(), state)
        }

        fun dismiss() {
            states.remove(id)
            updateGroupNotification()
            notificationManager.cancel(id)
        }

    }

}