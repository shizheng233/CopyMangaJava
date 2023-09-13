package com.shicheeng.copymanga.ui.screen.main.explore

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.shicheeng.copymanga.R
import com.shicheeng.copymanga.data.MangaSortBean
import com.shicheeng.copymanga.json.MangaSortJson
import com.shicheeng.copymanga.ui.screen.compoents.dimensionAttribute

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreFilterBottomSheet(
    title: String,
    list: List<MangaSortBean>,
    sortBean: MangaSortBean?,
    onSelected: (MangaSortBean) -> Unit,
) {
    Scaffold(
        modifier = Modifier.fillMaxWidth(),
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Text(text = title)
                    }
                )
                HorizontalDivider()
            }
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .padding(24.dp),
            contentPadding = it
        ) {
            itemsIndexed(list) { index, item ->
                ExploreFilterBottomSheetItem(
                    msb = item,
                    isSelected = sortBean?.pathWord == item.pathWord,
                    onSelected = onSelected
                )
            }
        }
    }
}

@Composable
private fun ExploreFilterBottomSheetItem(
    modifier: Modifier = Modifier,
    msb: MangaSortBean,
    isSelected: Boolean,
    onSelected: (MangaSortBean) -> Unit,
) {

    Column(
        modifier = modifier
            .clip(shape = CircleShape)
            .selectable(
                isSelected,
                onClick = { onSelected(msb) },
                role = Role.Button
            )
            .background(
                if (isSelected) MaterialTheme.colorScheme.primary.copy(.22f)
                else Color.Transparent,
            )
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .height(height = 56.dp)
                .padding(
                    start = dimensionAttribute(attrResId = android.R.attr.listPreferredItemPaddingStart),
                    end = dimensionAttribute(attrResId = android.R.attr.listPreferredItemPaddingEnd)
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AnimatedVisibility(
                visible = isSelected,
                enter = expandHorizontally(expandFrom = Alignment.Start),
                exit = shrinkHorizontally(shrinkTowards = Alignment.Start)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_done_24),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(end = 16.dp)
                )
                Spacer(modifier = Modifier.width(width = dimensionAttribute(attrResId = android.R.attr.listPreferredItemPaddingStart)))
            }
            Text(
                text = msb.pathName,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.titleSmall
            )
        }
    }
}

private fun roundedCornerShapeWith(isLast: Boolean, isTop: Boolean, size: Dp): RoundedCornerShape {
    return when {
        isTop -> RoundedCornerShape(
            topStart = size,
            topEnd = size,
            bottomEnd = 0.dp,
            bottomStart = 0.dp
        )

        isLast -> RoundedCornerShape(
            topStart = 0.dp,
            topEnd = 0.dp,
            bottomEnd = size,
            bottomStart = size
        )

        else -> RoundedCornerShape(0.dp)
    }
}
