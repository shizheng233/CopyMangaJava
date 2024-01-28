package com.shicheeng.copymanga.ui.screen.download

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.shicheeng.copymanga.R
import com.shicheeng.copymanga.ui.theme.ElevationTokens

@Composable
fun StateButton(
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    isPause: Boolean,
    onClick: () -> Unit,
) {

    val interaction = remember { MutableInteractionSource() }
    val isPressed by interaction.collectIsPressedAsState()
    val cornerSize by animateDpAsState(
        label = "state_button_size",
        targetValue = if (isPressed || isPressed) {
            size
        } else {
            12.dp
        }
    )

    Surface(
        onClick = onClick,
        tonalElevation = ElevationTokens.Level3,
        shape = RoundedCornerShape(cornerSize),
        interactionSource = interaction,
        modifier = modifier.size(size)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(
                    id = if (isPause) {
                        R.drawable.baseline_play_arrow_24
                    } else {
                        R.drawable.baseline_pause_24
                    }
                ),
                contentDescription = stringResource(id = if (isPause) R.string.resume else R.string.pause)
            )
        }
    }
}

/**
 * 圆角可变换的按钮
 * @param size 大小
 * @param id 图片资源id
 * @param contentDescription 辅助服务使用的文本来描述该图标所代表的含义。 应始终提供此图标，除非该图标用于装饰目的，并且不代表用户可以采取的有意义的操作。 该文本应该本地化，例如使用 [androidx.compose.ui.res.stringResource] 或类似。
 * @param onClick 点击事件回调
 */
@Composable
fun StateButton(
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    @DrawableRes id: Int,
    contentDescription: String?,
    onClick: () -> Unit,
) {

    val interaction = remember { MutableInteractionSource() }
    val isPressed by interaction.collectIsPressedAsState()
    val cornerSize by animateDpAsState(
        label = "state_button_size",
        targetValue = if (isPressed || isPressed) {
            size
        } else {
            12.dp
        }
    )

    Surface(
        onClick = onClick,
        tonalElevation = ElevationTokens.Level3,
        shape = RoundedCornerShape(cornerSize),
        interactionSource = interaction,
        modifier = modifier.size(size)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = id),
                contentDescription = contentDescription
            )
        }
    }
}