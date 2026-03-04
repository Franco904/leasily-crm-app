package com.fstengineering.leasilycrm.assistanceRegistration.presentation.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.presentation.composables.textFields.NeighborhoodFormField
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun AssistancePreferredNeighborhoodRegistrationDialog(
    neighborhood: String,
    onNeighborhoodChange: (String) -> Unit,
    error: Int?,
    pastNeighborhoods: ImmutableList<String>,
    isEditing: Boolean,
    onSave: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val neighborhoodFieldFocusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        delay(250.milliseconds)
        neighborhoodFieldFocusRequester.requestFocus()
    }

    AlertDialog(
        title = {
            Text(
                text = stringResource(
                    when {
                        isEditing -> R.string.assistance_registration_searched_neighborhood_edit_dialog_headline
                        else -> R.string.assistance_registration_searched_neighborhood_save_dialog_headline
                    }
                ),
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.headlineSmall,
            )
        },
        text = {
            Column {
                NeighborhoodFormField(
                    value = neighborhood,
                    onValueChange = { onNeighborhoodChange(it) },
                    pastNeighborhoods = pastNeighborhoods,
                    error = error,
                    fieldFocusRequester = neighborhoodFieldFocusRequester,
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onSave() },
            ) {
                Text(
                    text = stringResource(
                        if (isEditing) R.string.edit_action_button_text
                        else R.string.save_action_button_text,
                    ),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        },
        dismissButton = {
            TextButton(onClick = { onCancel() }) {
                Text(
                    text = stringResource(R.string.cancel_action_button_text),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        },
        onDismissRequest = {},
        shape = MaterialTheme.shapes.extraLarge,
        modifier = modifier
            .width(450.dp)
    )
}
