package com.shicheeng.copymanga.ui.screen.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import com.shicheeng.copymanga.LocalMainBottomNavigationPadding
import com.shicheeng.copymanga.R
import com.shicheeng.copymanga.ui.screen.compoents.PlainButton
import com.shicheeng.copymanga.ui.screen.compoents.pagingLoadingIndication
import com.shicheeng.copymanga.util.copy
import com.shicheeng.copymanga.viewmodel.MangaRecommendListViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecommendScreen(
    recommendViewModel: MangaRecommendListViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onPathWord: (String) -> Unit,
) {
    val list = recommendViewModel.recommendMangaList.collectAsLazyPagingItems()
    val topAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val paddingBottom = LocalMainBottomNavigationPadding.current
    val layoutDirection = LocalLayoutDirection.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.recommend)) },
                scrollBehavior = topAppBarScrollBehavior,
                navigationIcon = {
                    PlainButton(
                        id = R.string.back_to_up,
                        drawableRes = R.drawable.ic_arrow_back,
                        onButtonClick = onBack
                    )
                }
            )
        }
    ) { paddingValues ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            contentPadding = paddingValues.copy(
                layoutDirection = layoutDirection,
                bottom = paddingValues.calculateBottomPadding() + paddingBottom,
                start = 16.dp,
                end = 16.dp
            ),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(list.itemCount) { itemIndex ->
                list[itemIndex]?.let { item ->
                    CommonListItem(
                        url = item.comic.cover,
                        title = item.comic.name,
                        author = item.comic.authorReformation()
                    ) {
                        onPathWord.invoke(item.comic.pathWord)
                    }
                }
            }
            pagingLoadingIndication(
                loadState = list.loadState.append,
                onTry = list::retry
            )
        }
    }
}

