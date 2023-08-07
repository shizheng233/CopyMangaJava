package com.shicheeng.copymanga.ui.screen.setting

import androidx.annotation.DrawableRes
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.shicheeng.copymanga.R

@Composable
fun SelectionDialog(
    title: String,
    summaryText: String?,
    @DrawableRes iconRes: Int = R.drawable.baseline_format_list_bulleted_24,
    array: Array<String>,
    selectValue: String,
    onItemClick: (Int) -> Unit,
    onCancel: () -> Unit,
    onDismissListener: () -> Unit,
) = Dialog(
    onDismissRequest = onDismissListener
) {
    Surface(
        shape = RoundedCornerShape(28.dp),
        tonalElevation = 6.dp,
        modifier = Modifier.widthIn(min = 280.dp, max = 560.dp)
    ) {
        Column(
            modifier = Modifier.padding(all = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = null
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
            )
            if (summaryText != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = summaryText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Divider()
            array.forEach {
                val interactionSource = remember {
                    MutableInteractionSource()
                }
                Row(
                    Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .selectable(
                            selected = (it == selectValue),
                            onClick = { onItemClick(array.indexOf(it)) },
                            role = Role.RadioButton,
                            interactionSource = interactionSource,
                            indication = LocalIndication.current
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = (it == selectValue),
                        onClick = { onItemClick(array.indexOf(it)) },
                        interactionSource = interactionSource
                    )
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }
            }
            Divider()
            Spacer(modifier = Modifier.height(24.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = onCancel
                ) {
                    Text(text = stringResource(id = android.R.string.cancel))
                }
            }
        }
    }
}

@Composable
fun EditTextDialog(
    title: String,
    summaryText: String,
    originalValue: String,
    onDone: (String) -> Unit,
    onDismissListener: () -> Unit,
) = Dialog(
    onDismissRequest = onDismissListener,
) {

    var inputValue by remember {
        mutableStateOf(originalValue)
    }

    Surface(
        shape = RoundedCornerShape(28.dp),
        tonalElevation = 6.dp,
        modifier = Modifier.widthIn(min = 280.dp, max = 560.dp)
    ) {
        Column(
            modifier = Modifier.padding(all = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = painterResource(id = R.drawable.outline_input_24),
                contentDescription = null
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = summaryText,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = inputValue,
                onValueChange = {
                    inputValue = it
                },
                placeholder = {
                    Text(text = stringResource(R.string.input))
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onDone = { onDone(inputValue) }),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )
            Spacer(modifier = Modifier.height(24.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = {
                        onDone(inputValue)
                    }
                ) {
                    Text(text = stringResource(id = android.R.string.ok))
                }
            }
        }
    }
}

@Composable
fun MutableSelectedDialog(
    title: String,
    summaryText: String?,
    @DrawableRes iconRes: Int = R.drawable.baseline_format_list_bulleted_24,
    mapValue: Map<String, String>,
    selectValues: Set<String>,
    onOk: (Set<String>) -> Unit,
    onCancel: () -> Unit,
    onDismissListener: () -> Unit,
) = Dialog(
    onDismissRequest = onDismissListener
) {
    val array = remember {
        mapValue.keys.filter {
            selectValues.contains(it)
        }.toMutableStateList()
    }
    Surface(
        shape = RoundedCornerShape(28.dp),
        tonalElevation = 6.dp,
        modifier = Modifier.widthIn(min = 280.dp, max = 560.dp)
    ) {
        Column(
            modifier = Modifier.padding(all = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = null
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
            )
            if (summaryText != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = summaryText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Divider()
            mapValue.forEach { current ->
                val interactionSource = remember {
                    MutableInteractionSource()
                }
                val isSelected = array.contains(current.key)
                Row(
                    Modifier
                        .selectable(
                            selected = isSelected,
                            onClick = {
                                if (!isSelected) array.add(current.key)
                                else array.remove(current.key)
                            },
                            interactionSource = interactionSource,
                            role = Role.Checkbox,
                            indication = LocalIndication.current
                        )
                        .fillMaxWidth()
                        .height(56.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isSelected,
                        onCheckedChange = {
                            if (!isSelected) array.add(current.key)
                            else array.remove(current.key)
                        },
                        interactionSource = interactionSource
                    )
                    Text(
                        text = current.value,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }
            }
            Divider()
            Spacer(modifier = Modifier.height(24.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = onCancel
                ) {
                    Text(text = stringResource(id = android.R.string.cancel))
                }
                Spacer(modifier = Modifier.width(8.dp))
                TextButton(
                    onClick = {
                        onOk(array.toMutableSet())
                    }
                ) {
                    Text(text = stringResource(id = android.R.string.ok))
                }
            }
        }
    }
}
