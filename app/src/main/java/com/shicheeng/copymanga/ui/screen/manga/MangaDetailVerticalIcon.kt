package com.shicheeng.copymanga.ui.screen.manga

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.shicheeng.copymanga.util.click

@Composable
fun VerticalIcon(
    modifier: Modifier = Modifier,
    @DrawableRes iconId: () -> Int,
    text: String,
    click: () -> Unit,
) {
    Column(
        modifier = modifier.click { click() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(id = iconId()),
            contentDescription = text,
            modifier = Modifier.padding(4.dp),
            tint = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = text,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(horizontal = 4.dp),
            style = MaterialTheme.typography.labelMedium
        )
    }
}

@Composable
fun VerticalIcon(
    modifier: Modifier = Modifier,
    @DrawableRes iconId: () -> Int,
    text: String,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(id = iconId()),
            contentDescription = text,
            modifier = Modifier.padding(4.dp),
            tint = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = text,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(horizontal = 4.dp),
            style = MaterialTheme.typography.labelMedium
        )
    }
}