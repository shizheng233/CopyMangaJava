package com.shicheeng.copymanga.ui.screen.compoents

import android.util.TypedValue
import androidx.annotation.AttrRes
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBarState
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.Dp
import com.shicheeng.copymanga.ui.theme.ElevationTokens

/**
 *
 */
@Composable
@ReadOnlyComposable
internal fun dimensionAttribute(
    @AttrRes attrResId: Int,
) = dimensionResource(TypedValue().apply {
    LocalContext.current.theme.resolveAttribute(
        attrResId,
        this,
        true
    )
}.resourceId)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun withAppBarColor(
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    topAppBarState: TopAppBarState,
): Color {
    val colorTransitionFraction = topAppBarState.overlappedFraction
    val fraction = if (colorTransitionFraction > 0.01f) 1f else 0f
    val appBarContainerColor by animateColorAsState(
        targetValue = lerp(
            start = backgroundColor,
            stop = MaterialTheme.colorScheme.applyTonalElevation(
                backgroundColor = backgroundColor,
                elevation = ElevationTokens.Level2
            ),
            fraction = FastOutLinearInEasing.transform(fraction)
        ),
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow)
    )
    return appBarContainerColor
}


fun ColorScheme.applyTonalElevation(backgroundColor: Color, elevation: Dp): Color {
    return if (backgroundColor == surface) {
        surfaceColorAtElevation(elevation)
    } else {
        backgroundColor
    }
}