package com.alimapps.senbombardir.ui.screen.update

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.alimapps.senbombardir.R

@Composable
fun AppUpdateDialog(
    onSkip: () -> Unit,
    onUpdate: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = { onSkip() },
        title = {
            Text(
                text = stringResource(R.string.update_required_title),
                style = MaterialTheme.typography.titleMedium,
            )
        },
        text = {
            Text(
                text = stringResource(R.string.update_required_message),
                style = MaterialTheme.typography.bodyMedium,
            )
        },
        confirmButton = {
            Button(onClick = onUpdate) {
                Text(text = stringResource(R.string.update_button_update))
            }
        },
        dismissButton = {
            TextButton(onClick = onSkip) {
                Text(text = stringResource(R.string.update_button_skip))
            }
        },
        containerColor = MaterialTheme.colorScheme.surface,
    )
}
