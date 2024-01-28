package com.shicheeng.copymanga.ui.screen.downloaded

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.shicheeng.copymanga.R
import com.shicheeng.copymanga.data.PersonalInnerDataModel
import com.shicheeng.copymanga.ui.screen.compoents.PlainButton
import com.shicheeng.copymanga.ui.screen.download.EmptyScreen
import com.shicheeng.copymanga.util.copyComposable
import com.shicheeng.copymanga.viewmodel.DownloadViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DownloadedScreen(
    cViewModel: DownloadViewModel = hiltViewModel(),
    onNavigate: () -> Unit,
    onClick: (String?) -> Unit,
) {
    val list by cViewModel.list.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.download_manga)) },
                navigationIcon = {
                    PlainButton(
                        id = R.string.back_to_up,
                        drawableRes = R.drawable.ic_arrow_back,
                        onButtonClick = onNavigate
                    )
                }
            )
        }
    ) { paddingValues ->
        if (list.isEmpty()) {
            EmptyScreen(paddingValues = paddingValues)
        } else {
            LazyVerticalGrid(
                contentPadding = paddingValues.copyComposable(
                    start = 16.dp, end = 16.dp
                ),
                columns = GridCells.Fixed(3),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(list) { innerItem: PersonalInnerDataModel? ->
                    innerItem?.let {
                        DownloadedListItem(url = innerItem.url, title = innerItem.name) {
                            if (innerItem.pathWord != null) {
                                onClick(innerItem.pathWord)
                            }
                        }
                    }
                }
            }
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DownloadedListItem(
    url: Uri?,
    title: String,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier.width(IntrinsicSize.Min),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.padding(bottom = 4.dp)
        ) {
            AsyncImage(
                model = url,
                contentDescription = null,
                placeholder = ColorPainter(MaterialTheme.colorScheme.primary),
                modifier = Modifier
                    .clip(MaterialTheme.shapes.medium)
                    .aspectRatio(2f / 3f),
                contentScale = ContentScale.Crop
            )
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(4.dp)
            )
        }
    }
}