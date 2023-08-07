package com.shicheeng.copymanga.ui.screen.main.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.shicheeng.copymanga.R
import com.shicheeng.copymanga.data.ListBeanManga
import com.shicheeng.copymanga.ui.screen.compoents.MangaCover


@Composable
fun HomeBarColumn(
    title: String,
    list: List<ListBeanManga>,
    onHeaderLineClick: () -> Unit,
    onEachClick: (ListBeanManga) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        HomeRowHeaderLine(title = title, click = onHeaderLineClick)
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(all = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(list) {
                HomeBarColumnCover(listBeanManga = it, click = onEachClick)
            }
        }
    }
}

@Composable
fun HomeRowHeaderLine(
    title: String,
    click: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.weight(1f)
        )
        TextButton(
            modifier = Modifier,
            onClick = click,
        ) {
            Text(
                text = stringResource(R.string.see_all),
                modifier = Modifier.align(Alignment.CenterVertically)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeRowHeaderLinePriview() {
    HomeRowHeaderLine(title = "haokande") {

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeBarColumnCover(
    listBeanManga: ListBeanManga,
    click: (ListBeanManga) -> Unit,
) {
    Card(
        onClick = { click.invoke(listBeanManga) }
    ) {
        Column(
            modifier = Modifier
                .width(IntrinsicSize.Min)
                .padding(bottom = 4.dp)
        ) {
            MangaCover.Big(
                url = listBeanManga.urlCoverManga,
                shape = MaterialTheme.shapes.medium
            )
            Text(
                text = listBeanManga.nameManga,
                style = MaterialTheme.typography.titleSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = listBeanManga.authorManga,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                overflow = TextOverflow.Ellipsis
            )
        }

    }
}
