package com.shicheeng.copymanga.ui.screen.manga

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.shicheeng.copymanga.R
import com.shicheeng.copymanga.data.local.LocalChapter
import com.shicheeng.copymanga.util.UIState

/**
 * 这是章节的列表，当列状态为[UIState.Error]不会显示任何内容。
 */
fun LazyListScope.chapterList(
    inSelectMode: Boolean,
    selectChapters: List<LocalChapter>,
    chapterState: UIState<List<LocalChapter>>,
    webLookedUUID: String?,
    onLongClick: (LocalChapter) -> Unit,
    onClick: (LocalChapter) -> Unit,
) {
    if (chapterState is UIState.Error<*> || chapterState == UIState.Loading) {
        return
    }
    val successState = chapterState as UIState.Success
    items(
        items = successState.content,
        key = { it.hashCode() },
        contentType = { MangaDetailKey.LIST_CHAPTER }
    ) { eachChapter ->
        ChapterItem(
            inSelectMode = inSelectMode,
            isSelected = selectChapters.contains(eachChapter),
            title = eachChapter.name,
            time = eachChapter.datetime_created,
            isDownload = eachChapter.isDownloaded,
            onLongClick = {
                onLongClick(eachChapter)
            },
            readIn = if (
                eachChapter.isReadProgress
                && !eachChapter.isReadFinish
                && eachChapter.readIndex != (eachChapter.size - 1)
            ) {
                eachChapter.readIndex
            } else null,
            isRead = eachChapter.isReadFinish || eachChapter.readIndex == (eachChapter.size - 1),
            isWebLooked = eachChapter.uuid == webLookedUUID
        ) {
            onClick(eachChapter)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ChapterItem(
    inSelectMode: Boolean,
    isSelected: Boolean,
    isRead: Boolean,
    title: String,
    time: String,
    readIn: Int? = null,
    isDownload: Boolean = false,
    isWebLooked: Boolean = false,
    onLongClick: () -> Unit,
    onClick: () -> Unit,
) {
    val textAlpha = remember(isRead) { if (isRead) .38f else 1f }
    val textSubtitleAlpha = remember(isRead) { if (isRead) .38f else 0.78f }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(
                if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.26f)
                else Color.Transparent
            )
            .fillMaxWidth()
            .combinedClickable(
                onClick = {
                    if (inSelectMode) {
                        onLongClick()
                    } else {
                        onClick.invoke()
                    }
                },
                onLongClick = {
                    onLongClick.invoke()
                }
            )
            .padding(start = 16.dp, top = 12.dp, end = 16.dp, bottom = 12.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.alpha(textAlpha)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = time,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                        modifier = Modifier.alpha(textSubtitleAlpha)
                    )
                    when {
                        readIn != null -> {
                            Text(
                                text = stringResource(
                                    id = R.string.read_in,
                                    formatArgs = arrayOf(readIn + 1)
                                ),
                                overflow = TextOverflow.Ellipsis,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier
                                    .alpha(textSubtitleAlpha)
                                    .padding(start = 4.dp)
                            )
                        }

                        isRead -> {
                            Text(
                                text = stringResource(R.string.read_finished),
                                overflow = TextOverflow.Ellipsis,
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier
                                    .alpha(textSubtitleAlpha)
                                    .padding(start = 4.dp)
                            )
                        }
                    }
                }
            }
            if (isWebLooked) {
                Icon(
                    painter = painterResource(id = R.drawable.outline_cloud_24),
                    contentDescription = stringResource(id = R.string.shelf_cloud)
                )
            }
            if (isDownload) {
                Icon(
                    painter = painterResource(id = R.drawable.outline_download_for_offline_24),
                    contentDescription = stringResource(id = R.string.download_manga)
                )
            }
        }
    }
}

@Composable
fun TipDialog(
    onDismiss: () -> Unit,
    onPositive: () -> Unit,
    onNegative: () -> Unit,
) = AlertDialog(
    onDismissRequest = onDismiss,
    confirmButton = {
        Button(
            onClick = {
                onPositive()
                onDismiss()
            }
        ) {
            Text(text = stringResource(R.string.enable))
        }
        onDismiss()
    },
    properties = DialogProperties(),
    dismissButton = {
        Button(
            onClick = {
                onNegative()
                onDismiss()
            }
        ) {
            Text(text = stringResource(R.string.not_enabled))
        }
    },
    title = {
        Text(text = stringResource(R.string.confrim_update))
    },
    text = {
        Text(text = stringResource(R.string.enable_update_text))
    }
)