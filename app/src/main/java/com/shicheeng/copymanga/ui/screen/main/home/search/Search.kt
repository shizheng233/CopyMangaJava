package com.shicheeng.copymanga.ui.screen.main.home.search

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.shicheeng.copymanga.R
import com.shicheeng.copymanga.util.copyComposable
import com.shicheeng.copymanga.viewmodel.SearchViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SearchScreen(
    searchViewModel: SearchViewModel = hiltViewModel(),
    onSearch: (String) -> Unit,
    onBack: () -> Unit,
) {

    val (searchKeyWord, onSaveKeyWord) = rememberSaveable { mutableStateOf("") }
    val historyWords by searchViewModel.searchedHistoryWord.collectAsState()

    Scaffold(
        topBar = {
            FullScreenSearchView(
                value = searchKeyWord,
                valueChange = {
                    onSaveKeyWord(it)
                    searchViewModel.upWord(it)
                },
                onSearch = {
                    onSearch(it)
                },
                onBackClick = onBack,
                modifier = Modifier.systemBarsPadding()
            ) {
                onSaveKeyWord("")
            }
        }
    ) { paddingValue ->
        LazyColumn(
            modifier = Modifier,
            contentPadding = paddingValue.copyComposable(top = 72.dp + 36.dp)
        ) {
            items(
                items = historyWords,
                key = { it }
            ) {
                ListItem(
                    headlineContent = {
                        Text(text = it)
                    },
                    leadingContent = {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_history_24),
                            contentDescription = null
                        )
                    },
                    modifier = Modifier
                        .animateItemPlacement()
                        .clickable {
                            onSearch(it)
                            searchViewModel.saveSearchWord(it)
                        }
                )
            }
        }
    }
}
