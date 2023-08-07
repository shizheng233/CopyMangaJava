package com.shicheeng.copymanga.ui.screen.compoents

import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage


@Composable
fun CommonCover(
    url: String,
    contentDescription: String,
    shape: Shape = MaterialTheme.shapes.medium,
) {
    AsyncImage(
        model = url,
        contentDescription = contentDescription,
        placeholder = ColorPainter(MaterialTheme.colorScheme.primary),
        modifier = Modifier
            .aspectRatio(2f / 3f)
            .clip(shape),
        contentScale = ContentScale.Crop
    )
}