package com.fstengineering.leasilycrm.profile.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.presentation.composables.AppTextField
import com.fstengineering.leasilycrm.profile.models.EditUserErrorUiState
import com.fstengineering.leasilycrm.profile.models.UserUiState
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun EditUserDialog(
    userUiState: UserUiState,
    onUserUiStateChange: (UserUiState) -> Unit,
    editUserErrorUiState: EditUserErrorUiState,
    onEdit: (UserUiState) -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val nameFieldFocusRequester = remember { FocusRequester() }
    val creciNumberFieldFocusRequester = remember { FocusRequester() }

    var currentUserUiState by remember(userUiState) { mutableStateOf(userUiState) }

    LaunchedEffect(Unit) {
        delay(250.milliseconds)
        nameFieldFocusRequester.requestFocus()
    }

    AlertDialog(
        title = {
            Text(
                text = stringResource(R.string.profile_edit_user_dialog_headline),
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.headlineSmall,
            )
        },
        text = {
            Column {
                AppTextField(
                    label = stringResource(R.string.profile_edit_user_dialog_name_field_placeholder),
                    value = currentUserUiState.name ?: "",
                    onValueChange = { nameInput ->
                        currentUserUiState = currentUserUiState.copy(
                            name = nameInput,
                        )
                        onUserUiStateChange(userUiState.copy(name = nameInput))
                    },
                    isInvalid = editUserErrorUiState.nameError != null,
                    supportingText = stringResource(
                        editUserErrorUiState.nameError
                            ?: R.string.profile_edit_user_dialog_required_helper_text,
                    ),
                    maxLength = 75,
                    onDone = { creciNumberFieldFocusRequester.requestFocus() },
                    fieldFocusRequester = nameFieldFocusRequester,
                )
                Spacer(modifier = Modifier.height(16.dp))
                AppTextField(
                    label = stringResource(R.string.profile_edit_user_dialog_creci_field_placeholder),
                    value = currentUserUiState.creciNumber ?: "",
                    onValueChange = { creciNumberInput ->
                        currentUserUiState = currentUserUiState.copy(
                            creciNumber = creciNumberInput,
                        )
                        onUserUiStateChange(userUiState.copy(creciNumber = creciNumberInput))
                    },
                    isInvalid = editUserErrorUiState.creciNumberError != null,
                    supportingText = if (editUserErrorUiState.creciNumberError != null) {
                        stringResource(editUserErrorUiState.creciNumberError)
                    } else null,
                    maxLength = 8,
                    keyboardType = KeyboardType.Number,
                    onDone = { onEdit(userUiState) },
                    fieldFocusRequester = creciNumberFieldFocusRequester,
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onEdit(currentUserUiState) },
            ) {
                Text(
                    text = stringResource(R.string.edit_action_button_text),
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.labelLarge,
                )
            }
        },
        dismissButton = {
            TextButton(onClick = { onCancel() }) {
                Text(
                    text = stringResource(R.string.cancel_action_button_text),
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.labelLarge,
                )
            }
        },
        onDismissRequest = {},
        shape = MaterialTheme.shapes.extraLarge,
        modifier = modifier
            .width(450.dp)
    )
}
