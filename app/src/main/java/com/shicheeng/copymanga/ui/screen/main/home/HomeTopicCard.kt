package com.shicheeng.copymanga.ui.screen.main.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.shicheeng.copymanga.R
import com.shicheeng.copymanga.data.MainTopicDataModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomePageTopicRow(
    list: List<MainTopicDataModel>,
    onTopicBarClick: () -> Unit,
    onItemClick: (MainTopicDataModel) -> Unit,
) {
    val lazyState = rememberLazyListState()
    Column {
        HomeRowHeaderLine(title = stringResource(R.string.topic), click = onTopicBarClick)
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(all = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            state = lazyState
        ) {
            items(list) {
                HomePageTopicCard(
                    title = it.name,
                    supportedText = it.brief,
                    subText = it.period,
                    imageUrl = it.coverUrl,
                    modifier = Modifier.width(320.dp),
                    maxSupportedTextLine = 3,
                    isTitleMaxTwo = true
                ) {
                    onItemClick(it)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomePageTopicCard(
    modifier: Modifier = Modifier,
    title: String,
    supportedText: String,
    subText: String,
    imageUrl: String?,
    maxSupportedTextLine: Int = Int.MAX_VALUE,
    isTitleMaxTwo: Boolean = false,
    onClickAction: () -> Unit,
) {
    OutlinedCard(
        modifier = modifier,
        onClick = onClickAction
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = if (isTitleMaxTwo) 2 else Int.MAX_VALUE,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = subText,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(top = 16.dp)
            )
            Text(
                text = supportedText,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp),
                maxLines = maxSupportedTextLine,
                overflow = TextOverflow.Ellipsis
            )
            AsyncImage(
                model = imageUrl,
                contentDescription = null,
                placeholder = ColorPainter(color = MaterialTheme.colorScheme.secondary),
                modifier = Modifier
                    .wrapContentHeight()
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 16.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .aspectRatio(5f / 2f),
                contentScale = ContentScale.Crop
            )
        }
    }
}
