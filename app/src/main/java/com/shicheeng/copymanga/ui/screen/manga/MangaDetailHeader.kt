package com.shicheeng.copymanga.ui.screen.manga

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.shicheeng.copymanga.R
import com.shicheeng.copymanga.data.MangaHistoryDataModel
import com.shicheeng.copymanga.ui.screen.compoents.MangaCover
import com.shicheeng.copymanga.util.click


@Composable
fun DetailInfoBox(
    modifier: Modifier = Modifier,
    mangaInfoDataModel: MangaHistoryDataModel,
    topPadding: Dp,
    onAuthorClicked: () -> Unit,
) {
    Box(modifier = modifier) {
        val backDropColors = listOf(
            Color.Transparent,
            MaterialTheme.colorScheme.background
        )
        AsyncImage(
            model = mangaInfoDataModel.url,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .matchParentSize()
                .drawWithContent {
                    drawContent()
                    drawRect(
                        brush = Brush.verticalGradient(backDropColors)
                    )
                }
                .blur(4.dp)
                .alpha(.2f)
        )
        DetailHeader(
            mangaInfoDataModel = mangaInfoDataModel,
            onAuthorClicked = onAuthorClicked,
            topPadding = topPadding
        )
    }
}

@Composable
fun DetailHeader(
    mangaInfoDataModel: MangaHistoryDataModel,
    topPadding: Dp,
    onAuthorClicked: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp + topPadding, start = 16.dp, end = 16.dp)
    ) {
        MangaCover.Small(
            url = mangaInfoDataModel.url,
            shape = MaterialTheme.shapes.medium
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 16.dp)
        ) {
            Text(
                text = mangaInfoDataModel.name,
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = mangaInfoDataModel.alias
                    ?: stringResource(id = R.string.no_alias),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = mangaInfoDataModel.authorList.joinToString { it.name },
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .padding(top = 4.dp)
                    .click { onAuthorClicked() }
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(
                        id = when (mangaInfoDataModel.mangaStatusId) {
                            0 -> {
                                R.drawable.ic_baseline_loop
                            }

                            1 -> {
                                R.drawable.ic_done_all
                            }

                            else -> {
                                R.drawable.outline_do_not_disturb_24
                            }
                        }
                    ),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(end = 4.dp)
                        .size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                ProvideTextStyle(value = MaterialTheme.typography.bodyMedium) {
                    Text(
                        text = mangaInfoDataModel.mangaStatus,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    DotText()
                    Text(
                        text = mangaInfoDataModel.mangaRegion,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_baseline_hot),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(end = 4.dp)
                        .size(16.dp)
                )
                Text(
                    text = mangaInfoDataModel.mangaPopularNumber,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Text(
                text = stringResource(
                    R.string.last_update,
                    mangaInfoDataModel.mangaLastUpdate
                ),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(top = 4.dp),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}


@Composable
fun DetailRowInfo(
    isCollect: Boolean,
    isSubscribed: Boolean,
    onCollectClicked: () -> Unit,
    onSubscribedClick: () -> Unit,
    onCommentClick: () -> Unit,
) {
    /*从Tachiyomi直接复制来的，这个比较简单就不自己想办法写了*/
    val defaultActionButtonColor = MaterialTheme.colorScheme.onSurface.copy(alpha = .38f)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        MangaActionButton(
            title = stringResource(if (isCollect) R.string.remove_add_to_lib else R.string.add_to_lib),
            icon = ImageVector.vectorResource(
                id = if (isCollect) {
                    R.drawable.baseline_library_add_check_24
                } else {
                    R.drawable.baseline_library_add_24
                }
            ),
            color = if (isCollect) MaterialTheme.colorScheme.primary else defaultActionButtonColor,
            onClick = onCollectClicked
        )
        MangaActionButton(
            title = stringResource(
                id = if (isSubscribed) {
                    R.string.unsubscribe_for_updates
                } else {
                    R.string.subscribe_for_updates
                }
            ),
            icon = ImageVector.vectorResource(
                id = if (isSubscribed) {
                    R.drawable.iconmonstr_rss_feed_baseline
                } else {
                    R.drawable.iconmonstr_rss_feed_outline
                }
            ),
            color = if (isSubscribed) MaterialTheme.colorScheme.primary else defaultActionButtonColor,
            onClick = onSubscribedClick
        )
        MangaActionButton(
            title = stringResource(id = R.string.comment_text),
            icon = ImageVector.vectorResource(R.drawable.outline_comment_24),
            color = MaterialTheme.colorScheme.primary,
            onClick = onCommentClick
        )
    }
}

@Composable
private fun RowScope.MangaActionButton(
    title: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit,
) {
    TextButton(
        onClick = onClick,
        modifier = Modifier.weight(1f),
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(20.dp),
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = title,
                color = color,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun DotText() {
    Text(text = " • ")
}