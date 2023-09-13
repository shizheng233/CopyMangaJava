package com.shicheeng.copymanga.ui.screen.authorsmanga

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import com.shicheeng.copymanga.R
import com.shicheeng.copymanga.ui.screen.compoents.PlainButton
import com.shicheeng.copymanga.ui.screen.compoents.pagingLoadingIndication
import com.shicheeng.copymanga.ui.screen.list.CommonListItem
import com.shicheeng.copymanga.util.copyComposable
import com.shicheeng.copymanga.viewmodel.AuthorMangaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthorsMangaScreen(
    viewModel: AuthorMangaViewModel = hiltViewModel(),
    onNav: () -> Unit,
    onPathWord: (String) -> Unit,
) {
    val data = viewModel.list.collectAsLazyPagingItems()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(id = R.string.authors_manga))
                },
                navigationIcon = {
                    PlainButton(
                        id = R.string.back_to_up,
                        drawableRes = R.drawable.ic_arrow_back,
                        onButtonClick = onNav
                    )
                }
            )
        }
    ) { paddingValues ->
        LazyVerticalGrid(
            contentPadding = paddingValues.copyComposable(
                start = 16.dp,
                end = 16.dp
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            columns = GridCells.Fixed(3),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(data.itemCount) { index ->
                data[index]?.let { mangaItem ->
                    CommonListItem(
                        url = mangaItem.cover,
                        title = mangaItem.name,
                        author = mangaItem.author.joinToString { it.name }
                    ) {
                        onPathWord(mangaItem.pathWord)
                    }
                }
            }
            pagingLoadingIndication(
                loadState = data.loadState.append,
                onTry = data::retry
            )
        }
    }
}