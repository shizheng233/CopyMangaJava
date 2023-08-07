package com.shicheeng.copymanga.ui.screen.setting

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.core.view.HapticFeedbackConstantsCompat
import com.shicheeng.copymanga.R

/**
 * 使用compose写的设置界面。基本构件，表示在首选项层次结构中向用户显示的单个设置。处理最简单的点击事件。
 *
 * @param title 使用 String 设置此首选项的标题。
 * @param summary 使用 String 设置此首选项的摘要。
 * @param leaderIconRes 使用 DrawableResID 设置此首选项的图标。
 * @param onClick 设置单击此首选项时要调用的回调。
 */
@Composable
fun Preference(
    modifier: Modifier = Modifier,
    title: String,
    summary: String,
    @DrawableRes leaderIconRes: Int?,
    onClick: () -> Unit,
) {
    ListItem(
        headlineContent = {
            Text(text = title)
        },
        supportingContent = {
            Text(text = summary)
        },
        leadingContent = {
            if (leaderIconRes != null) {
                Icon(
                    painter = painterResource(id = leaderIconRes),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(preferenceSpace)
                        .size(24.dp),
                )
            } else {
                Spacer(modifier = Modifier.size(preferenceSpaceSize))
            }
        },
        modifier = modifier
            .clickable { onClick() },
    )
}

/**
 * 使用compose写的将条目列表显示为对话框的首选项。该首选项保存一个字符串值，该控件在点击时回弹出一个对话框来让用户选择与[array]对应的[arrayValue]，二者下标对应。
 *
 * @param dialogSupportedText 必传参数，用于显示对话框的介绍
 * @param title 必传参数，使用 String 设置此首选项的标题
 * @param summary 必传参数，使用 String 设置此首选项的摘要
 * @param array 必传参数，对话框的列表显示
 * @param selectValue 必传参数，对话框的被选择值
 * @param arrayValue 必传参数，与[array]下标对应的值
 * @param leaderIconRes 必传参数，使用 DrawableResID 设置此首选项的图标
 * @param onItemClick 点击列表的回调事件
 */
@Composable
fun ListPreference(
    title: String,
    summary: String,
    dialogSupportedText: String,
    array: Array<String>,
    selectValue: String,
    arrayValue: Array<String>,
    @DrawableRes leaderIconRes: Int,
    onItemClick: (Int, Array<String>) -> Unit,
) {
    var isShow by remember { mutableStateOf(false) }
    Preference(title = title, summary = summary, leaderIconRes = leaderIconRes) {
        isShow = true
    }
    if (isShow) {
        SelectionDialog(
            title = title,
            summaryText = dialogSupportedText,
            array = array,
            selectValue = selectValue,
            iconRes = leaderIconRes,
            onItemClick = {
                onItemClick(it, arrayValue)
                isShow = false
            },
            onCancel = {
                isShow = false
            }
        ) {
            isShow = false
        }
    }
}

/**
 * 使用compose写的将条目列表显示为对话框的首选项。该首选项保存一个字符串值，该控件在点击时回弹出一个对话框来让用户选择[mapValue]里面的值。
 *
 * @param dialogSupportedText 必传参数，用于显示对话框的介绍
 * @param title 必传参数，使用 String 设置此首选项的标题
 * @param summaryProvider 必传参数，使用 String 设置此首选项的摘要
 * @param mapValue 必传参数，对话框的列表显示，选择的为[Map.keys]，显示的为[Map.values]
 * @param selectValue 必传参数，对话框的被选择值
 * @param leaderIconRes 必传参数，使用 DrawableResID 设置此首选项的图标
 * @param onOK 点击列表下面的按钮的回调事件
 */
@Composable
fun MutableSelectListPreference(
    title: String,
    dialogSupportedText: String,
    mapValue: Map<String, String>,
    summaryProvider: @Composable (Map<String, String>) -> String,
    selectValue: Set<String>,
    @DrawableRes leaderIconRes: Int,
    onOK: (Set<String>) -> Unit,
) {
    var isShow by remember { mutableStateOf(false) }

    Preference(
        title = title,
        summary = summaryProvider(mapValue),
        leaderIconRes = leaderIconRes
    ) {
        isShow = true
    }
    if (isShow) {
        MutableSelectedDialog(
            title = title,
            summaryText = dialogSupportedText,
            mapValue = mapValue,
            selectValues = selectValue,
            iconRes = leaderIconRes,
            onCancel = {
                isShow = false
            },
            onOk = {
                onOK(it)
                isShow = false
            }
        ) {
            isShow = false
        }
    }
}

/**
 * 使用compose写的开关设置组件，提供双状态可切换选项的首选项。
 *
 * @param title 使用 String 设置此首选项的标题。
 * @param summary 使用 String 设置此首选项的摘要。
 * @param leaderIconRes 使用 DrawableResID 设置此首选项的图标。
 * @param onClick 设置单击此首选项时要调用的回调。
 * @param selectValue 开关的选择
 */
@Composable
fun SwitchPreference(
    title: String,
    summary: String,
    selectValue: Boolean,
    @DrawableRes leaderIconRes: Int?,
    onClick: (Boolean) -> Unit,
) {
    val mutableInteractionSource = remember {
        MutableInteractionSource()
    }
    val hapticFeedbackConstants = LocalHapticFeedback.current
    ListItem(
        headlineContent = {
            Text(text = title)
        },
        supportingContent = {
            Text(text = summary)
        },
        trailingContent = {
            Switch(
                checked = selectValue,
                onCheckedChange = onClick,
                interactionSource = mutableInteractionSource,
            )
        },
        leadingContent = {
            if (leaderIconRes != null) {
                Icon(
                    painter = painterResource(id = leaderIconRes),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(preferenceSpace)
                        .size(24.dp),
                )
            } else {
                Spacer(modifier = Modifier.size(preferenceSpaceSize))
            }
        },
        modifier = Modifier.toggleable(
            value = selectValue,
            interactionSource = mutableInteractionSource,
            indication = LocalIndication.current,
            role = Role.Switch
        ) {
            onClick(it)
            hapticFeedbackConstants.performHapticFeedback(
                hapticFeedbackType = HapticFeedbackType(
                    value = if (it) {
                        HapticFeedbackConstantsCompat.TOGGLE_ON
                    } else {
                        HapticFeedbackConstantsCompat.TOGGLE_OFF
                    }
                )
            )

        }
    )
}

/**
 * 使用compose写的提示首选项，处理类似于[SwitchPreference]或者[Preference]这种有着需要被注意的细节问题。
 *
 * **注意**：不能用来写类似简介的文字，这个应该**只用来**提示用户。
 *
 * @param supportText 必传参数，显示的文字。
 */
@Composable
fun TipPreference(
    supportText: String,
) {
    ListItem(
        headlineContent = {
            Row(
                Modifier.padding(start = 16.dp + 48.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_manga_info_main),
                    contentDescription = null
                )
                Text(
                    text = supportText,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(start = 8.dp),
                )
            }
        }
    )
}

/**
 * 使用compose写的将输入框显示为对话框的首选项。该首选项保存一个字符串值，该控件在点击时回弹出一个对话框来让用户选择输入文字。
 *
 * @param dialogSupportedText 必传参数，用于显示对话框的介绍
 * @param title 必传参数，使用 String 设置此首选项的标题
 * @param summary 必传参数，使用 String 设置此首选项的摘要
 * @param originalValue 必传参数，原始数据
 * @param leaderIconRes 必传参数，使用 DrawableResID 设置此首选项的图标。
 * @param onInput 必传参数，输入确认的回调
 */
@Composable
fun EditTextPreference(
    modifier: Modifier = Modifier,
    title: String,
    summary: String,
    dialogSupportedText: String,
    originalValue: String,
    @DrawableRes leaderIconRes: Int,
    onInput: (String) -> Unit,
) {
    var isShow by remember { mutableStateOf(false) }
    Preference(
        modifier = modifier,
        title = title,
        summary = summary,
        leaderIconRes = leaderIconRes
    ) {
        isShow = true
    }
    if (isShow) {
        EditTextDialog(
            title = title,
            summaryText = dialogSupportedText,
            originalValue = originalValue,
            onDone = {
                onInput(it)
                isShow = false
            }
        ) {
            isShow = false
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
fun LazyListScope.groupText(
    @StringRes text: Int,
) {
    stickyHeader(
        key = text
    ) {
        Text(
            text = stringResource(id = text),
            Modifier
                .padding(16.dp)
                .padding(start = 48.dp + 16.dp),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

private val preferenceSpace = PaddingValues(all = 12.dp)
private val preferenceSpaceSize = 48.dp