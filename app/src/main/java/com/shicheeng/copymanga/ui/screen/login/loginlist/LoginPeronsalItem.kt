package com.shicheeng.copymanga.ui.screen.login.loginlist

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.shicheeng.copymanga.R
import com.shicheeng.copymanga.data.login.LocalLoginDataModel
import com.shicheeng.copymanga.ui.screen.main.personal.hostFor

@Composable
fun LoginPersonalSelection(
    modifier: Modifier = Modifier,
    isSelected: Boolean,
    localLoginDataModel: LocalLoginDataModel,
    onDelete: () -> Unit,
    onClick: () -> Unit,
) {

    val mutableInteraction = remember(::MutableInteractionSource)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .selectable(
                selected = isSelected,
                onClick = onClick,
                indication = LocalIndication.current,
                interactionSource = mutableInteraction
            )
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = isSelected,
            onClick = onClick,
            interactionSource = mutableInteraction
        )
        AsyncImage(
            model = hostFor(localLoginDataModel.avatarImageUrl),
            contentDescription = null,
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape),
            placeholder = ColorPainter(color = MaterialTheme.colorScheme.primary)
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 16.dp)
        ) {
            Text(
                text = localLoginDataModel.nikeName,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = localLoginDataModel.userName,
                style = MaterialTheme.typography.titleSmall
            )
        }
        IconButton(onClick = onDelete) {
            Icon(
                painter = painterResource(id = R.drawable.baseline_delete_24),
                contentDescription = stringResource(id = R.string.delete)
            )
        }
    }
}
