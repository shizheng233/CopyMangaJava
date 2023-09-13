package com.shicheeng.copymanga.ui.screen.main.home.search

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import com.shicheeng.copymanga.R
import com.shicheeng.copymanga.ui.screen.compoents.PlainButton
import soup.compose.material.motion.MaterialFade

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FullScreenSearchViewHeader(
    modifier: Modifier = Modifier,
    topAppBarScrollBehavior: TopAppBarScrollBehavior,
    value: String,
    valueChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    onBackClick: () -> Unit,
    onClearClick: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        TopAppBar(
            modifier = modifier.fillMaxWidth(),
            title = {
                BasicTextField(
                    value = value,
                    onValueChange = valueChange,
                    singleLine = true,
                    modifier = Modifier,
                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    keyboardActions = KeyboardActions(
                        onSearch = {
                            onSearch(value)
                        }
                    ),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    cursorBrush = SolidColor(value = MaterialTheme.colorScheme.secondary)
                ) { innerTextField ->
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        innerTextField()
                        MaterialFade(visible = value.isBlank()) {
                            Text(
                                text = stringResource(id = R.string.search_text),
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }
            },
            navigationIcon = {
                PlainButton(
                    id = R.string.back_to_up,
                    drawableRes = R.drawable.ic_arrow_back,
                    onButtonClick = onBackClick
                )
            },
            actions = {
                MaterialFade(visible = value.isNotEmpty()) {
                    PlainButton(
                        id = com.google.android.material.R.string.clear_text_end_icon_content_description,
                        drawableRes = R.drawable.baseline_close_24,
                        onButtonClick = onClearClick
                    )
                }
            },
            scrollBehavior = topAppBarScrollBehavior
        )
        HorizontalDivider(color = MaterialTheme.colorScheme.outline)
    }
}

