package com.shicheeng.copymanga.ui.screen.download

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.shicheeng.copymanga.R
import com.shicheeng.copymanga.server.DownloadStateChapter


@Composable
fun DownloadItem(
    downloadStateChapter: DownloadStateChapter,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = downloadStateChapter.chapter.mangaHistoryDataModel.name,
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = when (downloadStateChapter) {
                is DownloadStateChapter.CANCEL -> {
                    stringResource(id = R.string.cancel)
                }

                is DownloadStateChapter.DONE -> {
                    stringResource(id = R.string.all_done)
                }

                is DownloadStateChapter.DOWNLOADING -> {
                    downloadStateChapter.currentLocalChapter.name
                }

                is DownloadStateChapter.ERROR -> {
                    stringResource(id = R.string.error_in_download)
                }

                is DownloadStateChapter.PREPARE -> {
                    stringResource(id = R.string.preparing)
                }

                is DownloadStateChapter.PostBeforeDone -> {
                    stringResource(id = R.string.post_before_done)
                }

                is DownloadStateChapter.WAITING -> {
                    stringResource(id = R.string.waiting)
                }
            },
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.alpha(0.78f)
        )
        when (downloadStateChapter) {
            is DownloadStateChapter.DONE -> {
                Text(
                    text = stringResource(id = R.string.all_done),
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.alpha(0.78f)
                )
            }

            is DownloadStateChapter.DOWNLOADING -> {
                Text(
                    text = "${downloadStateChapter.currentPage} / ${downloadStateChapter.totalPages}",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.alpha(0.78f)
                )
            }

            else -> {

            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        StateProgressIndication(downloadStateChapter = downloadStateChapter)
    }
}

@Composable
fun StateProgressIndication(downloadStateChapter: DownloadStateChapter) {
    when (downloadStateChapter) {
        is DownloadStateChapter.CANCEL -> {
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth(),
                progress = 1f
            )
        }

        is DownloadStateChapter.DONE -> {
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth(),
                progress = 1f
            )
        }

        is DownloadStateChapter.DOWNLOADING -> {
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth(),
                progress = downloadStateChapter.percent
            )
        }

        is DownloadStateChapter.ERROR -> {
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth(),
                progress = 1f,
                trackColor = MaterialTheme.colorScheme.error,
                color = MaterialTheme.colorScheme.errorContainer
            )
        }

        is DownloadStateChapter.PREPARE -> {
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth(),
            )
        }

        is DownloadStateChapter.PostBeforeDone -> {
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth(),
            )
        }

        is DownloadStateChapter.WAITING -> {
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }

}

