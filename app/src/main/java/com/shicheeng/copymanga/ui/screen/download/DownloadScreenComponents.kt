package com.shicheeng.copymanga.ui.screen.download

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.work.WorkInfo
import coil.compose.AsyncImage
import com.shicheeng.copymanga.R
import com.shicheeng.copymanga.data.downloadmodel.DownloadUiDataModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DownloadItem(
    downloadUiDataModel: DownloadUiDataModel,
    onCancel: () -> Unit,
    onCardClick: () -> Unit,
    onClick: () -> Unit,
) {
    val animatedProgressState = ProgressIndicatorDefaults.ProgressAnimationSpec
    val progressAnimated by remember { mutableFloatStateOf(0f) }
    val progressAnimatedAsState by animateFloatAsState(
        targetValue = progressAnimated,
        animationSpec = animatedProgressState,
        label = "progress_animated"
    )

    OutlinedCard(
        onClick = { onCardClick() },
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            AsyncImage(
                model = downloadUiDataModel.localSavableMangaModel.mangaHistoryDataModel.url,
                contentDescription = null,
                modifier = Modifier
                    .height(120.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .aspectRatio(2f / 3f),
                contentScale = ContentScale.Crop
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp, end = 12.dp)
            ) {
                Text(
                    text = downloadUiDataModel.localSavableMangaModel.mangaHistoryDataModel.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "%.2f%%".format(downloadUiDataModel.percent * 100),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (downloadUiDataModel.workerState == WorkInfo.State.RUNNING) {
                    Text(
                        text = downloadUiDataModel.getEtaString()?.toString()
                            ?: stringResource(R.string.downloading),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    StateProgressIndication(
                        isIndeterminate = downloadUiDataModel.isIndeterminate,
                        progress = progressAnimatedAsState,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
            when (downloadUiDataModel.workerState) {
                WorkInfo.State.RUNNING -> {
                    StateButton(
                        isPause = downloadUiDataModel.isPause,
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .align(Alignment.CenterVertically),
                        onClick = onClick
                    )
                }

                WorkInfo.State.ENQUEUED -> {
                    StateButton(
                        id = R.drawable.baseline_close_24,
                        contentDescription = stringResource(R.string.cancel),
                        onClick = onCancel,
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .align(Alignment.CenterVertically)
                    )
                }

                else -> {}
            }
        }
    }
}

@Composable
private fun StateProgressIndication(
    modifier: Modifier = Modifier,
    isIndeterminate: Boolean,
    progress: Float,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.CenterStart
    ) {
        if (!isIndeterminate) {
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(CircleShape),
                progress = progress
            )
        } else {
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(CircleShape),
            )
        }
    }
}

