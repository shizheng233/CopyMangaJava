package com.shicheeng.copymanga.ui.screen.list

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.shicheeng.copymanga.ui.screen.compoents.CommonCover

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommonListItem(
    url: String,
    title: String,
    author: String,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier.width(IntrinsicSize.Min),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.padding(bottom = 4.dp)
        ) {
            CommonCover(url = url, contentDescription = title)
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = author,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.alpha(0.78f),
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
        }
    }
}