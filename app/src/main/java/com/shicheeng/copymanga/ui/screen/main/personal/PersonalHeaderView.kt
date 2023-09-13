package com.shicheeng.copymanga.ui.screen.main.personal

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Badge
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import coil.compose.AsyncImage
import com.shicheeng.copymanga.R
import com.shicheeng.copymanga.data.login.LocalLoginDataModel
import com.shicheeng.copymanga.ui.theme.ElevationTokens

private const val AVATAR_HOST_URL = "https://hi77-overseas.mangafuna.xyz/"

fun hostFor(string: String): String {
    return if (string.toUri().scheme == "https") {
        string
    } else {
        buildString {
            append(AVATAR_HOST_URL)
            append(string)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonalHeaderView(
    localLoginDataModel: LocalLoginDataModel?,
    click: () -> Unit,
) {
    val backgroundV = MaterialTheme.colorScheme.surfaceColorAtElevation(ElevationTokens.Level1)
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Image(
            painter = painterResource(id = R.drawable.undraw_personal_file_re),
            contentDescription = null,
            modifier = Modifier
                .height(150.dp)
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .clip(MaterialTheme.shapes.large)
                .drawWithContent {
                    drawRect(color = backgroundV)
                    drawContent()
                },
            contentScale = ContentScale.FillHeight
        )
        Spacer(modifier = Modifier.height(16.dp))
        ListItem(
            headlineContent = {
                Row(
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = localLoginDataModel?.nikeName
                            ?: stringResource(id = R.string.no_login)
                    )
                    if (localLoginDataModel?.isExpired == true) {
                        Badge(
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.padding(start = 4.dp)
                        ) {
                            Text(
                                text = stringResource(id = R.string.login_expired),
                                modifier = Modifier.padding(4.dp)
                            )
                        }
                    }
                }
            },
            leadingContent = {
                if (localLoginDataModel != null) {
                    AsyncImage(
                        model = hostFor(localLoginDataModel.avatarImageUrl),
                        contentDescription = null,
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape),
                        placeholder = ColorPainter(MaterialTheme.colorScheme.secondaryContainer)
                    )
                } else {
                    AvatarPlaceholder()
                }
            },
            supportingContent = if (localLoginDataModel != null) {
                {
                    Text(text = localLoginDataModel.userName)
                }
            } else null,
            trailingContent = {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = stringResource(id = R.string.topic_detail_text)
                )
            },
            modifier = Modifier.clickable { click() }
        )
    }
}

@Composable
fun AvatarPlaceholder() {
    Image(
        painter = painterResource(id = R.drawable.undraw_drink_coffee),
        contentDescription = null,
        modifier = Modifier
            .size(72.dp)
            .clip(CircleShape)
    )
}


