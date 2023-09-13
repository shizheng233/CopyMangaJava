package com.shicheeng.copymanga.ui.screen.manga

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.shicheeng.copymanga.R
import com.shicheeng.copymanga.data.MangaHistoryDataModel
import com.shicheeng.copymanga.data.local.LocalChapter
import com.shicheeng.copymanga.ui.screen.compoents.MangaCover
import com.shicheeng.copymanga.util.UIState
import com.shicheeng.copymanga.util.click

@Composable
fun DetailHeader(
    mangaInfoDataModel: MangaHistoryDataModel,
    onAuthorClicked: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp, start = 16.dp, end = 16.dp)
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
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = mangaInfoDataModel.alias
                    ?: stringResource(id = R.string.no_alias),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = mangaInfoDataModel.authorList.joinToString { it.name },
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .padding(top = 4.dp)
                    .click { onAuthorClicked() }
            )
            Text(
                text = stringResource(
                    R.string.last_update,
                    mangaInfoDataModel.mangaLastUpdate
                ),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(top = 4.dp),
                style = MaterialTheme.typography.titleSmall
            )
        }
    }
}


@Composable
fun DetailRowInfo(
    mangaInfoDataModel: MangaHistoryDataModel,
    chapters: UIState<List<LocalChapter>>,
    onCommentClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        VerticalIcon(
            iconId = {
                when (mangaInfoDataModel.mangaStatusId) {
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
            },
            text = mangaInfoDataModel.mangaStatus
        )
        VerticalDivider(
            modifier = Modifier
                .padding(vertical = 8.dp)
        )
        VerticalIcon(
            iconId = { R.drawable.ic_baseline_region },
            text = mangaInfoDataModel.mangaRegion
        )
        VerticalDivider(
            modifier = Modifier
                .padding(vertical = 8.dp)
        )
        if (chapters is UIState.Success) {
            VerticalIcon(
                iconId = { R.drawable.ic_outline_page },
                text = stringResource(
                    id = R.string.chapter,
                    formatArgs = arrayOf(chapters.content.size.toString())
                )
            )
        }
        VerticalDivider(
            modifier = Modifier
                .padding(vertical = 8.dp)
        )
        VerticalIcon(
            iconId = { R.drawable.ic_baseline_hot },
            text = mangaInfoDataModel.mangaPopularNumber
        )
        VerticalDivider(
            modifier = Modifier
                .padding(vertical = 8.dp)
        )
        VerticalIcon(
            iconId = { R.drawable.baseline_comment_24 },
            text = stringResource(R.string.comment_text),
            click = onCommentClick
        )
    }
}

@Composable
fun RowScope.VerticalIcon(
    modifier: Modifier = Modifier,
    @DrawableRes iconId: () -> Int,
    text: String,
    color: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    textColor: Color = MaterialTheme.colorScheme.onSurface,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(id = iconId()),
            contentDescription = text,
            modifier = Modifier.padding(4.dp),
            tint = color
        )
        BasicText(
            text = text,
            modifier = Modifier.padding(horizontal = 4.dp),
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Normal
            ),
            maxLines = 1,
            color = { textColor }
        )
    }
}

@Composable
fun VerticalIcon(
    modifier: Modifier = Modifier,
    @DrawableRes iconId: () -> Int,
    text: String,
    color: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    fontSize: TextUnit = TextUnit.Unspecified,
    click: () -> Unit,
) {
    Column(
        modifier = modifier.click { click() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(id = iconId()),
            contentDescription = text,
            modifier = Modifier.padding(4.dp),
            tint = color
        )
        Text(
            text = text,
            color = color,
            modifier = Modifier.padding(horizontal = 4.dp),
            fontSize = fontSize,
            style = MaterialTheme.typography.labelMedium
        )
    }
}
