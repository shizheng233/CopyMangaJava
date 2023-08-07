package com.shicheeng.copymanga.ui.screen.main.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.interaction.DragInteraction
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.shicheeng.copymanga.data.DataBannerBean
import com.shicheeng.copymanga.util.click
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay


/**
 * 按照官方的例子抄的
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Banner(
    modifier: Modifier = Modifier,
    list: List<DataBannerBean>,
    click: (DataBannerBean) -> Unit,
) {
    val pageCount = list.size
    val startPage = Int.MAX_VALUE / 2
    val pageStateBanner = rememberPagerState(
        initialPage = startPage
    ) { Int.MAX_VALUE }

    fun pageMapper(index: Int): Int {
        return (index - startPage) floorMod pageCount
    }

    var underDragging by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(key1 = Unit) {
        pageStateBanner.interactionSource.interactions.collect { interaction ->
            when (interaction) {
                is PressInteraction.Press -> underDragging = true
                is PressInteraction.Release -> underDragging = false
                is PressInteraction.Cancel -> underDragging = false
                is DragInteraction.Start -> underDragging = true
                is DragInteraction.Stop -> underDragging = false
                is DragInteraction.Cancel -> underDragging = false
            }
        }
    }

    Box(modifier = modifier.fillMaxWidth()) {
        HorizontalPager(
            state = pageStateBanner,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) { index ->
            val page = pageMapper(index)
            BannerItem(dataBannerBean = list[page], bannerClick = click)
        }
        HorizontalPagerIndicator(
            pagerState = pageStateBanner,
            pageCount = pageCount,
            modifier = Modifier.align(Alignment.BottomCenter),
            pageIndexMapping = ::pageMapper
        )
    }

    if (underDragging.not()) {
        LaunchedEffect(key1 = underDragging) {
            try {
                while (true) {
                    delay(5000L)
                    val current = pageStateBanner.currentPage
                    val currentPos = pageMapper(current)
                    val nextPage = current + 1
                    if (underDragging.not()) {
                        val toPage = nextPage.takeIf { nextPage < pageStateBanner.pageCount }
                            ?: (currentPos + startPage + 1)
                        if (toPage > current) {
                            pageStateBanner.animateScrollToPage(toPage)
                        } else {
                            pageStateBanner.scrollToPage(toPage)
                        }
                    }
                }
            } catch (e: CancellationException) {
                e.printStackTrace()
            }
        }
    }

}

@Composable
fun BannerItem(
    dataBannerBean: DataBannerBean,
    bannerClick: (DataBannerBean) -> Unit,
) {
    val colors = listOf(
        MaterialTheme.colorScheme.surface,
        Color.Transparent,
        Color.Transparent,
        Color.Transparent,
        MaterialTheme.colorScheme.surface
    )
    val textBackgroundColor = MaterialTheme.colorScheme.surface
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .click { bannerClick.invoke(dataBannerBean) }
    ) {
        AsyncImage(
            model = dataBannerBean.bannerImageUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            placeholder = ColorPainter(MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .matchParentSize()
                .drawWithContent {
                    drawContent()
                    drawRect(brush = Brush.verticalGradient(colors))
                }
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomStart)
                .padding(bottom = 16.dp, start = 16.dp, end = 16.dp)
        ) {
            Text(
                text = dataBannerBean.bannerBrief,
                modifier = Modifier

                    .drawWithContent {
                        drawRect(color = textBackgroundColor)
                        drawContent()
                    }
                    .padding(2.dp),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

private infix fun Int.floorMod(other: Int): Int = when (other) {
    0 -> this
    else -> this - floorDiv(other) * other
}
