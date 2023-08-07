package com.shicheeng.copymanga.ui.screen.download

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import com.shicheeng.copymanga.R

@Composable
fun EmptyScreen(
    paddingValues: PaddingValues = PaddingValues(0.dp),
    @StringRes id: Int = R.string.empty_download,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(id = id),
            fontStyle = FontStyle.Italic,
            color = MaterialTheme.colorScheme.onSurface
        )

    }
}