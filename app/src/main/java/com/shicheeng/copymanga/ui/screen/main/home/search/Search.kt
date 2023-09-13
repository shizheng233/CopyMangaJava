package com.shicheeng.copymanga.ui.screen.main.home.search

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.shicheeng.copymanga.R
import com.shicheeng.copymanga.util.copyComposable
import com.shicheeng.copymanga.viewmodel.SearchViewModel

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    searchViewModel: SearchViewModel = hiltViewModel(),
    onSearch: (String) -> Unit,
    onBack: () -> Unit,
) {

    val (searchKeyWord, onSaveKeyWord) = rememberSaveable { mutableStateOf("") }
    val historyWords by searchViewModel.searchedHistoryWord.collectAsState()
    val topAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        topBar = {
            FullScreenSearchViewHeader(
                value = searchKeyWord,
                valueChange = {
                    onSaveKeyWord(it)
                    searchViewModel.upWord(it)
                },
                onSearch = {
                    onSearch(it)
                },
                onBackClick = onBack,
                topAppBarScrollBehavior = topAppBarScrollBehavior
            ) {
                onSaveKeyWord("")
            }
        }
    ) { paddingValue ->
        LazyColumn(
            modifier = Modifier.nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
            contentPadding = paddingValue.copyComposable(
                top = 16.dp + paddingValue.calculateTopPadding()
            )
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
