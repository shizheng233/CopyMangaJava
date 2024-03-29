package com.shicheeng.copymanga.ui.screen.compoents

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import com.shicheeng.copymanga.R

@Composable
fun LoadingScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun ErrorScreen(
    errorMessage: String,
    onTry: () -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .padding(all = 16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = errorMessage)
            FilledTonalButton(onClick = onTry) {
                Text(text = stringResource(id = R.string.retry))
            }
        }
    }
}

@Composable
fun ErrorScreen(
    errorMessage: String,
    onTry: () -> Unit,
    secondaryText: String,
    onSecondaryClick: () -> Unit = { },
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .padding(all = 16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = errorMessage)
            Spacer(modifier = Modifier.height(4.dp))
            Row {
                FilledTonalButton(onClick = onTry) {
                    Text(text = stringResource(id = R.string.retry))
                }
                FilledTonalButton(
                    onClick = onSecondaryClick,
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Text(text = secondaryText)
                }
            }
        }
    }
}

@Composable
fun ErrorScreen(
    errorMessage: String,
    onTry: () -> Unit,
    needSecondaryText: Boolean,
    secondaryText: String,
    onSecondaryClick: () -> Unit = { },
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .padding(all = 16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = errorMessage)
            Spacer(modifier = Modifier.height(4.dp))
            Row {
                FilledTonalButton(onClick = onTry) {
                    Text(text = stringResource(id = R.string.retry))
                }
                if (needSecondaryText) {
                    FilledTonalButton(
                        onClick = onSecondaryClick,
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Text(text = secondaryText)
                    }
                }
            }
        }
    }
}

fun LazyGridScope.pagingLoadingIndication(loadState: LoadState, onTry: () -> Unit) {
    item(
        span = {
            GridItemSpan(3)
        }
    ) {
        when (loadState) {
            is LoadState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                }
            }

            is LoadState.NotLoading -> {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(id = R.string.all_clear),
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            is LoadState.Error -> {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        modifier = Modifier
                            .padding(all = 16.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = stringResource(id = R.string.load_failure))
                        FilledTonalButton(onClick = onTry) {
                            Text(text = stringResource(id = R.string.retry))
                        }
                    }
                }
            }
        }
    }
}

fun LazyListScope.pagingLoadingIndication(loadState: LoadState, onTry: () -> Unit) {
    item(
        contentType = "pagingLoadingIndication",
        key = "pagingLoadingIndication"
    ) {
        when (loadState) {
            is LoadState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                }
            }

            is LoadState.NotLoading -> {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(id = R.string.all_clear),
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            is LoadState.Error -> {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        modifier = Modifier
                            .padding(all = 16.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = stringResource(id = R.string.load_failure))
                        FilledTonalButton(onClick = onTry) {
                            Text(text = stringResource(id = R.string.retry))
                        }
                    }
                }
            }
        }
    }
}