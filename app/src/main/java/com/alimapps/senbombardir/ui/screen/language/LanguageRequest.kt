package com.alimapps.senbombardir.ui.screen.language

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.alimapps.senbombardir.R

/** Longest text sent to analytics: a language name, not a message. */
const val LANGUAGE_REQUEST_MAX_LENGTH = 40

/** Texts of the "Missing your language?" block. */
data class LanguageRequestTexts(
    val question: String,
    val button: String,
    val dialogTitle: String,
    val hint: String,
    val send: String,
    val cancel: String,
    val thanks: String,
)

/** Texts in the app language (settings: the language is already chosen). */
@Composable
fun localizedLanguageRequestTexts() = LanguageRequestTexts(
    question = stringResource(R.string.language_request_question),
    button = stringResource(R.string.language_request_button),
    dialogTitle = stringResource(R.string.language_request_dialog_title),
    hint = stringResource(R.string.language_request_hint),
    send = stringResource(R.string.language_request_send),
    cancel = stringResource(R.string.language_request_cancel),
    thanks = stringResource(R.string.language_request_thanks),
)

/**
 * "Missing your language? Request it" at the bottom of a language list.
 * Opens a dialog with a text field; the entered language goes to [onSubmit] (analytics only).
 */
@Composable
fun LanguageRequestBlock(
    texts: LanguageRequestTexts,
    onSubmit: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    var showDialog by rememberSaveable { mutableStateOf(false) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxWidth(),
    ) {
        Text(
            text = texts.question,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.labelSmall,
            textAlign = TextAlign.Center,
        )
        TextButton(onClick = { showDialog = true }) {
            Text(
                text = texts.button,
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.labelLarge,
                textAlign = TextAlign.Center,
            )
        }
    }

    if (showDialog) {
        LanguageRequestDialog(
            texts = texts,
            onDismiss = { showDialog = false },
            onSend = { language ->
                showDialog = false
                onSubmit(language)
                Toast.makeText(context, texts.thanks, Toast.LENGTH_SHORT).show()
            },
        )
    }
}

@Composable
private fun LanguageRequestDialog(
    texts: LanguageRequestTexts,
    onDismiss: () -> Unit,
    onSend: (String) -> Unit,
) {
    var value by rememberSaveable { mutableStateOf("") }
    val language = value.trim()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = texts.dialogTitle, style = MaterialTheme.typography.titleMedium)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = value,
                    onValueChange = { value = it.take(LANGUAGE_REQUEST_MAX_LENGTH) },
                    placeholder = {
                        Text(
                            text = texts.hint,
                            color = MaterialTheme.colorScheme.outline,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    },
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodyMedium,
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onSend(language) },
                enabled = language.isNotEmpty(),
            ) {
                Text(text = texts.send, style = MaterialTheme.typography.labelLarge)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = texts.cancel, style = MaterialTheme.typography.labelLarge)
            }
        },
        containerColor = MaterialTheme.colorScheme.surface,
    )
}
