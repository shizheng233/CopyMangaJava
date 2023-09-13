package com.shicheeng.copymanga.ui.screen.main.explore

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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.shicheeng.copymanga.data.finished.Item
import com.shicheeng.copymanga.ui.screen.compoents.CommonCover

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreItem(
    modifier: Modifier = Modifier,
    item: Item,
    onItemClick: (Item) -> Unit,
) {
    Card(
        modifier = modifier.width(IntrinsicSize.Min),
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
                url = item.cover,
                contentDescription = item.name
            )
            Text(
                text = item.name,
                style = MaterialTheme.typography.titleSmall,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
            Text(
                text = item.authorReformation(),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.alpha(0.78f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}