package com.shicheeng.copymanga.ui.screen.history.local

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.shicheeng.copymanga.R
import com.shicheeng.copymanga.data.MangaHistoryDataModel
import com.shicheeng.copymanga.data.webhistory.WebHistoryItem
import com.shicheeng.copymanga.ui.screen.compoents.CommonCover
import com.shicheeng.copymanga.ui.screen.compoents.PlainButton
import com.shicheeng.copymanga.util.convertToOnlyTime

@Composable
fun HistoryItem(
    modifier: Modifier = Modifier,
    data: MangaHistoryDataModel,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit,
) {
    Row(
        modifier = modifier
            .clickable(onClick = onClick)
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Card(
            modifier = Modifier.width(50.dp),
            shape = RoundedCornerShape(4.dp)
        ) {
            CommonCover(
                url = data.url,
                contentDescription = data.name,
                shape = RoundedCornerShape(size = 4.dp)
            )
        }
        Column(
            modifier = Modifier
                .padding(start = 8.dp)
                .weight(1f)
        ) {
            Text(
                text = data.name,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = "${data.time.convertToOnlyTime()} • ${
                    stringResource(
                        id = R.string.info_read_in,
                        formatArgs = arrayOf(data.positionChapter + 1, data.positionPage + 1)
                    )
                }",
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(top = 2.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        PlainButton(
            id = R.string.delete,
            drawableRes = R.drawable.baseline_delete_outline_24,
            onButtonClick = onDeleteClick
        )
    }
}

@Composable
fun HistoryItemCloud(
    modifier: Modifier = Modifier,
    data: WebHistoryItem,
    onClick: () -> Unit,
) {
    Row(
        modifier = modifier
            .clickable(onClick = onClick)
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Card(
            modifier = Modifier.width(50.dp),
            shape = RoundedCornerShape(4.dp)
        ) {
            CommonCover(
                url = data.comic.cover,
                contentDescription = data.comic.name,
                shape = RoundedCornerShape(size = 4.dp)
            )
        }
        Column(
            modifier = Modifier
                .padding(start = 8.dp)
                .weight(1f)
        ) {
            Text(
                text = data.comic.name,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = data.lastChapterName,
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(top = 2.dp, end = 8.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        PlainButton(
            id = R.string.shelf_cloud,
            drawableRes = R.drawable.outline_cloud_24
        ) {}
    }
}
