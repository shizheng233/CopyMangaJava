package com.shicheeng.copymanga.ui.screen.compoents

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.shicheeng.copymanga.R

@Composable
fun EmptyDataScreen(
    modifier: Modifier = Modifier,
    tipText: String = stringResource(id = R.string.no_content),
    isEmpty: Boolean,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = modifier.fillMaxSize(),
    ) {
        if (isEmpty) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.undraw_no_data_re_kwbl),
                        contentDescription = null,
                    )
                    Text(text = tipText)
                }
            }
        } else {
            content()
        }
    }
}

@Composable
fun EmptyDataScreen(
    modifier: Modifier = Modifier,
    tipText: String = stringResource(id = R.string.no_content),
) {

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.undraw_no_data_re_kwbl),
                contentDescription = null,
            )
            Text(text = tipText)
        }
    }
}
