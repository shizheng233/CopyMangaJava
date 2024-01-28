package com.shicheeng.copymanga.data.downloadmodel

import android.text.format.DateUtils
import androidx.work.WorkInfo
import androidx.work.Worker
import com.shicheeng.copymanga.data.local.LocalSavableMangaModel
import java.lang.invoke.MethodHandles.Lookup
import java.util.UUID

data class DownloadUiDataModel(
    val localSavableMangaModel: LocalSavableMangaModel,
    val pathWord: String,
    val progress: Int,
    val max: Int,
    val error: String?,
    val isIndeterminate: Boolean,
    val isPause: Boolean,
    val isStopped: Boolean,
    val workerState: WorkInfo.State,
    val timeStamp: Long,
    val totalChapter: Int,
    val id: UUID,
    val eta: Long,
) : Comparable<DownloadUiDataModel> {

    val percent: Float
        get() = if (max > 0) progress / max.toFloat() else 0f

    val hasEta: Boolean
        get() = workerState == WorkInfo.State.RUNNING && !isPause && eta > 0L

    override fun compareTo(other: DownloadUiDataModel): Int {
        return timeStamp.compareTo(other.timeStamp)
    }

    val canResume: Boolean
        get() = workerState == WorkInfo.State.RUNNING && isPause

    fun getEtaString(): CharSequence? = if (hasEta) {
        DateUtils.getRelativeTimeSpanString(
            eta,
            System.currentTimeMillis(),
            DateUtils.SECOND_IN_MILLIS,
        )
    } else {
        null
    }


}
