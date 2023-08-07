package com.shicheeng.copymanga.ui.screen.main.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.shicheeng.copymanga.R
import com.shicheeng.copymanga.data.MainPageDataModel
import com.shicheeng.copymanga.data.MangaRankMiniModel
import com.shicheeng.copymanga.ui.screen.compoents.MangaCover


@OptIn(ExperimentalFoundationApi::class)
fun LazyListScope.miniLeaderBoard(
    selectedTabIndex: Int,
    onTabClick: (Int) -> Unit,
    rankList: List<String>,
    mainPageDataModel: MainPageDataModel,
    onHeaderLineClick: () -> Unit,
    onRankItemClick: (MangaRankMiniModel) -> Unit,
) {
    item(
        key = HomeListKey.RANK,
        contentType = HomeListKey.RANK
    ) {
        HomeRowHeaderLine(
            title = stringResource(id = R.string.rank_mini),
            click = onHeaderLineClick
        )
    }

    stickyHeader {
        TabRow(selectedTabIndex = selectedTabIndex) {
            rankList.forEachIndexed { index, s ->
                Tab(
                    selected = index == selectedTabIndex,
                    onClick = { onTabClick.invoke(index) },
                    text = {
                        Text(text = s)
                    }
                )
            }
        }
    }
    when (selectedTabIndex) {
        0 -> {
            items(mainPageDataModel.listRankDay, key = { it.name }) {
                MiniRankItem(
                    miniModel = it,
                    onRankItemClick = onRankItemClick,
                    modifier = Modifier.animateItemPlacement()
                )
            }
        }

        1 -> {
            items(
                items = mainPageDataModel.listRankWeek,
                key = {
                    it.name
                }
            ) {
                MiniRankItem(
                    miniModel = it,
                    onRankItemClick = onRankItemClick,
                    modifier = Modifier.animateItemPlacement()
                )
            }
        }

        2 -> {
            items(
                items = mainPageDataModel.listRankMonth,
                key = {
                    it.name
                }
            ) {
                MiniRankItem(
                    miniModel = it,
                    onRankItemClick = onRankItemClick,
                    modifier = Modifier.animateItemPlacement()
                )
            }
        }
    }

}


@Composable
fun MiniRankItem(
    modifier: Modifier = Modifier,
    miniModel: MangaRankMiniModel,
    onRankItemClick: (MangaRankMiniModel) -> Unit,
) {
    Row(
        modifier = modifier
            .clickable { onRankItemClick.invoke(miniModel) }
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        MangaCover.ExtraSmall(
            url = miniModel.urlCover,
        )
        Column(
            modifier = Modifier
                .padding(start = 8.dp, end = 8.dp)
                .weight(1f)
        ) {
            Text(
                text = miniModel.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = miniModel.author,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 4.dp)
            )
            Row {
                Icon(
                    painter = painterResource(id = R.drawable.ic_trend_up),
                    contentDescription = stringResource(R.string.trend_up),
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = miniModel.riseHot,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
            Text(
                text = miniModel.popular,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}

