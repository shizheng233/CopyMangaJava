package com.shicheeng.copymanga.ui.screen.main.personal

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.shicheeng.copymanga.R
import com.shicheeng.copymanga.viewmodel.PersonalViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonalScreen(
    viewModel: PersonalViewModel = hiltViewModel(),
    onHistoryClick: () -> Unit,
    onLibraryClick: () -> Unit,
    onDownloadClick: () -> Unit,
    onSubscribedClick: () -> Unit,
    onPersonalHeaderClick: (isHadLoginUser: Boolean) -> Unit,
    onSettingClick: () -> Unit,
) {

    val user by viewModel.user.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(id = R.string.personal))
                }
            )
        }
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = it
        ) {
            item(
                key = PersonalToken.HEADER,
                contentType = PersonalToken.HEADER
            ) {
                PersonalHeaderView(localLoginDataModel = user) {
                    onPersonalHeaderClick(user != null)
                }
            }
            item(
                key = PersonalToken.TOP_DIVIDER,
                contentType = PersonalToken.TOP_DIVIDER
            ) {
                HorizontalDivider()
            }
            item(
                key = PersonalToken.ITEM_HISTORY,
                contentType = PersonalToken.ITEM_HISTORY
            ) {
                PersonalItem(
                    id = R.string.history,
                    iconId = R.drawable.baseline_history_24,
                    onClick = onHistoryClick
                )
            }
            item {
                PersonalItem(
                    id = R.string.subscribe,
                    iconId = R.drawable.baseline_rss_feed_24,
                    onClick = onSubscribedClick
                )
            }
            item(
                key = PersonalToken.ITEM_BOOK_SHELF,
                contentType = PersonalToken.ITEM_BOOK_SHELF
            ) {
                PersonalItem(
                    id = R.string.shelf_cloud,
                    iconId = R.drawable.outline_library_books_24,
                    onClick = onLibraryClick
                )
            }
            item(
                key = PersonalToken.ITEM_DOWNLOADED_MANGA,
                contentType = PersonalToken.ITEM_DOWNLOADED_MANGA
            ) {
                PersonalItem(
                    id = R.string.download_manga,
                    iconId = R.drawable.outline_download_24,
                    onClick = onDownloadClick
                )
            }
            item(
                key = PersonalToken.BOTTOM_DIVIDER,
                contentType = PersonalToken.BOTTOM_DIVIDER
            ) {
                HorizontalDivider()
            }
            item(
                key = PersonalToken.ITEM_SETTING,
                contentType = PersonalToken.ITEM_SETTING
            ) {
                PersonalItem(
                    id = R.string.setting,
                    iconId = R.drawable.ic_setting_outline,
                    onClick = onSettingClick
                )
            }
        }
    }
}

@Composable
private fun PersonalItem(
    @StringRes id: Int,
    @DrawableRes iconId: Int,
    onClick: () -> Unit,
) {
    ListItem(
        headlineContent = {
            Text(text = stringResource(id = id))
        },
        leadingContent = {
            Icon(
                painter = painterResource(id = iconId),
                contentDescription = null
            )
        },
        modifier = Modifier.clickable { onClick() }
    )
}