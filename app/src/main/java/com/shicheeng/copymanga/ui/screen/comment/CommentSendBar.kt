package com.shicheeng.copymanga.ui.screen.comment

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.shicheeng.copymanga.R
import com.shicheeng.copymanga.data.commentpush.CommentPushDataModel
import com.shicheeng.copymanga.ui.theme.ElevationTokens
import com.shicheeng.copymanga.util.SendUIState
import soup.compose.material.motion.animation.materialFadeThroughIn
import soup.compose.material.motion.animation.materialFadeThroughOut

@Composable
fun CommentSendBar(
    modifier: Modifier = Modifier,
    value: String,
    isExpired:Boolean,
    onValueChange: (String) -> Unit,
    sendUIState: SendUIState<out CommentPushDataModel>,
    onSend: () -> Unit,
) {
    Surface(
        tonalElevation = ElevationTokens.Level4,
        shadowElevation = ElevationTokens.Level2,
        modifier = modifier
            .zIndex(1f)
    ) {
        Column(
            modifier = Modifier
                .padding(
                    bottom = 8.dp,
                    top = 8.dp,
                    end = 16.dp,
                    start = 16.dp
                )
                .navigationBarsPadding()
                .imePadding()
                .animateContentSize()
        ) {
            Text(
                text = stringResource(R.string.send_comment_bar_title),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier
            ) {
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    modifier = Modifier
                        .weight(1f)
                        .defaultMinSize(
                            minWidth = ButtonDefaults.MinWidth,
                            minHeight = ButtonDefaults.MinHeight
                        ),
                    textStyle = MaterialTheme.typography.bodyMedium
                        .copy(
                            color = MaterialTheme.colorScheme.onSurface
                        )
                ) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        tonalElevation = ElevationTokens.Level0,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    ) {
                        Box(
                            modifier = Modifier.padding(
                                horizontal = 16.dp,
                                vertical = 8.dp
                            ),
                            contentAlignment = Alignment.CenterStart,
                        ) {
                            it()
                            this@Row.AnimatedVisibility(
                                visible = value.isEmpty(),
                                enter = materialFadeThroughIn(),
                                exit = materialFadeThroughOut()
                            ) {
                                Text(
                                    text = stringResource(R.string.type_send_content),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                FilledTonalButton(
                    onClick = onSend,
                    enabled = sendUIState == SendUIState.Idle && !isExpired
                ) {
                    when (sendUIState) {

                        is SendUIState.Error -> {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_close_24),
                                contentDescription = null
                            )
                        }

                        SendUIState.Idle -> {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_send_24),
                                contentDescription = null
                            )
                        }

                        SendUIState.Loading -> {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp))
                        }

                        is SendUIState.Success -> {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_done_all),
                                contentDescription = null
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = stringResource(R.string.send_comment))
                }
            }
        }
    }
}

