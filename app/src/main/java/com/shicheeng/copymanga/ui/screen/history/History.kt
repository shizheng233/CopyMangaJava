package com.shicheeng.copymanga.ui.screen.history

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.shicheeng.copymanga.R
import com.shicheeng.copymanga.ui.screen.compoents.PlainButton
import com.shicheeng.copymanga.ui.screen.compoents.SaveStatePager
import com.shicheeng.copymanga.ui.screen.compoents.withAppBarColor
import com.shicheeng.copymanga.ui.screen.history.local.LocalHistoryScreen
import com.shicheeng.copymanga.ui.screen.history.web.WebHistoryScreen
import com.shicheeng.copymanga.util.copyComposable
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HistoryScreen(
    navigationClick: () -> Unit,
    onRequestLogin: () -> Unit,
    onPathWord: (String) -> Unit,
) {

    val topAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val stringIds = rememberHistoryWord()
    val pagerState = rememberPagerState(pageCount = stringIds::size)
    val savableState = rememberSaveableStateHolder()
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                TopAppBar(
                    title = { Text(text = stringResource(id = R.string.history)) },
                    scrollBehavior = topAppBarScrollBehavior,
                    navigationIcon = {
                        PlainButton(
                            id = R.string.back_to_up,
                            drawableRes = R.drawable.ic_arrow_back,
                            onButtonClick = navigationClick
                        )
                    }
                )
                PrimaryTabRow(
                    selectedTabIndex = pagerState.currentPage,
                    containerColor = withAppBarColor(topAppBarState = topAppBarScrollBehavior.state),
                ) {
                    for (i in 0 until pagerState.pageCount) {
                        val interactionSource = remember(::MutableInteractionSource)
                        Tab(
                            selected = pagerState.currentPage == i,
                            onClick = {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(i)
                                }
                            },
                            text = {
                                Text(text = stringResource(id = stringIds[i]))
                            },
                            modifier = Modifier,
                            interactionSource = interactionSource
                        )
                    }
                }
            }

        }
    ) { paddingValues ->
        SaveStatePager(
            pagerState = pagerState,
            contentPadding = paddingValues.copyComposable(
                bottom = 0.dp
            ),
            savableStateHolder = savableState,
            keys = { stringIds },
            modifier = Modifier.nestedScroll(topAppBarScrollBehavior.nestedScrollConnection)
        ) { index ->
            when (index) {
                0 -> {
                    LocalHistoryScreen(
                        onPathWord = onPathWord,
                        bottomPadding = paddingValues.calculateBottomPadding()
                    )
                }

                1 -> {
                    WebHistoryScreen(
                        onPathWord = onPathWord,
                        onRequestLogin = onRequestLogin,
                        bottomPadding = paddingValues.calculateBottomPadding()
                    )
                }
            }
        }
    }
}

@Composable
private fun rememberHistoryWord() = remember {
    listOf(
        R.string.local_history,
        R.string.web_history
    )
}