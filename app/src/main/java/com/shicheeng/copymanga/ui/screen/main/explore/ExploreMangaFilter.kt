package com.shicheeng.copymanga.ui.screen.main.explore

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.shicheeng.copymanga.R
import com.shicheeng.copymanga.data.MangaSortBean
import com.shicheeng.copymanga.json.MangaSortJson

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreFilter(
    modifier: Modifier = Modifier,
    showList: MutableMap<MangaSortJson, MangaSortBean?>,
    onThemeClick: () -> Unit,
    onOrderClick: () -> Unit,
    onTopClick: () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize(),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            FilterChip(
                onClick = onOrderClick,
                modifier = Modifier.animateContentSize(),
                label = {
                    Text(
                        text = showList[MangaSortJson.ORDER]?.pathName
                            ?: stringResource(id = R.string.order)
                    )
                },
                selected = showList[MangaSortJson.ORDER] != null,
                trailingIcon = {
                    Icon(
                        painter = painterResource(id = com.google.android.material.R.drawable.mtrl_ic_arrow_drop_down),
                        contentDescription = null
                    )
                }
            )
            FilterChip(
                onClick = onTopClick,
                modifier = Modifier.animateContentSize(),
                label = {
                    Text(
                        text = showList[MangaSortJson.PATH]?.pathName
                            ?: stringResource(id = R.string.top)
                    )
                },
                trailingIcon = {
                    Icon(
                        painter = painterResource(id = com.google.android.material.R.drawable.mtrl_ic_arrow_drop_down),
                        contentDescription = null
                    )
                },
                selected = showList[MangaSortJson.PATH] != null
            )
            FilterChip(
                onClick = onThemeClick,
                modifier = Modifier.animateContentSize(),
                label = {
                    Text(
                        text = showList[MangaSortJson.THEME]?.pathName
                            ?: stringResource(id = R.string.theme)
                    )
                },
                trailingIcon = {
                    Icon(
                        painter = painterResource(id = com.google.android.material.R.drawable.mtrl_ic_arrow_drop_down),
                        contentDescription = null
                    )
                },
                selected = showList[MangaSortJson.THEME] != null
            )
        }
    }
}

