package com.shicheeng.copymanga.ui.screen.setting.about

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.mikepenz.aboutlibraries.Libs
import com.mikepenz.aboutlibraries.entity.Library
import com.mikepenz.aboutlibraries.util.author
import com.mikepenz.aboutlibraries.util.withContext
import com.shicheeng.copymanga.BuildConfig
import com.shicheeng.copymanga.R
import com.shicheeng.copymanga.ui.screen.compoents.PlainButton
import com.shicheeng.copymanga.ui.screen.compoents.VerticalFastScroller
import com.shicheeng.copymanga.util.openUrl

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(
    onBack: () -> Unit,
) {

    val content = LocalContext.current
    val topAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val libs = remember(Libs.Builder().withContext(content).build()::libraries)
    val thankfulApps = rememberThankfulApps()
    val lazyState = rememberLazyListState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(id = R.string.about))
                },
                navigationIcon = {
                    PlainButton(
                        id = R.string.back_to_up,
                        drawableRes = R.drawable.ic_arrow_back,
                        onButtonClick = onBack
                    )
                },
                scrollBehavior = topAppBarScrollBehavior
            )
        }
    ) { paddingValues ->
        VerticalFastScroller(
            listState = lazyState,
            topContentPadding = paddingValues.calculateTopPadding()
        ) {
            LazyColumn(
                contentPadding = paddingValues,
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
                state = lazyState
            ) {
                item {
                    AboutScreenHeader()
                }
                item {
                    Text(
                        text = stringResource(R.string.thankful_app),
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                items(thankfulApps) { aboutUiModel ->
                    ListItem(
                        headlineContent = {
                            Text(text = aboutUiModel.name)
                        },
                        supportingContent = {
                            Text(text = aboutUiModel.description)
                        },
                        leadingContent = {
                            Icon(
                                painter = rememberAsyncImagePainter(model = aboutUiModel.url),
                                contentDescription = null
                            )
                        },
                        modifier = Modifier.clickable {
                            content openUrl aboutUiModel.url
                        }
                    )
                }
                item {
                    Text(
                        text = stringResource(R.string.open_source),
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                items(libs) { library ->
                    AboutListItem(library = library)
                }
                item {
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreenHeader() {

    val backgroundColor = MaterialTheme.colorScheme.secondaryContainer
    val density = LocalDensity.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            contentDescription = "logo",
            painter = painterResource(id = R.mipmap.ic_copy_foreground),
            modifier = Modifier
                .drawWithContent {
                    drawCircle(
                        color = backgroundColor,
                        radius = with(density) {
                            130
                                .toDp()
                                .toPx()
                        }
                    )
                    drawContent()
                },
            colorFilter = ColorFilter
                .tint(color = contentColorFor(backgroundColor = backgroundColor))
        )
        Spacer(modifier = Modifier.height(8.dp))
        BadgedBox(
            badge = {
                Badge(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Text(text = BuildConfig.VERSION_NAME)
                }
            }
        ) {
            Text(
                text = stringResource(id = R.string.app_name),
                style = MaterialTheme.typography.titleLarge
            )
        }

        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = stringResource(R.string.copy_manga_summary),
            modifier = Modifier.padding(horizontal = 16.dp),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutListItem(
    library: Library,
) {
    val name = remember { library.licenses.takeIf { it.isNotEmpty() }?.first()?.name }
    val context = LocalContext.current
    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        onClick = {
            library.website?.let {
                context openUrl it
            }
        }
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    painter = painterResource(
                        id = when {
                            name?.contains("MIT", ignoreCase = true) == true -> {
                                R.drawable.legal_license_mit_svgrepo_com
                            }

                            name?.contains("Apache", ignoreCase = true) == true -> {
                                R.drawable.apache_svgrepo_com
                            }

                            else -> {
                                R.drawable.open_source_fill_svgrepo_com
                            }
                        }
                    ),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(8.dp)
                        .size(32.dp)
                )
                Column(
                    modifier = Modifier.padding(bottom = 4.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = library.name,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = library.author.ifBlank { stringResource(id = android.R.string.unknownName) },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            HorizontalDivider()
            Text(
                text = library.description ?: stringResource(id = android.R.string.unknownName),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                CompositionLocalProvider(
                    LocalTextStyle provides MaterialTheme.typography.labelSmall,
                    LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant
                ) {
                    Text(
                        text = library.licenses.takeIf { it.isNotEmpty() }
                            ?.joinToString { it.name }
                            ?: stringResource(id = android.R.string.unknownName),
                        modifier = Modifier.padding(end = 16.dp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = library.artifactVersion
                            ?: stringResource(id = android.R.string.unknownName)
                    )
                }

            }
        }

    }
}

