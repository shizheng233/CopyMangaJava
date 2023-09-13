package com.shicheeng.copymanga.ui.screen.manga

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.shicheeng.copymanga.R
import com.shicheeng.copymanga.ui.screen.compoents.PlainButton
import com.shicheeng.copymanga.ui.theme.ElevationTokens

@Composable
fun MangaDetailBottomBar(
    modifier: Modifier = Modifier,
    bottomCornerSize: CornerSize = CornerSize(0.dp),
    onDownloadClick: () -> Unit,
    onMarkReadClick: () -> Unit,
    onMarkNoReadClick: () -> Unit,
) {
    Surface(
        shape = MaterialTheme.shapes.large.copy(
            bottomStart = bottomCornerSize,
            bottomEnd = bottomCornerSize
        ),
        tonalElevation = ElevationTokens.Level2,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceAround,
            modifier = Modifier.padding(all = 16.dp)
        ) {
            PlainButton(
                id = R.string.download_manga,
                drawableRes = R.drawable.outline_download_24,
                onButtonClick = onDownloadClick
            )
            PlainButton(
                id = R.string.mark_to_read,
                drawableRes = R.drawable.ic_done_all,
                onButtonClick = onMarkReadClick
            )
            PlainButton(
                id = R.string.mark_to_no_read,
                drawableRes = R.drawable.baseline_remove_done_24,
                onButtonClick = onMarkNoReadClick
            )
        }
    }
}