package com.shicheeng.copymanga.ui.screen.setting.about

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.unit.dp
import com.mikepenz.aboutlibraries.Libs
import com.mikepenz.aboutlibraries.util.withContext
import com.shicheeng.copymanga.BuildConfig
import com.shicheeng.copymanga.R
import com.shicheeng.copymanga.ui.screen.compoents.PlainButton

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun AboutScreen(
    onBack: () -> Unit,
) {

    val content = LocalContext.current
    val topAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val libs = remember {
        Libs.Builder()
            .withContext(content)
            .build()
            .libraries
    }

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
        LazyColumn(
            contentPadding = paddingValues,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.nestedScroll(topAppBarScrollBehavior.nestedScrollConnection)
        ) {
            item {
                AboutScreenHeader()
            }
            stickyHeader {
                Text(
                    text = stringResource(R.string.open_source),
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    color = MaterialTheme.colorScheme.primary
                )
            }
            items(libs) { library ->
                ListItem(
                    headlineContent = {
                        Text(text = library.name)
                    },
                    supportingContent = if (library.developers.isNotEmpty() || library.licenses.isNotEmpty()) {
                        {
                            Text(
                                text = (library.developers.map { it.name } + library.licenses.map { it.name })
                                    .joinToString(",")
                            )
                        }
                    } else null,
                    leadingContent = {
                        val name = library.licenses.takeIf { it.isNotEmpty() }?.first()?.name
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
                            contentDescription = name,
                        )
                    },
                    modifier = Modifier.clickable {

                    }
                )
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
        modifier = Modifier.fillMaxWidth(),
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
                            130.toDp().toPx()
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
            style = MaterialTheme.typography.titleMedium
        )
    }
}

