package com.shicheeng.copymanga.ui.screen.manga

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.graphics.ExperimentalAnimationGraphicsApi
import androidx.compose.animation.graphics.res.animatedVectorResource
import androidx.compose.animation.graphics.res.rememberAnimatedVectorPainter
import androidx.compose.animation.graphics.vector.AnimatedImageVector
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shicheeng.copymanga.R
import com.shicheeng.copymanga.data.MangaSortBean
import com.shicheeng.copymanga.util.click
import kotlin.math.roundToInt


private val whitespaceLineRegex = Regex("[\\r\\n]{2,}", setOf(RegexOption.MULTILINE))

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MangaExpandSummary(
    modifier: Modifier = Modifier,
    defaultExpandState: Boolean,
    description: String?,
    tags: () -> List<MangaSortBean>?,
    onTagsClick: (MangaSortBean) -> Unit,
) {

    Column(modifier = modifier) {
        val (expand, onExpand) = rememberSaveable {
            mutableStateOf(defaultExpandState)
        }
        val desc = description.takeIf { !it.isNullOrBlank() }
            ?: stringResource(id = R.string.no_description)
        val trimmedDescription = remember(desc) {
            desc.replace(whitespaceLineRegex, "\n")
                .trimEnd()
        }
        MangaSummary(
            expandedDescription = desc,
            shrunkDescription = trimmedDescription,
            expanded = expand,
            modifier = Modifier
                .padding(top = 8.dp)
                .padding(horizontal = 16.dp)
                .click { onExpand(!expand) }
        )
        val tag = tags()
        if (tag != null) {
            FlowRow(
                modifier = Modifier.padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                tag.forEach {
                    SuggestionChip(
                        onClick = { onTagsClick(it) },
                        label = {
                            Text(text = it.pathName)
                        }
                    )
                }
            }
        }
    }

}

@OptIn(ExperimentalAnimationGraphicsApi::class)
@Composable
private fun MangaSummary(
    expandedDescription: String,
    shrunkDescription: String,
    expanded: Boolean,
    modifier: Modifier = Modifier,
) {
    var expandedHeight by remember { mutableIntStateOf(0) }
    var shrunkHeight by remember { mutableIntStateOf(0) }
    val heightDelta = remember(expandedHeight, shrunkHeight) { expandedHeight - shrunkHeight }
    val animProgress by animateFloatAsState(if (expanded) 1f else 0f)
    val scrimHeight = with(LocalDensity.current) { remember { 24.sp.roundToPx() } }

    SubcomposeLayout(modifier = modifier.clipToBounds()) { constraints ->
        val shrunkPlaceable = subcompose("description-s") {
            Text(
                text = "\n\n", // Shows at least 3 lines
                style = MaterialTheme.typography.bodyMedium,
            )
        }.map { it.measure(constraints) }
        shrunkHeight = shrunkPlaceable.maxByOrNull { it.height }?.height ?: 0

        val expandedPlaceable = subcompose("description-l") {
            Text(
                text = expandedDescription,
                style = MaterialTheme.typography.bodyMedium,
            )
        }.map { it.measure(constraints) }
        expandedHeight =
            expandedPlaceable.maxByOrNull { it.height }?.height?.coerceAtLeast(shrunkHeight) ?: 0

        val actualPlaceable = subcompose("description") {
            SelectionContainer {
                Text(
                    text = if (expanded) expandedDescription else shrunkDescription,
                    maxLines = Int.MAX_VALUE,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.alpha(.78f),
                )
            }
        }.map { it.measure(constraints) }

        val scrimPlaceable = subcompose("scrim") {
            val colors = listOf(Color.Transparent, MaterialTheme.colorScheme.background)
            Box(
                modifier = Modifier.background(Brush.verticalGradient(colors = colors)),
                contentAlignment = Alignment.Center,
            ) {
                val image = AnimatedImageVector.animatedVectorResource(R.drawable.ic_arrow_avd)
                Icon(
                    painter = rememberAnimatedVectorPainter(image, !expanded),
                    contentDescription = stringResource(if (expanded) R.string.manga_info_collapse else R.string.manga_info_expand),
                    tint = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.background(Brush.radialGradient(colors = colors.asReversed())),
                )
            }
        }.map { it.measure(Constraints.fixed(width = constraints.maxWidth, height = scrimHeight)) }

        val currentHeight = shrunkHeight + ((heightDelta + scrimHeight) * animProgress).roundToInt()
        layout(constraints.maxWidth, currentHeight) {
            actualPlaceable.forEach {
                it.place(0, 0)
            }

            val scrimY = currentHeight - scrimHeight
            scrimPlaceable.forEach {
                it.place(0, scrimY)
            }
        }
    }
}
