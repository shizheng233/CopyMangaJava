package com.shicheeng.copymanga.ui.screen.main.leaderboard

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.shicheeng.copymanga.R
import com.shicheeng.copymanga.data.rank.Item
import com.shicheeng.copymanga.ui.screen.compoents.CommonCover

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaderBoardItem(
    item: Item,
    onItemClick: (Item) -> Unit,
) {
    Card(
        modifier = Modifier.width(IntrinsicSize.Min),
        onClick = {
            onItemClick.invoke(item)
        }
    ) {
        Column(
            modifier = Modifier
                .padding(bottom = 4.dp)
                .wrapContentSize()
        ) {
            CommonCover(
                url = item.comic.cover,
                contentDescription = item.comic.name
            )
            Text(
                text = item.comic.name,
                style = MaterialTheme.typography.titleSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = item.comic.authorThat(),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.alpha(0.78f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis

            )
        }
    }
}

@Composable
fun LeaderBoardItemPlaceholder() {
    Card(
        modifier = Modifier.width(IntrinsicSize.Min),
    ) {
        Column(
            modifier = Modifier
                .padding(bottom = 4.dp)
                .wrapContentSize()
        ) {
            CommonCover(
                url = "",
                contentDescription = ""
            )
            Text(
                text = stringResource(id = R.string.waiting),
                style = MaterialTheme.typography.titleSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = stringResource(id = R.string.waiting),
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.alpha(0.78f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}