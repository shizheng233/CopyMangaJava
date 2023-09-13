package com.shicheeng.copymanga.ui.screen.manga

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import com.shicheeng.copymanga.LocalMainBottomNavigationPadding
import com.shicheeng.copymanga.LocalSettingPreference
import com.shicheeng.copymanga.MainActivity
import com.shicheeng.copymanga.MangaReaderActivity
import com.shicheeng.copymanga.R
import com.shicheeng.copymanga.data.MangaSortBean
import com.shicheeng.copymanga.data.local.LocalChapter
import com.shicheeng.copymanga.server.DownloadService
import com.shicheeng.copymanga.ui.screen.compoents.ErrorScreen
import com.shicheeng.copymanga.ui.screen.compoents.LoadingScreen
import com.shicheeng.copymanga.ui.screen.compoents.PlainButton
import com.shicheeng.copymanga.ui.screen.compoents.VerticalFastScroller
import com.shicheeng.copymanga.ui.screen.setting.SettingPref
import com.shicheeng.copymanga.util.UIState
import com.shicheeng.copymanga.util.copy
import com.shicheeng.copymanga.viewmodel.MangaInfoViewModel
import dagger.hilt.android.EntryPointAccessors
import soup.compose.material.motion.MaterialFade

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalMaterialApi::class
)
@Composable
fun MangaDetailScreen(
    pathWord: String?,
    viewModel: MangaInfoViewModel = assistedHiltMangaInfoViewModel(pathWord = pathWord),
    onTagsClick: (MangaSortBean) -> Unit,
    onAuthorClick: (String) -> Unit,
    onCommentClick: (comicUUID: String) -> Unit,
    onNavigation: () -> Unit,
) {
    val content by viewModel.mangaInfo.collectAsState()
    val chapters by viewModel.chapters.collectAsState()
    val selectedChapters by viewModel.selectChapter.collectAsState()
    val lastWatchChapter by viewModel.lastWatchChapter.collectAsState()
    val lastWebWatchChapter by viewModel.lastWebLookedChapter.collectAsState()

    if (content is UIState.Loading) {
        LoadingScreen()
        return
    }

    if (content is UIState.Error<*>) {
        ErrorScreen(
            errorMessage = (content as UIState.Error<*>).errorMessage.message
                ?: stringResource(id = R.string.error)
        ) {
            viewModel.chapterLoadForce()
        }
        return
    }

    val contentSuccess = content as UIState.Success
    val layoutDirection = LocalLayoutDirection.current
    val bottomPadding = LocalMainBottomNavigationPadding.current
    val context = LocalContext.current
    val topAppBarState = TopAppBarDefaults.pinnedScrollBehavior()
    val refreshState = rememberPullRefreshState(
        refreshing = chapters == UIState.Loading,
        onRefresh = {
            viewModel.chapterLoadForce()
        }
    )
    var expanded by remember { mutableStateOf(false) }
    val inSelectedMode by remember { derivedStateOf { selectedChapters.isNotEmpty() } }
    var tipDialogShow by remember { mutableStateOf(false) }
    val setting = LocalSettingPreference.current
    val haptic = LocalHapticFeedback.current
    var bottomAuthorsSelector by remember { mutableStateOf(false) }
    val lazyListState = rememberLazyListState()


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.manga_detail)) },
                navigationIcon = {
                    if (inSelectedMode) {
                        PlainButton(
                            id = R.string.exit_select_mode,
                            drawableRes = R.drawable.baseline_close_24
                        ) {
                            viewModel.deselectedAllItem()
                        }
                    } else {
                        PlainButton(
                            id = R.string.back_to_up,
                            drawableRes = R.drawable.ic_arrow_back,
                            onButtonClick = onNavigation
                        )
                    }
                },
                scrollBehavior = topAppBarState,
                actions = {
                    if (!inSelectedMode) {
                        PlainButton(
                            id = {
                                if (lastWebWatchChapter?.results?.collect != null) {
                                    R.string.remove_add_to_lib
                                } else {
                                    R.string.add_to_lib
                                }
                            },
                            drawableRes = {
                                if (lastWebWatchChapter?.results?.collect != null) {
                                    R.drawable.baseline_library_add_check_24
                                } else {
                                    R.drawable.baseline_library_add_24
                                }
                            }
                        ) {
                            viewModel.comicAddWebLib(
                                mangaUUID = contentSuccess.content.comicUUID,
                                add = lastWebWatchChapter?.results?.collect == null
                            )
                        }
                        PlainButton(
                            id = {
                                if (contentSuccess.content.isSubscribe) {
                                    R.string.unsubscribe_for_updates
                                } else {
                                    R.string.subscribe_for_updates
                                }
                            },
                            drawableRes = {
                                if (contentSuccess.content.isSubscribe) {
                                    R.drawable.iconmonstr_rss_feed_baseline
                                } else {
                                    R.drawable.iconmonstr_rss_feed_outline
                                }
                            }
                        ) {
                            tipDialogShow = true
                            viewModel.comicUpdate(contentSuccess.content.isSubscribe.not())
                        }
                        Box(
                            modifier = Modifier
                                .wrapContentSize(Alignment.TopStart)
                        ) {
                            PlainButton(
                                id = R.string.download_manga,
                                drawableRes = R.drawable.outline_file_download_24
                            ) {
                                expanded = true
                            }
                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = {
                                    expanded = false
                                },
                                modifier = Modifier
                                    .widthIn(min = 112.dp, max = 280.dp)
                                    .zIndex(1f)
                            ) {
                                DropdownMenuItem(
                                    text = { Text(text = stringResource(R.string.download_first_5)) },
                                    onClick = {
                                        val firstChapters = viewModel.selectFirst5() ?: emptyList()
                                        pathWord?.let {
                                            DownloadService.startDownloadService(
                                                context = context,
                                                pathWord = it,
                                                uuids = firstChapters
                                                    .map { x -> x.uuid }
                                                    .toTypedArray()
                                            )
                                        }
                                        expanded = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text(text = stringResource(R.string.download_last_5)) },
                                    onClick = {
                                        val lastChapters = viewModel.selectLast5() ?: emptyList()
                                        pathWord?.let {
                                            DownloadService.startDownloadService(
                                                context = context,
                                                pathWord = it,
                                                uuids = lastChapters
                                                    .map { x -> x.uuid }
                                                    .toTypedArray()
                                            )
                                        }
                                        expanded = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text(text = stringResource(id = R.string.download_all)) },
                                    onClick = {
                                        if (chapters is UIState.Success) {
                                            pathWord?.let {
                                                DownloadService.startDownloadService(
                                                    context = context,
                                                    pathWord = it,
                                                    uuids = ((chapters as UIState.Success<List<LocalChapter>>).content)
                                                        .map { x -> x.uuid }
                                                        .toTypedArray()
                                                )
                                            }
                                        }
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                },
            )
        },
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
            MaterialFade(
                visible = chapters is UIState.Success && !inSelectedMode
            ) {
                ExtendedFloatingActionButton(
                    onClick = {
                        if (setting.webReadPoint) {
                            //漫画UUID
                            val uuid = lastWebWatchChapter?.results?.browse?.chapterUuid
                                ?: lastWatchChapter?.uuid
                                ?: (chapters as UIState.Success).content[0].uuid
                            //漫画PathWord
                            val pathWord2 = lastWebWatchChapter?.results?.browse?.pathWord
                                ?: lastWatchChapter?.comicPathWord
                                ?: pathWord
                                ?: (chapters as UIState.Success).content[0].comicPathWord
                            val intent = MangaReaderActivity.newInstance(
                                context = context,
                                pathWord = pathWord2,
                                uuid = uuid
                            )
                            context.startActivity(intent)
                        } else {
                            (lastWatchChapter ?: (chapters as UIState.Success).content[0]).run {
                                val intent = MangaReaderActivity.newInstance(
                                    context = context,
                                    pathWord = comicPathWord,
                                    uuid = uuid
                                )
                                context.startActivity(intent)
                            }
                        }
                    },
                    text = {
                        Text(
                            text = if (lastWatchChapter != null && contentSuccess.content.positionChapter != 0) {
                                stringResource(id = R.string.continue_read)
                            } else {
                                stringResource(id = R.string.start_read)
                            }
                        )
                    },
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_play_arrow_24),
                            contentDescription = null
                        )
                    },
                    expanded = true
                )
            }
        },
        bottomBar = {
            AnimatedVisibility(
                visible = inSelectedMode,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                MangaDetailBottomBar(
                    onDownloadClick = {
                        val downloadUUIDs = selectedChapters.map { it.uuid }
                        pathWord?.let {
                            DownloadService.startDownloadService(
                                context = context,
                                pathWord = it,
                                uuids = downloadUUIDs.toTypedArray()
                            )
                        }
                        viewModel.deselectedAllItem()
                    },
                    onMarkReadClick = {
                        viewModel.comicMarkRead(isRead = true)
                    }
                ) {
                    viewModel.comicMarkRead(isRead = false)
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier.pullRefresh(state = refreshState, enabled = true)
        ) {
            VerticalFastScroller(
                listState = lazyListState,
                topContentPadding = paddingValues.calculateTopPadding(),
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .nestedScroll(topAppBarState.nestedScrollConnection),
                    contentPadding = paddingValues.copy(
                        layoutDirection = layoutDirection,
                        bottom = paddingValues.calculateBottomPadding() + bottomPadding
                    ),
                    state = lazyListState
                ) {
                    item(
                        key = MangaDetailKey.HEADER,
                        contentType = MangaDetailKey.HEADER
                    ) {
                        DetailHeader(mangaInfoDataModel = contentSuccess.content) {
                            bottomAuthorsSelector = true
                        }
                    }
                    item(
                        key = MangaDetailKey.ROW_INFO,
                        contentType = MangaDetailKey.ROW_INFO
                    ) {
                        DetailRowInfo(
                            mangaInfoDataModel = contentSuccess.content,
                            chapters = chapters
                        ) {
                            onCommentClick(contentSuccess.content.comicUUID)
                        }
                    }
                    item(
                        key = MangaDetailKey.SUMMARY,
                        contentType = MangaDetailKey.SUMMARY
                    ) {
                        MangaExpandSummary(
                            defaultExpandState = false,
                            description = contentSuccess.content.mangaDetail,
                            tags = {
                                contentSuccess.content.themeList
                            },
                            onTagsClick = onTagsClick
                        )
                    }
                    item(
                        key = MangaDetailKey.LIST_DESC,
                        contentType = MangaDetailKey.LIST_DESC
                    ) {
                        Text(
                            text = stringResource(R.string.chapters_list),
                            modifier = Modifier.padding(all = 16.dp),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                    chapterList(
                        selectChapters = selectedChapters,
                        inSelectMode = inSelectedMode,
                        chapterState = chapters,
                        onLongClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            viewModel.selectItem(it, !selectedChapters.contains(it))
                        },
                        webLookedUUID = lastWebWatchChapter?.results?.browse?.chapterUuid
                    ) {
                        val intent =
                            MangaReaderActivity.newInstance(context, it.comicPathWord, it.uuid)
                        context.startActivity(intent)
                    }
                }
            }

            //指示器
            Box(
                modifier = Modifier
                    .padding(top = paddingValues.calculateTopPadding())
                    .matchParentSize()
                    .clipToBounds()
            ) {
                PullRefreshIndicator(
                    refreshing = chapters == UIState.Loading,
                    state = refreshState,
                    modifier = Modifier.align(Alignment.TopCenter),
                    backgroundColor = MaterialTheme.colorScheme.background,
                    contentColor = MaterialTheme.colorScheme.primary
                )
            }
        }
    }

    if (tipDialogShow && !setting.hasKey(SettingPref.KEY_ENABLE_COMIC_UPDATE)) {
        TipDialog(
            onDismiss = {
                tipDialogShow = false
            },
            onPositive = {
                viewModel.enableComicUpdate(true)
            }
        ) {
            viewModel.enableComicUpdate(false)
        }
    }

    if (bottomAuthorsSelector) {
        MangaDetailBottomSelector(
            list = contentSuccess.content.authorList,
            onDismissRequest = { bottomAuthorsSelector = false },
            onClick = {
                onAuthorClick(it)
            }
        )
    }

    BackHandler(enabled = inSelectedMode) {
        viewModel.deselectedAllItem()
    }

}

@Composable
private fun assistedHiltMangaInfoViewModel(pathWord: String?): MangaInfoViewModel {
    val context = LocalContext.current
    val factory = EntryPointAccessors.fromActivity(
        context as Activity,
        MainActivity.ViewModelAssistedFactoryProvider::class.java
    ).infoViewModelFactory()
    requireNotNull(pathWord) { "PATH WORD IS EMPTY" }
    return viewModel(factory = MangaInfoViewModel.provideAssistedViewModel(factory, pathWord))
}