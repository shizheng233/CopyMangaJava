package com.shicheeng.copymanga.ui.screen.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import com.shicheeng.copymanga.LocalMainBottomNavigationPadding
import com.shicheeng.copymanga.R
import com.shicheeng.copymanga.data.search.SearchResultDataModel
import com.shicheeng.copymanga.ui.screen.compoents.PlainButton
import com.shicheeng.copymanga.ui.screen.compoents.pagingLoadingIndication
import com.shicheeng.copymanga.ui.screen.list.CommonListItem
import com.shicheeng.copymanga.util.copyComposable
import com.shicheeng.copymanga.viewmodel.SearchResultViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchResultScreen(
    searchWord: String?,
    viewModel: SearchResultViewModel = hiltViewModel(),
    onNavigation: () -> Unit,
    onItemClick: (SearchResultDataModel) -> Unit,
) {

    val searchResultList = viewModel.searchResult.collectAsLazyPagingItems()
    val paddingBottom = LocalMainBottomNavigationPadding.current

    if (searchWord != null) {
        LaunchedEffect(key1 = searchWord) {
            viewModel.loadSearch(searchWord)
        }
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = searchWord ?: stringResource(id = android.R.string.unknownName))
                },
                navigationIcon = {
                    PlainButton(
                        id = R.string.back_to_up,
                        drawableRes = R.drawable.ic_arrow_back,
                        onButtonClick = onNavigation
                    )
                }
            )
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize(),
        ) {
            if (searchResultList.itemSnapshotList.isNotEmpty()) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    contentPadding = it.copyComposable(
                        bottom = paddingBottom,
                        end = 16.dp,
                        start = 16.dp,
                    ),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(searchResultList.itemCount) { itemIndex ->
                        searchResultList[itemIndex]?.let { item ->
                            CommonListItem(
                                url = item.cover,
                                title = item.name,
                                author = item.authorReformation()
                            ) {
                                onItemClick(item)
                            }
                        }
                    }
                    pagingLoadingIndication(
                        loadState = searchResultList.loadState.append
                    ) {
                        searchResultList.retry()
                    }
                }
            } else {
                Text(
                    text = stringResource(R.string.search_is_empty),
                    modifier = Modifier.align(Alignment.Center),
                    fontStyle = FontStyle.Italic
                )
            }
        }

    }
}