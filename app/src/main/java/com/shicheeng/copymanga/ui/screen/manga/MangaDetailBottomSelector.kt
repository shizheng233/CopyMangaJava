package com.shicheeng.copymanga.ui.screen.manga

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.shicheeng.copymanga.R
import com.shicheeng.copymanga.data.info.Author

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MangaDetailBottomSelector(
    list: List<Author>,
    onDismissRequest: () -> Unit,
    onClick: (String) -> Unit,
) = ModalBottomSheet(
    onDismissRequest = onDismissRequest,
    windowInsets = WindowInsets(
        left = 0,
        top = 0,
        right = 0,
        bottom = 0
    )
) {
    Text(
        text = stringResource(R.string.author_choice),
        style = MaterialTheme.typography.titleLarge,
        modifier = Modifier.padding(16.dp)
    )
    HorizontalDivider()
    list.forEach { author ->
        ListItem(
            headlineContent = { Text(text = author.name) },
            modifier = Modifier.clickable {
                onClick(author.pathWord)
                onDismissRequest()
            },
            leadingContent = {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_person_24),
                    contentDescription = null
                )
            },
            supportingContent = {
                Text(text = author.pathWord)
            }
        )
    }
    HorizontalDivider()
    Text(
        text = stringResource(R.string.author_combine, list.size),
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier
            .padding(16.dp)
            .navigationBarsPadding()
    )
}
