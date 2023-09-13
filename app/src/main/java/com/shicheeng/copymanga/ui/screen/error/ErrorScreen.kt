package com.shicheeng.copymanga.ui.screen.error

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.shicheeng.copymanga.R

@Composable
fun ErrorScreen(
    message: String?,
    onFinishClick: () -> Unit,
) {

    val scrollState = rememberScrollState()
    val context = LocalContext.current

    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(all = 16.dp),
        ) {
            Text(
                text = stringResource(R.string.fatal_error),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = stringResource(R.string.try_to_send_to_me),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = message ?: stringResource(id = R.string.no_content),
                modifier = Modifier
                    .weight(1f)
                    .scrollable(
                        state = scrollState,
                        orientation = Orientation.Vertical
                    )
            )
            FilledTonalButton(
                onClick = {
                    context.composeEmail(
                        addresses = arrayOf("sshizzhi1234@gmail.com"),
                        body = message ?: ""
                    )
                },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text(text = stringResource(R.string.send))
            }
            FilledTonalButton(
                onClick = onFinishClick,
                modifier = Modifier.align(Alignment.End)
            ) {
                Text(text = stringResource(R.string.finish_this))
            }
        }

    }
}

fun Context.composeEmail(addresses: Array<String>, body: String) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "*/*"
        putExtra(Intent.EXTRA_EMAIL, addresses)
        putExtra(Intent.EXTRA_TEXT, body)
    }
    startActivity(intent)
}