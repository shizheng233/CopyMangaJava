package com.shicheeng.copymanga.server.download.domin

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.PatternMatcher
import androidx.core.app.PendingIntentCompat
import com.shicheeng.copymanga.util.transformToUUIDMayNullSafety
import java.util.UUID

class PausingHandler(
    private val workerID: UUID,
    private val pausingHandle: PausingHandle,
) : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        val uuid = intent?.getStringExtra(UUID_STRING).transformToUUIDMayNullSafety()
        if (uuid != workerID) return
        when (intent?.action) {
            ACTION_PAUSE -> pausingHandle.pause()
            ACTION_RESUME -> pausingHandle.resume()
        }
    }

    companion object {
        private const val UUID_STRING = "uuid"
        private const val ACTION_PAUSE = "com.shihcheeng.copymanga.download.PAUSE"
        private const val ACTION_RESUME = "com.shihcheeng.copymanga.download.RESUME"
        private const val SCHEME = "workuid"

        fun createIntentFilter(id: UUID) = IntentFilter().apply {
            addAction(ACTION_PAUSE)
            addAction(ACTION_RESUME)
            addDataScheme(SCHEME)
            addDataPath(id.toString(), PatternMatcher.PATTERN_SIMPLE_GLOB)
        }

        fun getPauseIntent(context: Context, id: UUID) = Intent(ACTION_PAUSE)
            .setData(Uri.parse("$SCHEME://$id"))
            .setPackage(context.packageName)
            .putExtra(UUID_STRING, id.toString())

        fun getResumeIntent(context: Context, id: UUID) = Intent(ACTION_RESUME)
            .setData(Uri.parse("$SCHEME://$id"))
            .setPackage(context.packageName)
            .putExtra(UUID_STRING, id.toString())

        fun createPausePendingIntent(context: Context, id: UUID) = PendingIntentCompat.getBroadcast(
            context,
            0,
            getPauseIntent(context, id),
            0,
            false,
        )

        fun createResumePendingIntent(context: Context, id: UUID) =
            PendingIntentCompat.getBroadcast(
                context,
                0,
                getResumeIntent(context, id),
                0,
                false,
            )

    }

}