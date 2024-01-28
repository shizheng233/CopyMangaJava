package com.shicheeng.copymanga.ui.screen.manga

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import com.shicheeng.copymanga.LocalSettingPreference
import com.shicheeng.copymanga.MangaReaderActivity
import com.shicheeng.copymanga.R
import com.shicheeng.copymanga.data.MangaSortBean
import com.shicheeng.copymanga.data.local.LocalChapter
import com.shicheeng.copymanga.ui.screen.compoents.ErrorScreen
import com.shicheeng.copymanga.ui.screen.compoents.LoadingScreen
import com.shicheeng.copymanga.ui.screen.compoents.PlainButton
import com.shicheeng.copymanga.ui.screen.compoents.pullrefresh.SwipeRefresh
import com.shicheeng.copymanga.ui.screen.compoents.pullrefresh.rememberSwipeRefreshState
import com.shicheeng.copymanga.ui.screen.setting.SettingPref
import com.shicheeng.copymanga.util.UIState
import com.shicheeng.copymanga.util.copy
import com.shicheeng.copymanga.viewmodel.MangaInfoViewModel
import soup.compose.material.motion.MaterialFade

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MangaDetailScreen(
    pathWord: String?,
    viewModel: MangaInfoViewModel = hiltViewModel(),
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
    val context = LocalContext.current
    val refreshState = rememberSwipeRefreshState(isRefreshing = chapters is UIState.Loading)
    var expanded by remember { mutableStateOf(false) }
    val inSelectedMode by remember { derivedStateOf { selectedChapters.isNotEmpty() } }
    var tipDialogShow by remember { mutableStateOf(false) }
    val setting = LocalSettingPreference.current
    val haptic = LocalHapticFeedback.current
    var bottomAuthorsSelector by remember { mutableStateOf(false) }
    val lazyListState = rememberLazyListState()


    Scaffold(
        topBar = {
            val firstVisibleItemIndex by remember { derivedStateOf { lazyListState.firstVisibleItemIndex } }
            val firstVisibleItemScrollOffset by remember { derivedStateOf { lazyListState.firstVisibleItemScrollOffset } }
            val animatedTitleAlpha by animateFloatAsState(
                if (firstVisibleItemIndex > 0) 1f else 0f,
                label = "titleAlpha",
            )
            val animatedBgAlpha by animateFloatAsState(
                if (firstVisibleItemIndex > 0 || firstVisibleItemScrollOffset > 0) 1f else 0f,
                label = "bgAlpha",
            )
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.manga_detail),
                        modifier = Modifier.alpha(
                            if (inSelectedMode) 1f else animatedTitleAlpha,
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
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
                actions = {
                    if (!inSelectedMode) {
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
                                        viewModel.downloadManga(
                                            firstChapters
                                                .map { x -> x.uuid }
                                                .toTypedArray()
                                        )
                                        expanded = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text(text = stringResource(R.string.download_last_5)) },
                                    onClick = {
                                        val lastChapters = viewModel.selectLast5() ?: emptyList()
                                        pathWord?.let {
                                            lastChapters
                                                .map { x -> x.uuid }
                                                .toTypedArray()

                                        }
                                        expanded = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text(text = stringResource(id = R.string.download_all)) },
                                    onClick = {
                                        if (chapters is UIState.Success) {
                                            val uuids =
                                                ((chapters as UIState.Success<List<LocalChapter>>).content)
                                                    .map { x -> x.uuid }
                                                    .toTypedArray()
                                            viewModel.downloadManga(uuids)
                                        }
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme
                        .surfaceColorAtElevation(3.dp)
                        .copy(alpha = if (inSelectedMode) 1f else animatedBgAlpha),
                ),
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
                        val downloadUUIDs = selectedChapters.map { it.uuid }.toTypedArray()
                        viewModel.downloadManga(downloadUUIDs)
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
        SwipeRefresh(
            state = refreshState,
            onRefresh = {
                viewModel.chapterLoadForce()
            },
            indicatorPadding = paddingValues
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize(),
                contentPadding = paddingValues.copy(
                    layoutDirection = layoutDirection,
                    bottom = paddingValues.calculateBottomPadding() + 64.dp,
                    top = 0.dp
                ),
                state = lazyListState
            ) {
                item(
                    key = MangaDetailKey.HEADER,
                    contentType = MangaDetailKey.HEADER
                ) {
                    DetailInfoBox(
                        mangaInfoDataModel = contentSuccess.content,
                        topPadding = paddingValues.calculateTopPadding()
                    ) {
                        bottomAuthorsSelector = true
                    }
                }
                item(
                    key = MangaDetailKey.ROW_INFO,
                    contentType = MangaDetailKey.ROW_INFO
                ) {
                    DetailRowInfo(
                        onCommentClick = {
                            onCommentClick(contentSuccess.content.comicUUID)
                        },
                        isCollect = lastWebWatchChapter?.results?.collect != null,
                        onCollectClicked = {
                            viewModel.comicAddWebLib(
                                mangaUUID = contentSuccess.content.comicUUID,
                                add = lastWebWatchChapter?.results?.collect == null
                            )
                        },
                        isSubscribed = contentSuccess.content.isSubscribe,
                        onSubscribedClick = {
                            tipDialogShow = true
                            viewModel.comicUpdate(contentSuccess.content.isSubscribe.not())
                        }
                    )
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
                    val intent = MangaReaderActivity.newInstance(context, it.comicPathWord, it.uuid)
                    context.startActivity(intent)
                }
                item(
                    key = MangaDetailKey.BOTTOM_DESC,
                    contentType = MangaDetailKey.BOTTOM_DESC
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_manga_info_main),
                            contentDescription = stringResource(R.string.disclaimer)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = stringResource(R.string.general_tips),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = stringResource(R.string.general_warning),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
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

