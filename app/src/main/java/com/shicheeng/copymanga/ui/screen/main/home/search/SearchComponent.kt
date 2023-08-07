package com.shicheeng.copymanga.ui.screen.main.home.search

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.shicheeng.copymanga.R
import com.shicheeng.copymanga.ui.screen.compoents.PlainButton
import soup.compose.material.motion.MaterialFade

@Composable
fun FullScreenSearchView(
    modifier: Modifier = Modifier,
    value: String,
    valueChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    onBackClick: () -> Unit,
    onClearClick: () -> Unit,
) {
    Surface(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.height(72.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                PlainButton(
                    id = R.string.back_to_up,
                    drawableRes = R.drawable.ic_arrow_back,
                    tint = MaterialTheme.colorScheme.onSurface,
                    onButtonClick = onBackClick
                )
                BasicTextField(
                    value = value,
                    onValueChange = valueChange,
                    singleLine = true,
                    modifier = Modifier
                        .weight(1f)
                        .align(Alignment.CenterVertically),
                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    keyboardActions = KeyboardActions(
                        onSearch = {
                            onSearch(value)
                        }
                    ),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search)
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
                MaterialFade(visible = value.isNotEmpty()) {
                    PlainButton(
                        id = com.google.android.material.R.string.clear_text_end_icon_content_description,
                        drawableRes = R.drawable.baseline_close_24,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        onButtonClick = onClearClick
                    )
                }
            }
            androidx.compose.material3.Divider(color = MaterialTheme.colorScheme.outline)
        }
    }
}

