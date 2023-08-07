package com.shicheeng.copymanga.ui.screen.compoents

import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

enum class MangaCover(val size: Dp) {
    /**
     * 最小的封面，大小为65dp
     */
    ExtraSmall(65.dp),

    /**
     * 小的封面，大小为100dp
     */
    Small(100.dp),

    /**
     * 大的封面，大小为160dp
     */
    Big(160.dp);

    @Composable
    operator fun invoke(
        url: Any?,
        shape: Shape? = MaterialTheme.shapes.extraSmall,
    ) {
        AsyncImage(
            model = url,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .width(size)
                .aspectRatio(2f / 3f)
                .then(
                    if (shape != null) {
                        Modifier.clip(shape)
                    } else {
                        Modifier
                    }
                ),
            placeholder = ColorPainter(MaterialTheme.colorScheme.outline)
        )
    }

}