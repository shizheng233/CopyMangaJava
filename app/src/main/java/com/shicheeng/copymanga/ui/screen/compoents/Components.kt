package com.shicheeng.copymanga.ui.screen.compoents

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * 带有[PlainTooltipBox]的[IconButton]。
 * @param id String的资源ID,
 * @param drawableRes 图片的资源id,
 * @param onButtonClick 点击事件回调。
 */
@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun PlainButton(
    modifier: Modifier = Modifier,
    @StringRes id: () -> Int,
    @DrawableRes drawableRes: () -> Int,
    onButtonClick: () -> Unit,
) {

    TooltipBox(
        tooltip = {
            NormalTooltip(text = stringResource(id = id()))
        },
        modifier = modifier,
        positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
        state = rememberTooltipState()
    ) {
        IconButton(
            onClick = onButtonClick,
            modifier = Modifier
        ) {
            Icon(
                painter = painterResource(id = drawableRes()),
                contentDescription = stringResource(id = id())
            )
        }
    }

}

/**
 * 带有[PlainTooltipBox]的[IconButton]。
 * @param id String的资源ID,
 * @param drawableRes 图片的资源id,
 * @param onButtonClick 点击事件回调。
 */
@Composable
fun PlainButton(
    @StringRes id: Int,
    @DrawableRes drawableRes: Int,
    onButtonClick: () -> Unit,
) {
    PlainButton(id = { id }, drawableRes = { drawableRes }, onButtonClick = onButtonClick)
}

@Composable
private fun NormalTooltip(
    modifier: Modifier = Modifier,
    text: String,
) {
    Surface(
        contentColor = MaterialTheme.colorScheme.tertiaryContainer,
        color = MaterialTheme.colorScheme.onTertiaryContainer,
        shape = MaterialTheme.shapes.extraSmall,
        modifier = modifier
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleSmall
                .copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(8.dp)
        )
    }
}