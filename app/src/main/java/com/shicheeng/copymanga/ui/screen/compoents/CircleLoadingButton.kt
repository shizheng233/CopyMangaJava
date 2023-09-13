package com.shicheeng.copymanga.ui.screen.compoents

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.shicheeng.copymanga.R
import com.shicheeng.copymanga.ui.theme.ElevationTokens

@Composable
fun CircleLoadingButton(
    modifier: Modifier = Modifier,
    isLoading: Boolean,
    onClick: () -> Unit,
    tonalElevation: Dp = ElevationTokens.Level3,
) {
    Surface(
        onClick = onClick,
        shape = CircleShape,
        modifier = modifier.size(68.dp),
        tonalElevation = tonalElevation
    ) {
        AnimatedContent(
            targetState = isLoading,
            label = "circle loading"
        ) {
            if (it) {
                CircularProgressIndicator(
                    modifier = Modifier.padding(16.dp)
                )
            } else {
                Icon(
                    painter = painterResource(id = R.drawable.undraw_arrow),
                    contentDescription = stringResource(id = R.string.login_text),
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}
