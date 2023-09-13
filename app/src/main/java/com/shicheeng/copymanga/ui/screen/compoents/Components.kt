package com.shicheeng.copymanga.ui.screen.compoents

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.PlainTooltipBox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource

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

    PlainTooltipBox(
        tooltip = {
            Text(text = stringResource(id = id()))
        },
        modifier = modifier,
    ) {
        IconButton(
            onClick = onButtonClick,
            modifier = Modifier.tooltipTrigger()
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